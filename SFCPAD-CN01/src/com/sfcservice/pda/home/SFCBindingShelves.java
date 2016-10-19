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
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCBindingShelves extends Activity implements OnClickListener,
		OnFocusChangeListener, OnEditorActionListener {
	private Button btn_back, btnClear, btnConfirm;
	private EditText etSKU, etNewShelvesNum, etNum, etNumConfirm;
	private LinearLayout linePro;
	private TextView tvShow;
	private MyBroadCast broadCast;
	private String showstr = "";
	private boolean down = false;
	private boolean scan=false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCBindingShelves.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				etSKU.setText("");
				etNewShelvesNum.setText("");
				etNum.setText("");
				etNumConfirm.setText("");
				MyTool.playSuccessSound();
				MyTool.toastShow(SFCBindingShelves.this, "恭喜");
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCBindingShelves.this, strMsg);
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
		setContentView(R.layout.sfc_binding_shelves);
		init();
	}

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		etSKU = (EditText) findViewById(R.id.etSKU);
		etNewShelvesNum = (EditText) findViewById(R.id.etNewShelvesNum);
		etNum = (EditText) findViewById(R.id.etNum);
		etNumConfirm = (EditText) findViewById(R.id.etNumConfirm);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnClear.setOnFocusChangeListener(this);
		btnConfirm.setOnFocusChangeListener(this);
		etNumConfirm.setOnFocusChangeListener(this);
		etNumConfirm.setOnEditorActionListener(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			down = true;
			if (etNumConfirm.hasFocus()) {
				etNumConfirm.clearFocus();
				btnConfirm.requestFocus();
				btnConfirm.setFocusable(true);
				return true;
			}
		} else {
			down = false;
		}
		if(keyCode==113){
			MyTool.hideInputKeyBroad(this);
			etNewShelvesNum.setText("");
			etNum.setText("");
			etNumConfirm.setText("");
			etSKU.setText("");
			etSKU.requestFocus();
			etSKU.setFocusable(true);
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
		if (v.getId() == R.id.etNumConfirm) {
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
			down=false;
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
		scan=false;
		myClick(v);
	}

	public void myClick(View v) {
		MyTool.hideInputKeyBroad(this);
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btnConfirm:
			if(scan){
				scan=false;
				break;
			}
			if (etNewShelvesNum.getText().toString().equals("")
					|| etNum.getText().toString().equals("")
					|| etNumConfirm.getText().toString().equals("")
					|| etSKU.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "您还有未输入选项,请核查后输入");
				break;
			}

			try {
				int a = Integer.parseInt(etNum.getText().toString());
				int b = Integer.parseInt(etNumConfirm.getText().toString());
				if (a <= 0 || b <= 0) {
					MyTool.playFailedSound();
					MyTool.toastShow(this, "数量必须大于0,请重新输入");
					break;
				}
				if (a != b) {
					MyTool.playFailedSound();
					MyTool.toastShow(this, "两次数量输入不一致,请重新输入");
					break;
				}
			} catch (Exception e) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "数量格式不正确,请重新输入");
				break;
			}

			// 存储数据
			// MyConnection.getMyConnection().saveData(
			// "binding_shelves",
			// new String[] { "user_login_id", "sku", "shelf_num_new",
			// "count", "count_confirm" },
			// new String[] { MyConfig.getMyConfig().getUsers()[0],
			// etSKU.getText().toString(),
			// etNewShelvesNum.getText().toString(),
			// etNum.getText().toString(),
			// etNumConfirm.getText().toString() });
			//
			// etSKU.setText("");
			// etNewShelvesNum.setText("");
			// etNum.setText("");
			// etNumConfirm.setText("");
			// MyTool.playSuccessSound();
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);

			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "qty", "confirmQty", "productSku",
									"newshelf" },
							new String[] { etNum.getText().toString(),
									etNumConfirm.getText().toString(),
									etSKU.getText().toString(),
									etNewShelvesNum.getText().toString() },
							"bindShelf"), handler);
			break;
		case R.id.btnClear:
			etNewShelvesNum.setText("");
			etNum.setText("");
			etNumConfirm.setText("");
			etSKU.setText("");
			etSKU.requestFocus();
			etSKU.setFocusable(true);
			break;
		default:
			break;
		}
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus() {
		scan=true;
		if (etSKU.hasFocus()) {
			etSKU.setText("");
			etSKU.append(showstr);
			etSKU.clearFocus();
			etNewShelvesNum.requestFocus();
			etNewShelvesNum.setFocusable(true);
			return;
		}
		if (etNewShelvesNum.hasFocus()) {
			etNewShelvesNum.setText("");
			etNewShelvesNum.append(showstr);
			etNewShelvesNum.clearFocus();
			etNum.requestFocus();
			etNum.setFocusable(true);
			return;
		}
		if (etNum.hasFocus()) {
			etNum.setText("");
			etNum.append(showstr);
			etNum.clearFocus();
			etNumConfirm.requestFocus();
			etNumConfirm.setFocusable(true);
			return;
		}
		if (etNumConfirm.hasFocus()) {
			etNumConfirm.setText("");
			etNumConfirm.append(showstr);
			etNumConfirm.clearFocus();
			btnConfirm.requestFocus();
			btnConfirm.setFocusable(true);
			return;
		}
	}
}
