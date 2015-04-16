package com.stay.pull;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.yixun.R;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout {

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DURATION_MS = 190;
		static final int ANIMATION_FPS = 1000 / 60;

		private final Interpolator interpolator;
		private final int scrollToY;
		private final int scrollFromY;
		private final Handler handler;

		private boolean continueRunning = true;
		private long startTime = -1;
		private int currentY = -1;

		public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
			this.handler = handler;
			this.scrollFromY = fromY;
			this.scrollToY = toY;
			this.interpolator = new AccelerateDecelerateInterpolator();
		}

		@Override
		public void run() {

			/**
			 * Only set startTime if this is the first time we're starting, else
			 * actually calculate the Y delta
			 */
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - startTime))
						/ ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math
						.round((scrollFromY - scrollToY)
								* interpolator
										.getInterpolation(normalizedTime / 1000f));
				this.currentY = scrollFromY - deltaY;
				setHeaderScroll(currentY);
			}

			// If we're not at the target Y, keep going...
			if (continueRunning && scrollToY != currentY) {
				handler.postDelayed(this, ANIMATION_FPS);
			}
		}

		public void stop() {
			this.continueRunning = false;
			this.handler.removeCallbacks(this);
		}
	};

	// ===========================================================
	// Constants
	// ===========================================================

	static final float FRICTION = 2.0f;
	/**
	 * 向下拉动刷新
	 */
	static final int PULL_TO_DOWN_REFRESH = 0x0;
	/**
	 * 向上拉动刷新
	 */
	static final int PULL_TO_UP_REFRESH = 0x1;
	/**
	 * 释放刷新
	 */
	static final int RELEASE_TO_REFRESH = 0x2;
	static final int REFRESHING = 0x3;
	/**
	 * 手动刷新
	 */
	static final int MANUAL_REFRESHING = 0x4;

	/**
	 * 下拉刷新模式
	 */
	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
	/**
	 * 上拉刷新模式
	 */
	public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
	public static final int MODE_BOTH = 0x3;

	// ===========================================================
	// Fields
	// ===========================================================

	private int touchSlop;

	private float initialMotionY;
	private float lastMotionX;
	private float lastMotionY;
	private boolean isBeingDragged = false;

