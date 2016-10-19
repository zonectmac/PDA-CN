package com.sfcservice.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import com.sfcservice.pda.R;

public class ProDialog {

	private Context context;
	private Dialog dialog;
	private TextView tvShow;
	/**
	 * init the dialog
	 * 
	 * @return
	 */
	public ProDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dialog_pro);
		
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		tvShow=(TextView)dialog.findViewById(R.id.tv_show);
	}
	public void setTvShow(String str){
		tvShow.setText(str);
	}
	public void show() {
		dialog.show();
	}
	public void hide() {
		dialog.hide();
	}
	public void dismiss() {
		dialog.dismiss();
	}
	public boolean isShowing(){
		if(dialog.isShowing()){
			return true;
		}
		return false;
	}

}