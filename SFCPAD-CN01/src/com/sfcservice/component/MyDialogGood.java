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

public class MyDialogGood {

	private Context context;
	private Dialogcallback dialogcallback;
	private Dialog dialog;
	private Button sure, cancel;
	private TextView tvContent;

	/**
	 * init the dialog
	 * 
	 * @return
	 */
	public MyDialogGood(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dialog);

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});

		tvContent = (TextView) dialog.findViewById(R.id.tvContent);
		sure = (Button) dialog.findViewById(R.id.btnConfirm);
		cancel = (Button) dialog.findViewById(R.id.btnCancel);

		sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				dialogcallback.btnConfirm();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				dialogcallback.btnCancel();
			}
		});
	}

	public interface Dialogcallback {
		public void btnConfirm();

		public void btnCancel();

		public boolean exitActivity();
	}

	public void setDialogCallback(Dialogcallback dialogcallback) {
		this.dialogcallback = dialogcallback;
	}

	public void setContent(String content) {
		tvContent.setText(content);
	}

	public void setConfirmText(String confirmText) {
		sure.setText(confirmText);
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

	public void hideCancle() {
		cancel.setVisibility(View.GONE);
	}
}