//	private int state = PULL_TO_DOWN_REFRESH;
	private int state;
	private int mode = MODE_PULL_DOWN_TO_REFRESH;
	private int currentMode;

	private boolean disableScrollingWhileRefreshing = true;

	T refreshableView;
	private boolean isPullToRefreshEnabled = true;

	private HeaderLayout headerLayout;
	private FooterLayout footerLayout;
	private int headerHeight;
	
	private int newHeight;

	private final Handler handler = new Handler();

	private OnHeaderRefreshListener onHeaderRefreshListener;
	private OnFooterRefreshListener onFooterRefreshListener;
	//提阿偶点击事件
	private OnItemClickListener onItemClickListener;
	private SmoothScrollRunnable currentSmoothScrollRunnable;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PullToRefreshBase(Context context) {
		super(context);
		init(context, null);
	}

	public PullToRefreshBase(Context context, int mode) {
		super(context);
		this.mode = mode;
		init(context, null);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Deprecated. Use {@link #getRefreshableView()} from now on.
	 * 
	 * @deprecated
	 * @return The Refreshable View which is currently wrapped
	 */
	public final T getAdapterView() {
		return refreshableView;
	}

	/**
	 * Get the Wrapped Refreshable View. Anything returned here has already been
	 * added to the content view.
	 * 
	 * @return The View which is currently wrapped
	 */
	public final T getRefreshableView() {
		return refreshableView;
	}

	/**
	 * Whether Pull-to-Refresh is enabled
	 * 
	 * @return enabled
	 */
	public final boolean isPullToRefreshEnabled() {
		return isPullToRefreshEnabled;
	}

	/**
	 * Returns whether the widget has disabled scrolling on the Refreshable View
	 * while refreshing.
	 * 
	 * @param true if the widget has disabled scrolling while refreshing
	 */
	public final boolean isDisableScrollingWhileRefreshing() {
		return disableScrollingWhileRefreshing;
	}

	/**
	 * Returns whether the Widget is currently in the Refreshing state
	 * 
	 * @return true if the Widget is currently refreshing
	 */
	public final boolean isRefreshing() {
		return state == REFRESHING || state == MANUAL_REFRESHING;
	}

	/**
	 * By default the Widget disabled scrolling on the Refreshable View while
	 * refreshing. This method can change this behaviour.
	 * 
	 * @param disableScrollingWhileRefreshing
	 *            - true if you want to disable scrolling while refreshing
	 */
	public final void setDisableScrollingWhileRefreshing(
			boolean disableScrollingWhileRefreshing) {
		this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
	}

	/**
	 * Mark the current Refresh as complete. Will Reset the UI and hide the
	 * Refreshing View
	 */
	public final void onHeaderRefreshComplete() {
		if (state != PULL_TO_DOWN_REFRESH) {
			resetHeader();
		}
	}
	public final void onFooterRefreshComplete() {
		if (state != PULL_TO_UP_REFRESH) {
			resetFooter();
		}
	}

	/**
	 * Set OnRefreshListener for the Widget
	 * 
	 * @param listener
	 *            - Listener to be used when the Widget is set to Refresh
	 */
	public final void setOnHeaderRefreshListener(OnHeaderRefreshListener listener) {
		onHeaderRefreshListener = listener;
	}
	public final void setOnFooterRefreshListener(OnFooterRefreshListener listener) {
		onFooterRefreshListener = listener;
	}
	//设置i监听器
	public final void setOnClickListener(OnItemClickListener listener){
		onItemClickListener = listener;
	}
	/**
	 * A mutator to enable/disable Pull-to-Refresh for the current View
	 * 
	 * @param enable
	 *            Whether Pull-To-Refresh should be used
	 */
	public final void setPullToRefreshEnabled(boolean enable) {
		this.isPullToRefreshEnabled = enable;
	}

	/**
	 * Set Text to show when the Widget is being pulled, and will refresh when
	 * released
	 * 
	 * @param releaseLabel
	 *            - String to display
	 */
	public void setHeaderReleaseLabel(String releaseLabel) {
		if (null != headerLayout) {
			headerLayout.setReleaseLabel(releaseLabel);
		}

	}
	public void setFooterReleaseLabel(String releaseLabel) {

		if (null != footerLayout) {
			footerLayout.setReleaseLabel(releaseLabel);
		}
	}

	/**
	 * Set Text to show when the Widget is being Pulled
	 * 
	 * @param pullLabel
	 *            - String to display
	 */
	public void setHeaderPullLabel(String pullLabel) {
		if (null != headerLayout) {
			headerLayout.setPullLabel(pullLabel);
		}
//		if (null != footerLayout) {
//			footerLayout.setPullLabel(pullLabel);
//		}
	}
	public void setFooterPullLabel(String pullLabel) {
//		if (null != headerLayout) {
//			headerLayout.setPullLabel(pullLabel);
//		}
		if (null != footerLayout) {
			footerLayout.setPullLabel(pullLabel);
		}
	}

	/**
	 * Set Text to show when the Widget is refreshing
	 * 
	 * @param refreshingLabel
	 *            - String to display
	 */
	public void setHeaderRefreshingLabel(String refreshingLabel) {
		if (null != headerLayout) {
			headerLayout.setRefreshingLabel(refreshingLabel);
		}
//		if (null != footerLayout) {
//			footerLayout.setRefreshingLabel(refreshingLabel);
//		}
	}
	public void setFooterRefreshingLabel(String refreshingLabel) {
//		if (null != headerLayout) {
//			headerLayout.setRefreshingLabel(refreshingLabel);
//		}
		if (null != footerLayout) {
			footerLayout.setRefreshingLabel(refreshingLabel);
		}
	}

	public final void setHeaderRefreshing() {
		this.setHeaderRefreshing(true);
	}
	public final void setFooterRefreshing() {
		this.setFooterRefreshing(true);
	}

	/**
	 * Sets the Widget to be in the refresh state. The UI will be updated to
	 * show the 'Refreshing' view.
	 * 
	 * @param doScroll
	 *            - true if you want to force a scroll to the Refreshing view.
	 */
	public final void setHeaderRefreshing(boolean doScroll) {
		if (!isRefreshing()) {
			setHeaderRefreshingInternal(doScroll);
			state = MANUAL_REFRESHING;
		}
	}
	public final void setFooterRefreshing(boolean doScroll) {
		if (!isRefreshing()) {
			setFooterRefreshingInternal(doScroll);
			state = MANUAL_REFRESHING;
		}
	}

	public final boolean hasPullFromTop() {
		return currentMode != MODE_PULL_UP_TO_REFRESH;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		if (!isPullToRefreshEnabled) {
			return false;
		}

		if (isRefreshing() && disableScrollingWhileRefreshing) {
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getEdgeFlags() != 0) {
			return false;
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_MOVE: {
			if (isBeingDragged) {
				lastMotionY = event.getY();
				this.pullEvent();
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			if (isReadyForPull()) {
				lastMotionY = initialMotionY = event.getY();
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (isBeingDragged) {
				isBeingDragged = false;

				if(currentMode==MODE_PULL_UP_TO_REFRESH&&headerHeight < Math.abs(newHeight)){
					state = PULL_TO_UP_REFRESH;
				}else if(currentMode==MODE_PULL_DOWN_TO_REFRESH&&headerHeight < Math.abs(newHeight)){
					state = PULL_TO_DOWN_REFRESH;
				}
				
				if (state == PULL_TO_DOWN_REFRESH && null != onHeaderRefreshListener&&headerHeight < Math.abs(newHeight)) {
					setHeaderRefreshingInternal(true);
					onHeaderRefreshListener.onHeaderRefresh();
				} else if(state==PULL_TO_UP_REFRESH&&null !=onFooterRefreshListener&&headerHeight < Math.abs(newHeight)){
					setFooterRefreshingInternal(true);
					onFooterRefreshListener.onFooterRefresh();
				}else {
					smoothScrollTo(0);
				}
				state = RELEASE_TO_REFRESH;
				return true;
			}
			break;
		}
		}

		return false;
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled) {
			return false;
		}

		if (isRefreshing() && disableScrollingWhileRefreshing) {
			return true;
		}

		final int action = event.getAction();

		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			isBeingDragged = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
			return true;
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			if (isReadyForPull()) {

				final float y = event.getY();
				final float dy = y - lastMotionY;
				final float yDiff = Math.abs(dy);
				final float xDiff = Math.abs(event.getX() - lastMotionX);

				if (yDiff > touchSlop && yDiff > xDiff) {
					if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH)
							&& dy >= 0.0001f && isReadyForPullDown()) {
						lastMotionY = y;
						isBeingDragged = true;
						if (mode == MODE_BOTH) {
							currentMode = MODE_PULL_DOWN_TO_REFRESH;
						}
					} else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH)
							&& dy <= 0.0001f && isReadyForPullUp()) {
						lastMotionY = y;
						isBeingDragged = true;
						if (mode == MODE_BOTH) {
							currentMode = MODE_PULL_UP_TO_REFRESH;
						}
					}
				}
			}
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			if (isReadyForPull()) {
				lastMotionY = initialMotionY = event.getY();
				lastMotionX = event.getX();
				isBeingDragged = false;
			}
			break;
		}
		}

		return isBeingDragged;
	}

	protected void addRefreshableView(Context context, T refreshableView) {
		addView(refreshableView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 0, 1.0f));
	}

	/**
	 * This is implemented by derived classes to return the created View. If you
	 * need to use a custom View (such as a custom ListView), override this
	 * method and return an instance of your custom class.
	 * 
	 * Be sure to set the ID of the view in this method, especially if you're
	 * using a ListActivity or ListFragment.
	 * 
	 * @param context
	 * @param attrs
	 *            AttributeSet from wrapped class. Means that anything you
	 *            include in the XML layout declaration will be routed to the
	 *            created View
	 * @return New instance of the Refreshable View
	 */
	protected abstract T createRefreshableView(Context context,
			AttributeSet attrs);

	protected final int getCurrentMode() {
		return currentMode;
	}

	protected final FooterLayout getFooterLayout() {
		return footerLayout;
	}

	protected final HeaderLayout getHeaderLayout() {
		return headerLayout;
	}

	protected final int getHeaderHeight() {
		return headerHeight;
	}

	protected final int getMode() {
		return mode;
	}

	/**
	 * Implemented by derived class to return whether the View is in a state
	 * where the user can Pull to Refresh by scrolling down.
	 * 
	 * @return true if the View is currently the correct state (for example, top
	 *         of a ListView)
	 */
	protected abstract boolean isReadyForPullDown();

	/**
	 * Implemented by derived class to return whether the View is in a state
	 * where the user can Pull to Refresh by scrolling up.
	 * 
	 * @return true if the View is currently in the correct state (for example,
	 *         bottom of a ListView)
	 */
	protected abstract boolean isReadyForPullUp();

	// ===========================================================
	// Methods
	// ===========================================================

	protected void resetHeader() {
		state = PULL_TO_DOWN_REFRESH;
		isBeingDragged = false;

		if (null != headerLayout) {
			headerLayout.reset();
		}
//		if (null != footerLayout) {
//			footerLayout.reset();
//		}

		smoothScrollTo(0);
	}
	protected void resetFooter() {
		state = PULL_TO_UP_REFRESH;
		isBeingDragged = false;
		
//		if (null != headerLayout) {
//			headerLayout.reset();
//		}
		if (null != footerLayout) {
			footerLayout.reset();
		}
		
		smoothScrollTo(0);
	}

	protected void setHeaderRefreshingInternal(boolean doScroll) {
		state = REFRESHING;

		if (null != headerLayout) {
			headerLayout.refreshing();
		}
//		if (null != footerLayout) {
//			footerLayout.refreshing();
//		}

		if (doScroll) {
			smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight
					: headerHeight);
		}
	}
	protected void setFooterRefreshingInternal(boolean doScroll) {
		state = REFRESHING;
		
//		if (null != headerLayout) {
//			headerLayout.refreshing();
//		}
		if (null != footerLayout) {
			footerLayout.refreshing();
		}
		
		if (doScroll) {
			smoothScrollTo(currentMode == MODE_PULL_UP_TO_REFRESH ? headerHeight
					: -headerHeight);
		}
	}

	protected final void setHeaderScroll(int y) {
		scrollTo(0, y);
	}

	protected final void smoothScrollTo(int y) {
		if (null != currentSmoothScrollRunnable) {
			currentSmoothScrollRunnable.stop();
		}

		if (this.getScrollY() != y) {
			this.currentSmoothScrollRunnable = new SmoothScrollRunnable(
					handler, getScrollY(), y);
			handler.post(currentSmoothScrollRunnable);
		}
	}

	private void init(Context context, AttributeSet attrs) {

		setOrientation(LinearLayout.VERTICAL);

		touchSlop = ViewConfiguration.getTouchSlop();

		// Styleables from XML
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PullToRefresh);
		if (a.hasValue(R.styleable.PullToRefresh_mode)) {
			mode = a.getInteger(R.styleable.PullToRefresh_mode,
					MODE_PULL_DOWN_TO_REFRESH);
		}

		// Refreshable View
		// By passing the attrs, we can add ListView/GridView params via XML
		refreshableView = this.createRefreshableView(context, attrs);
		this.addRefreshableView(context, refreshableView);

		// Loading View Strings
		String pullLabel = context
				.getString(R.string.pull_to_refresh_pull_label);
		String refreshingLabel = context
				.getString(R.string.pull_to_refresh_refreshing_label);
		String releaseLabel = context
				.getString(R.string.pull_to_refresh_release_label);

		// Add Loading Views
		if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
			headerLayout = new HeaderLayout(context,
					MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel,
					refreshingLabel);
			addView(headerLayout, 0, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(headerLayout);
			headerHeight = headerLayout.getMeasuredHeight();
		}
		if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
			footerLayout = new FooterLayout(context, MODE_PULL_UP_TO_REFRESH,
					releaseLabel, pullLabel, refreshingLabel);
			addView(footerLayout, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(footerLayout);
			headerHeight = footerLayout.getMeasuredHeight();
		}

		// Styleables from XML
		if (a.hasValue(R.styleable.PullToRefresh_headerTextColor)) {
			final int color = a.getColor(
					R.styleable.PullToRefresh_headerTextColor, Color.BLACK);
			if (null != headerLayout) {
				headerLayout.setTextColor(color);
			}
			if (null != footerLayout) {
				footerLayout.setTextColor(color);
			}
		}
		if (a.hasValue(R.styleable.PullToRefresh_headerBackground)) {
			this.setBackgroundResource(a.getResourceId(
					R.styleable.PullToRefresh_headerBackground, Color.WHITE));
		}
		if (a.hasValue(R.styleable.PullToRefresh_adapterViewBackground)) {
			refreshableView.setBackgroundResource(a.getResourceId(
					R.styleable.PullToRefresh_adapterViewBackground,
					Color.WHITE));
		}
		a.recycle();

		// Hide Loading Views
		switch (mode) {
		case MODE_BOTH:
			setPadding(0, -headerHeight, 0, -headerHeight);
			break;
		case MODE_PULL_UP_TO_REFRESH:
			setPadding(0, 0, 0, -headerHeight);
			break;
		case MODE_PULL_DOWN_TO_REFRESH:
		default:
			setPadding(0, -headerHeight, 0, 0);
			break;
		}

		// If we're not using MODE_BOTH, then just set currentMode to current
		// mode
		if (mode != MODE_BOTH) {
			currentMode = mode;
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * Actions a Pull Event
	 * 
	 * @return true if the Event has been handled, false if there has been no
	 *         change
	 */
	private boolean pullEvent() {

		
		final int oldHeight = this.getScrollY();

		switch (currentMode) {
		case MODE_PULL_UP_TO_REFRESH:
			newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0)
					/ FRICTION);
//			state = PULL_TO_UP_REFRESH;
			// newHeight = Math.round((initialMotionY - lastMotionY) /
			// FRICTION);
			break;
		case MODE_PULL_DOWN_TO_REFRESH:
		default:
			newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0)
					/ FRICTION);
