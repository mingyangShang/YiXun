package com.yixun.myview;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class HideBarScrollListener implements OnScrollListener{
	private OnScroll view = null;
	public HideBarScrollListener(OnScroll v){
		this.view = v;
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		switch(arg1){
		case OnScrollListener.SCROLL_STATE_IDLE://����״̬ 
//			view.setVisibility(View.VISIBLE);
			view.onIdle();
			break;  
	    case OnScrollListener.SCROLL_STATE_FLING://����״̬  
//	    	view.setVisibility(View.INVISIBLE);
	    	view.onFling();
	    	break;         
	    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://���������  
//	    	view.setVisibility(View.INVISIBLE);
	    	view.onScroll();
	    	break;  
		}  
	}
	public interface OnScroll{
		void onIdle();
		void onFling();
		void onScroll();
	}
	

}
