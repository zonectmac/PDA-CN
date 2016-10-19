package com.sfcservice.pda;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SFCPopWindow {
	private PopupWindow p;
	private SFCPopWindow(){
		
	}
	public static SFCPopWindow sfcPopWindow;
	public static SFCPopWindow getSFCPopWindow(){
		if(sfcPopWindow==null){
			sfcPopWindow=new SFCPopWindow();
		}
		return sfcPopWindow;
	}
	public interface BtnClickCallBack {
		public void btnClick();
	}
	/**
	 * 退出时的模拟dialog
	 * @param context
	 * @param strContent
	 * @param baseView
	 * @param btnClickCallBack
	 */
	public void show(Context context, String strContent, View baseView,
			final BtnClickCallBack btnClickCallBack) {
		if(p!=null&&p.isShowing()){
			p.dismiss();
			return;
		}
		View v = LayoutInflater.from(context)
				.inflate(R.layout.sfc_dialog, null);
		LinearLayout lineContent = (LinearLayout) v
				.findViewById(R.id.lineContent);
		lineContent.startAnimation(AnimationUtils.loadAnimation(context,
				R.anim.dialog_enter));
		TextView tvContent = (TextView) v.findViewById(R.id.tvContent);
		tvContent.setText(strContent);
		Button btnConfrim = (Button) v.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) v.findViewById(R.id.btnCancel);

		btnConfrim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				p.dismiss();
				btnClickCallBack.btnClick();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				p.dismiss();
			}
		});
		p = new PopupWindow(v, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		p.setBackgroundDrawable(new BitmapDrawable());
		p.setFocusable(true);
		p.showAtLocation(baseView, Gravity.CENTER, 0, 0);
	}
}