//			state = PULL_TO_DOWN_REFRESH;
			// newHeight = Math.round((initialMotionY - lastMotionY) /
			// FRICTION);
			break;
		}

		setHeaderScroll(newHeight);

		if (newHeight != 0) {
			if (state == PULL_TO_DOWN_REFRESH && headerHeight < Math.abs(newHeight)) {
				state = RELEASE_TO_REFRESH;

				switch (currentMode) {
				case MODE_PULL_UP_TO_REFRESH:
					//state = PULL_TO_UP_REFRESH;
					footerLayout.releaseToRefresh();
					break;
				case MODE_PULL_DOWN_TO_REFRESH:
					//state = PULL_TO_DOWN_REFRESH;
					headerLayout.releaseToRefresh();
					break;
				}

				return true;

			} else if(state == PULL_TO_UP_REFRESH && headerHeight < Math.abs(newHeight)){
				state = RELEASE_TO_REFRESH;

				switch (currentMode) {
				case MODE_PULL_UP_TO_REFRESH:
					//state = PULL_TO_UP_REFRESH;
					footerLayout.releaseToRefresh();
					break;
				case MODE_PULL_DOWN_TO_REFRESH:
					//state = PULL_TO_DOWN_REFRESH;
					headerLayout.releaseToRefresh();
					break;
				}

				return true;
			}else if (state == RELEASE_TO_REFRESH
					&& headerHeight >= Math.abs(newHeight)) {

				switch (currentMode) {
				case MODE_PULL_UP_TO_REFRESH:
					state = PULL_TO_UP_REFRESH;
					footerLayout.pullToRefresh();
					break;
				case MODE_PULL_DOWN_TO_REFRESH:
					state = PULL_TO_DOWN_REFRESH;
					headerLayout.pullToRefresh();
					break;
				}

				return true;
			}
		}

		return oldHeight != newHeight;
	}

	private boolean isReadyForPull() {
		switch (mode) {
		case MODE_PULL_DOWN_TO_REFRESH:
			return isReadyForPullDown();
		case MODE_PULL_UP_TO_REFRESH:
			return isReadyForPullUp();
		case MODE_BOTH:
			return isReadyForPullUp() || isReadyForPullDown();
		}
		return false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface OnHeaderRefreshListener {

		public void onHeaderRefresh();

	}

	public static interface OnFooterRefreshListener {

		public void onFooterRefresh();

	}

	public static interface OnLastItemVisibleListener {

		public void onLastItemVisible();

	}

	@Override
	public void setLongClickable(boolean longClickable) {
		getRefreshableView().setLongClickable(longClickable);
	}
}
