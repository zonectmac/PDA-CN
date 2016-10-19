package com.sfcservice.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyTool;

public class SFCDisExceptionDialog {

	private Context context;
	private Dialogcallback dialogcallback;
	private Dialog dialog;
	private Button sure,cancel;
	private TextView tvExceptionCount;
	private EditText etCount;
	private int countAll;
	private Button btn;
	private TextView tv;
	private Button btnItem;
	public SFCDisExceptionDialog(Context con) {
		this.context = con;
		dialog = new Dialog(context, R.style.SFCDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sfc_dis_exception_dialog);
		
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_BACK){
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});
		
		tvExceptionCount = (TextView) dialog.findViewById(R.id.tvExceptionCount);
		sure = (Button) dialog.findViewById(R.id.btnConfirm);
		cancel=(Button)dialog.findViewById(R.id.btnCancel);
		etCount=(EditText)dialog.findViewById(R.id.etCount);
		etCount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int i=0;
				try {
					i=Integer.parseInt(etCount.getText().toString());
					if(i<0){
						MyTool.toastShow(context, "数量输入有误");
						return;
					}
					setDisCount(i);
				} catch (Exception e) {
					// TODO: handle exception
					MyTool.toastShow(context, "数量输入有误");
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
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
	public void show() {
		etCount.selectAll();
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
	public void setCountAll(int countAll){
		this.countAll=countAll;
		etCount.setText(countAll+"");
	}
	public void setDisCount(int disCount){
		if((countAll-disCount<0)){
			MyTool.toastShow(context, "此次最大配货数为 "+countAll+" ,请重新输入");
			return;
		}
		tvExceptionCount.setText(""+(countAll-disCount));
	}
	public String getCoutAll(){
		return countAll+"";
	}
	public String getExceptionCount(){
		return tvExceptionCount.getText().toString();
	}
	public String getGoodCount(){
		return etCount.getText().toString();
	}
	public boolean isAdd(){
		int i=0;
		try {
			i=Integer.parseInt(etCount.getText().toString());
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		if(i>=countAll||i<0){
			return false;
		}
		return true;
	}
}