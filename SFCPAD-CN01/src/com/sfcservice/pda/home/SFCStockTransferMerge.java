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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class SFCStockTransferMerge extends Activity implements OnClickListener,
		OnFocusChangeListener, OnEditorActionListener {
	private Button btn_back, btnClear, btnConfirm;
	private EditText etOldShelfNum, etAfterShelfNum;
	private TextView tvBoxNum;
	private MyBroadCast broadCast;
	private String showstr = "";
	private boolean down = false;
	private boolean boxInfo = false;
	private boolean scan=false;
	private LinearLayout linePro;
	private TextView tvShow;
	private Animation animation;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				if (boxInfo) {
					tvBoxNum.setText("");
					boxInfo = false;
				}
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCStockTransferMerge.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				if (boxInfo) {
					String strInfo = MyConnection.getMyConnection()
							.getBoxNumByOldShelfNum();
					tvBoxNum.setText(strInfo);
					boxInfo = false;
					tvBoxNum.startAnimation(animation);
					etAfterShelfNum.requestFocus();
					etAfterShelfNum.setFocusable(true);
				} else {
					MyTool.playSuccessSound();
					MyTool.toastShow(SFCStockTransferMerge.this, "转移成功");
					etAfterShelfNum.setText("");
					etOldShelfNum.setText("");
					tvBoxNum.setText("");
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCStockTransferMerge.this, strMsg);

				if (boxInfo) {
					tvBoxNum.setText("");
					boxInfo = false;
				}
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
		setContentView(R.layout.sfc_stock_transfer_merge);
		init();
	}

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		animation=AnimationUtils.loadAnimation(this, R.anim.right_in);
		etOldShelfNum = (EditText) findViewById(R.id.etOldShelfNum);
		etAfterShelfNum = (EditText) findViewById(R.id.etAfterShelvesNum);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btn_back = (Button) findViewById(R.id.btn_back);
		tvBoxNum = (TextView) findViewById(R.id.tvBoxNum);
		btn_back.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnClear.setOnFocusChangeListener(this);
		btnConfirm.setOnFocusChangeListener(this);

		etAfterShelfNum.setOnFocusChangeListener(this);
		etAfterShelfNum.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == 0) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								etAfterShelfNum.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					etAfterShelfNum.clearFocus();
					btnConfirm.requestFocus();
					btnConfirm.setFocusable(true);
				}
				return true;
			}
		});
		etOldShelfNum.setOnEditorActionListener(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			down = true;
		} else {
			down = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.etAfterShelvesNum) {
			if (!hasFocus && down) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(
							etAfterShelfNum.getWindowToken(),
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
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == 0 || actionId == 6) {
			if (event != null && event.getAction() == 1) {
				return true;
			}
			if (MyConfig.getMyConfig().getNetGood()) {
				MyTool.hideInputKeyBroad(SFCStockTransferMerge.this);
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

	public void myClick(View v) {
		MyTool.hideInputKeyBroad(this);
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btnClear:
			etOldShelfNum.setText("");
			etAfterShelfNum.setText("");
			tvBoxNum.setText("");
			break;
		case R.id.btnConfirm:
			if(scan){
				scan=false;
				break;
			}
			if (etAfterShelfNum.getText().toString().equals("")
					|| etOldShelfNum.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "您还有未输入选项,请核查后输入");
				break;
			}
			// 提交数据至服务器端
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);

			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "wsCodeOld", "wsCode" },
							new String[] { etOldShelfNum.getText().toString(),
									etAfterShelfNum.getText().toString() },
							"tranferMerge"), handler);

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
						new String[] { "ws_code_old" },
						new String[] { etOldShelfNum.getText().toString() },
						"checkMerge"), handler);
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus() {
		scan=true;
		if (etOldShelfNum.hasFocus()) {
			etOldShelfNum.setText("");
			etOldShelfNum.append(showstr);
			etOldShelfNum.clearFocus();
			getBoxInfo();
			return;
		}
		if (etAfterShelfNum.hasFocus()) {
			etAfterShelfNum.setText("");
			etAfterShelfNum.append(showstr);
			etAfterShelfNum.clearFocus();
			btnConfirm.requestFocus();
			btnConfirm.setFocusable(true);
			return;
		}
	}
}
