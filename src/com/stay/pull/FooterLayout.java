package com.stay.pull;

import com.yixun.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FooterLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView footerImage;
	private final ProgressBar footerProgress;
	private final TextView footerText;

	private String pullLabel;
	private String refreshingLabel;
	private String releaseLabel;

	private final Animation rotateAnimation, resetRotateAnimation;

	public FooterLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel) {
		super(context);
		ViewGroup footer = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, this);
		footerText = (TextView) footer.findViewById(R.id.pull_to_refresh_text);
		footerImage = (ImageView) footer.findViewById(R.id.pull_to_refresh_image);
		footerProgress = (ProgressBar) footer.findViewById(R.id.pull_to_refresh_progress);

		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		        0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
		        Animation.RELATIVE_TO_SELF, 0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;
		this.refreshingLabel = refreshingLabel;

		switch (mode) {
			case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
				footerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
				break;
			case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
			default:
				footerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
				break;
		}
	}

	public void reset() {
		footerText.setText(pullLabel);
		footerImage.setVisibility(View.VISIBLE);
		footerProgress.setVisibility(View.GONE);
	}

	public void releaseToRefresh() {
		footerText.setText(releaseLabel);
		footerImage.clearAnimation();
		footerImage.startAnimation(rotateAnimation);
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
	}

	public void refreshing() {
		footerText.setText(refreshingLabel);
		footerImage.clearAnimation();
		footerImage.setVisibility(View.INVISIBLE);
		footerProgress.setVisibility(View.VISIBLE);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		footerText.setText(pullLabel);
		footerImage.clearAnimation();
		footerImage.startAnimation(resetRotateAnimation);
	}

	public void setTextColor(int color) {
		footerText.setTextColor(color);
	}

}
