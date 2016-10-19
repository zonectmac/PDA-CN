package com.sfcservice.component;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.sfcservice.bean.DisMoreItemBean;
import com.sfcservice.pda.R;

public class SFCDisMoreItemDialog {

	private Context context;
	private Dialog dialog;
	private TextView tvClose;
	private DisMoreItemView disMoreView;
	public SFCDisMoreItemDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dis_online_more_dialog);
		tvClose=(TextView)dialog.findViewById(R.id.tvClose);
		disMoreView=(DisMoreItemView)dialog.findViewById(R.id.disMoreView);
		tvClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	public void show() {
		dialog.show();
	}
	public void setData(ArrayList<DisMoreItemBean> listBean){
		disMoreView.setData(listBean);
	}
	public void dismiss() {
		dialog.dismiss();
	}
	public boolean isShowing() {
		if (dialog.isShowing()) {
			return true;
		}
		return false;
	}
}