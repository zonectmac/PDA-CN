package com.sfcservice.pda.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCContainerShelvesBinding extends Activity implements
		OnClickListener, OnFocusChangeListener, OnEditorActionListener {
	private Button btn_back, btnConfirm, btnClear;
	private EditText etBoxNum, etBindingShelfNum;
	private MyBroadCast broadCast;
	private String showstr = "";
	private boolean down = false;
	private boolean boxInfo = false;
	private boolean scan=false;
	private LinearLayout linePro;
	private TextView tvShow;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				if(boxInfo){
					boxInfo=false;
				}
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCContainerShelvesBinding.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				if (boxInfo) {
					MyTool.toastShow(SFCContainerShelvesBinding.this,
							"恭喜,箱号检测可用");
					etBindingShelfNum.requestFocus();
					etBindingShelfNum.setFocusable(true);
					boxInfo=false;
				} else {
					MyTool.playSuccessSound();
					MyTool.toastShow(SFCContainerShelvesBinding.this, "恭喜,成功绑定");
					etBindingShelfNum.setText("");
					etBoxNum.setText("");
				}
				break;
			case MyConfig.RESULTF:
				if(boxInfo){
					boxInfo=false;
				}
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCContainerShelvesBinding.this, strMsg);
				break;
			default:
				break;
			}
		};
	};

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("urovo.rcv.message")) {
				MyTool.playSound();
				byte[] barocode = intent.getByteArrayExtra("barocode");
				int barocodelen = intent.getIntExtra("length", 0);
				showstr = new String(barocode, 0, barocodelen);
				SFCFocus();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_container_shelves_binding);
		init();
	}

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		etBoxNum = (EditText) findViewById(R.id.etBoxNum);
		etBindingShelfNum = (EditText) findViewById(R.id.etBindingShelvesNum);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btn_back = (Button) findViewById(R.id.btn_back);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnConfirm.setOnFocusChangeListener(this);
		etBoxNum.setOnEditorActionListener(this);

		etBindingShelfNum.setOnFocusChangeListener(this);
		etBindingShelfNum
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == 0) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							if (imm.isActive()) {
								imm.hideSoftInputFromWindow(
										etBindingShelfNum.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
							}
							btnConfirm.requestFocus();
							btnConfirm.setFocusable(true);
						}
						return false;
					}
				});
		etBoxNum.setOnEditorActionListener(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			down = true;
		} else {
			down = false;
		}
		
		if(keyCode==113){
			MyTool.hideInputKeyBroad(this);
			etBoxNum.setText("");
			etBindingShelfNum.setText("");
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.etBindingShelvesNum) {
			if (!hasFocus && down) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(
							etBindingShelfNum.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				btnConfirm.requestFocus();
				btnConfirm.setFocusable(true);
			}
			return;
		}
		if (v.isInTouchMode() && hasFocus) {
			down=false;
			myClick(v);
			return;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == 0 || actionId == 6) {
			if (event != null && event.getAction() == 1) {
				return true;
			}
			if (MyConfig.getMyConfig().getNetGood()) {
				MyTool.hideInputKeyBroad(SFCContainerShelvesBinding.this);
				getBoxInfo();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		scan=false;
		myClick(v);
	}

	private void myClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			MyTool.hideInputKeyBroad(this);
			finish();
			break;
		case R.id.btnClear:
			MyTool.hideInputKeyBroad(this);
			etBoxNum.setText("");
			etBindingShelfNum.setText("");
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
			break;
		case R.id.btnConfirm:
			if(scan){
				scan=false;
				break;
			}
			MyTool.hideInputKeyBroad(this);
			if (etBoxNum.getText().toString().equals("")
					|| etBindingShelfNum.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "您还有未输入选项,请核查后输入");
				break;
			}
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "containerCode", "wsCode" },
							new String[] { etBoxNum.getText().toString(),
									etBindingShelfNum.getText().toString() },
							"wsbind"), handler);
			break;
		default:
			break;
		}
	}

	public void getBoxInfo() {
		boxInfo = true;
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "containerCode" },
						new String[] { etBoxNum.getText().toString() },
						"wsbindCheck"), handler);
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus() {
		scan=true;
		if (etBoxNum.hasFocus()) {
			etBoxNum.setText("");
			etBoxNum.append(showstr);
			getBoxInfo();
			return;
		}
		if (etBindingShelfNum.hasFocus()) {
			etBindingShelfNum.setText("");
			etBindingShelfNum.append(showstr);
			btnConfirm.requestFocus();
			btnConfirm.setFocusable(true);
			return;
		}
	}
}
