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

public class SFCCutSheetBack extends Activity implements OnClickListener,
		OnFocusChangeListener, OnEditorActionListener {
	private Button btn_back, btnClear, btnConfirm;
	private EditText etBackNum, etShelvesNum, etSKU;
	private LinearLayout linePro;
	private TextView tvShow;
	private MyBroadCast broadCast;
	private String showstr = "";
	private boolean down = false;
	private boolean scan = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCCutSheetBack.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				etBackNum.setText("");
				etShelvesNum.setText("");
				etSKU.setText("");
				MyTool.playSuccessSound();
				MyTool.toastShow(SFCCutSheetBack.this, "恭喜");
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCCutSheetBack.this, strMsg);
				break;
			default:
				break;
			}
		}
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
		setContentView(R.layout.sfc_cut_sheet_back);
		init();
	}

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		etBackNum = (EditText) findViewById(R.id.etBackNum);
		etShelvesNum = (EditText) findViewById(R.id.etShelvesNum);
		etSKU = (EditText) findViewById(R.id.etSKU);

		btnClear = (Button) findViewById(R.id.btnClear);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnClear.setOnFocusChangeListener(this);
		btnConfirm.setOnFocusChangeListener(this);

		etSKU.setOnFocusChangeListener(this);
		etSKU.setOnEditorActionListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			down = true;
			if (etSKU.hasFocus()) {
				etSKU.clearFocus();
				btnConfirm.requestFocus();
				btnConfirm.setFocusable(true);
				return true;
			}
		} else {
			down = false;
		}

		if (keyCode == 113) {
			MyTool.hideInputKeyBroad(this);
			etBackNum.setText("");
			etShelvesNum.setText("");
			etSKU.setText("");
			etBackNum.requestFocus();
			etBackNum.setFocusable(true);
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == 0) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(etSKU.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			etSKU.clearFocus();
			btnConfirm.requestFocus();
			btnConfirm.setFocusable(true);
		}
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.etSKU) {
			if (!hasFocus && down) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(etSKU.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			return;
		}
		if (v.isInTouchMode() && hasFocus) {
			down = false;
			myClick(v);
		}
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
		scan = false;
		myClick(v);
	}

	public void myClick(View v) {
		MyTool.hideInputKeyBroad(this);
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btnClear:
			etBackNum.setText("");
			etShelvesNum.setText("");
			etSKU.setText("");
			etBackNum.requestFocus();
			etBackNum.setFocusable(true);
			break;
		case R.id.btnConfirm:
			if (scan) {
				scan = false;
				break;
			}
			if (etBackNum.getText().toString().equals("")
					|| etShelvesNum.getText().toString().equals("")
					|| etSKU.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "您还有未输入选项,请核查后输入");
			} else {
				// 存储数据
				// MyConnection.getMyConnection().saveData(
				// "cut_sheet_back",
				// new String[] { "user_login_id", "back_num",
				// "shelf_num", "sku" },
				// new String[] { MyConfig.getMyConfig().getUsers()[0],
				// etBackNum.getText().toString(),
				// etShelvesNum.getText().toString(),
				// etSKU.getText().toString() });

				// 提交至服务器
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);

				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection()
								.writeJsonWithUserInfo(
										new String[] { "bpcode", "wscode",
												"productSku" },
										new String[] {
												etBackNum.getText().toString(),
												etShelvesNum.getText()
														.toString(),
												etSKU.getText().toString() },
										"backputaway"), handler);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus() {
		scan = true;
		if (etBackNum.hasFocus()) {
			etBackNum.setText("");
			etBackNum.append(showstr);
			etBackNum.clearFocus();
			etShelvesNum.requestFocus();
			etShelvesNum.setFocusable(true);
			return;
		}
		if (etShelvesNum.hasFocus()) {
			etShelvesNum.setText("");
			etShelvesNum.append(showstr);
			etShelvesNum.clearFocus();
			etSKU.requestFocus();
			etSKU.setFocusable(true);
			return;
		}
		if (etSKU.hasFocus()) {
			etSKU.setText("");
			etSKU.append(showstr);
			etSKU.clearFocus();
			btnConfirm.requestFocus();
			btnConfirm.setFocusable(true);
			return;
		}
	}
}
