  
   package com.yixun.myview;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.yixun.R;
import com.yixun.manager.DisplayUtils;
import com.yixun.manager.SettingUtils;

/**
 * 状态按钮的监听事件
 * 
 * @author w
 * 
 */
public class ToggleListener implements OnCheckedChangeListener {
	private Context context;
	private String settingName;
	private ToggleButton toggle;
	private ImageButton toggle_Button;
	private final String voice = "声音效果";
	private final String vibrator = "震动效果";
	private final String receive = "离线时接收消息";
	private final String byOther = "对方无网时用短信发通知";

	public ToggleListener(Context context, String settingName,
			ToggleButton toggle, ImageButton toggle_Button) {
		this.context = context;
		this.settingName = settingName;
		this.toggle = toggle;
		this.toggle_Button = toggle_Button;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// 保存设置
		if (voice.equals(settingName)) {
			SettingUtils.set(context, SettingUtils.VOICE, isChecked);
		} else if (vibrator.equals(settingName)) {
			SettingUtils.set(context, SettingUtils.VIBRATOR, isChecked);
		} else if(receive.equals(settingName)){
			SettingUtils.set(context, SettingUtils.RECEIVE, isChecked);
		} else if(byOther.equals(settingName)){
			SettingUtils.set(context, SettingUtils.BYOTHER, isChecked);
		}
		// 播放动画
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toggle_Button
				.getLayoutParams();
		if (isChecked) {
			// 调整位置
			params.addRule(RelativeLayout.ALIGN_RIGHT, -1);
			if (voice.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_LEFT, R.id.toggle_voice);
			} else if (vibrator.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_LEFT,R.id.toggle_vibrator);
			}  else if (receive.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_LEFT,R.id.toggle_receive);
			}  else if (byOther.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_LEFT,R.id.toggle_byOther);
			}  
			toggle_Button.setLayoutParams(params);
			toggle_Button.setImageResource(R.drawable.progress_thumb_selector);
			toggle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			// 播放动画
			TranslateAnimation animation = new TranslateAnimation(
					DisplayUtils.dip2px(context, 40), 0, 0, 0);
			animation.setDuration(200);
			toggle_Button.startAnimation(animation);
		} else {
			// 调整位置
			if (voice.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_voice);
			} else if (vibrator.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_RIGHT,R.id.toggle_vibrator);
			} else if (receive.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_RIGHT,R.id.toggle_receive);
			} else if (byOther.equals(settingName)) {
				params.addRule(RelativeLayout.ALIGN_RIGHT,R.id.toggle_byOther);
			}
			params.addRule(RelativeLayout.ALIGN_LEFT, -1);
			toggle_Button.setLayoutParams(params);
			toggle_Button
					.setImageResource(R.drawable.progress_thumb_off_selector);

			toggle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			// 播放动画
			TranslateAnimation animation = new TranslateAnimation(
					DisplayUtils.dip2px(context, -40), 0, 0, 0);
			animation.setDuration(200);
			toggle_Button.startAnimation(animation);
		}
	}

}
