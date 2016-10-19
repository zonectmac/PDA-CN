package com.sfcservice.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sfcservice.pda.R;

public class SFCDisCompleteDialog implements OnClickListener {

	private Context context;
	private Dialogcallback dialogcallback;
	private Dialog dialog;
	private Button  btnCheck, btnCommit;
	private TextView tvContent;

	/**
	 * init the dialog
	 * 
	 * @return
	 */
	public SFCDisCompleteDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dialog_discomplete);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialogcallback.exitActivity();
					return true;
				}
				return false;
			}
		});

		btnCheck = (Button) dialog.findViewById(R.id.btnCheck);
		btnCommit = (Button) dialog.findViewById(R.id.btnCommit);
		tvContent = (TextView) dialog.findViewById(R.id.tvContent);

		btnCheck.setOnClickListener(this);
		btnCommit.setOnClickListener(this);
	}

	public interface Dialogcallback {
	 
		public void btnCheck();

		public void btnCommit();

		public void exitActivity();
	}

	public void setDialogCallback(Dialogcallback dialogcallback) {
		this.dialogcallback = dialogcallback;
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

	public boolean isShowing() {
		if (dialog.isShowing()) {
			return true;
		}
		return false;
	}

	public void setContent(String content) {
		tvContent.setText(content);
	}

	public void setBtnCheckText(String str) {
		btnCheck.setText(str);
	}

	public void setBtnCommitText(String str) {
		btnCommit.setText(str);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
			case R.id.btnCheck:
				dialogcallback.btnCheck();
				break;
			case R.id.btnCommit:
				dialogcallback.btnCommit();
				break;
			default:
				break;
		}
	}

}