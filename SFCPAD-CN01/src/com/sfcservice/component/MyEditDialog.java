package com.sfcservice.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyTool;

public class MyEditDialog {

	private Context context;
	private Dialogcallback dialogcallback;
	private Dialog dialog;
	private Button sure, cancel;
	private TextView tvContent;
	private EditText etAddNum;
	private boolean isDismmis = true;

	/**
	 * init the dialog
	 * 
	 * @return
	 */
	public MyEditDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dis_add_dialog);

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					dismiss();
					boolean bool = dialogcallback.exitActivity();
					return bool;
				}
				return false;
			}
		});

		tvContent = (TextView) dialog.findViewById(R.id.tvContent);
		etAddNum = (EditText) dialog.findViewById(R.id.etAddBox);
		sure = (Button) dialog.findViewById(R.id.btnConfirm);
		cancel = (Button) dialog.findViewById(R.id.btnCancel);

		sure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getAddBoxNum().equals("")) {
					MyTool.toastShow(context, "箱号不能为空");
					return;
				}
				if (isDismmis) {
					dismiss();
				}
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
		etAddNum.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(etAddNum.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
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

	public boolean isShowing() {
		if (dialog.isShowing()) {
			return true;
		}
		return false;
	}

	/**
	 * 点击确定不让dialog消失
	 */
	public void isDismmis() {
		if (isDismmis) {
			isDismmis = false;
			return;
		}
		isDismmis = true;
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
		isDismmis = true;
	}

	public String getAddBoxNum() {
		return etAddNum.getText().toString();
	}

	public void setAddBoxNum(String str) {
		etAddNum.setText("");
		etAddNum.append(str);
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(etAddNum.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		sure.requestFocus();
		sure.setFocusable(true);
	}
}