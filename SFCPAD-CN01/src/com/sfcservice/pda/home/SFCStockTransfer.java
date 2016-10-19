package com.sfcservice.pda.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.sfcservice.bean.StockTransferDetail;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCStockTransfer extends Activity implements OnClickListener,
		OnEditorActionListener {
	private String userCode;// 用户名
	private MyBroadCast broadCast;
	private LinearLayout linePro;
	private StockTransferDetail std;
	private TextView tvShow;
	private EditText etunshelvetran;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCStockTransfer.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				std = new StockTransferDetail();
				// 解析返回数据并存取到数据库
				// MyConnection.getMyConnection().insertStockDetail(userCode);
				std = MyConnection.getMyConnection().getStockTransfer(userCode);
				if (std.getOpm_sortcode() != null) {
					System.out.println("-----sssss" + std.toString());
					Intent intent = new Intent(SFCStockTransfer.this,
							SFCStockTransferDetail.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("std", std);
					intent.putExtras(bundle);
					startActivity(intent);
					SharedPreferences sp = getSharedPreferences("test",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("opCode", etunshelvetran.getText()
							.toString().trim());
					editor.commit();
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				Toast.makeText(SFCStockTransfer.this, strMsg,
						Toast.LENGTH_SHORT).show();
				MyTool.toastShow(SFCStockTransfer.this, strMsg);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_stock_transfer);
		initView();
		SharedPreferences sp = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		userCode = sp.getString("user", "");
	}

	private void initView() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		etunshelvetran = (EditText) findViewById(R.id.et_under_shelve_transfer);
		etunshelvetran.setOnEditorActionListener(this);
		findViewById(R.id.btn_back).setOnClickListener(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		etunshelvetran.setText("");
	}

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("urovo.rcv.message")) {
				MyTool.playSound();
				byte[] barocode = intent.getByteArrayExtra("barocode");
				int barocodelen = intent.getIntExtra("length", 0);
				String showstr = new String(barocode, 0, barocodelen);

				if (etunshelvetran.hasFocus()) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								etunshelvetran.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					etunshelvetran.setText("");
					etunshelvetran.append(showstr);
					getData();
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 113) {
			etunshelvetran.setText("");
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadCast);
		handler.removeCallbacksAndMessages(null);// 解决handler内存泄漏问题
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == 0 || actionId == 6) {
			if (event != null && event.getAction() == 1) {
				return true;
			}
			if (MyConfig.getMyConfig().getNetGood()) {
				if (etunshelvetran.getText().toString().equals("")) {
					MyTool.playFailedSound();
					MyTool.toastShow(SFCStockTransfer.this, "请输入下架单单号");
				} else {
					MyTool.hideInputKeyBroad(SFCStockTransfer.this);
					getData();
				}
				return true;
			} else {
				MyTool.toastShow(SFCStockTransfer.this, "暂无网络");
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取要转移的单
	 */
	private void getData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "op_code" },
						new String[] { etunshelvetran.getText().toString()
								.trim() }, "getDetailByOpcode"), handler);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		}
	}

}
