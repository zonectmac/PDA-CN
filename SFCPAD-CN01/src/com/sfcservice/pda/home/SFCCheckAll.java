package com.sfcservice.pda.home;

import java.util.ArrayList;

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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.CheckBean;
import com.sfcservice.component.MyDialog;
import com.sfcservice.component.MyDialog.Dialogcallback;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCCheckAll extends Activity implements OnClickListener,
		OnEditorActionListener {
	private EditText etShelfNum;
	private Button btnBack;
	private LinearLayout linePro;
	private TextView tvShow;
	private MyDialog dialog;
	private ArrayList<CheckBean> listBean;
	/**
	 * 0获取货架数据，1添加至盘点任务列表
	 */
	private int STATE = -1;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCCheckAll.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					listBean = new ArrayList<CheckBean>();

					MyConnection.getMyConnection().getSKUInfo(listBean);
					MyConfig.getMyConfig().setListBean(listBean);

					Intent intent = new Intent(SFCCheckAll.this,
							SFCCheckAllSKU.class);
					intent.putExtra(MyConfig.TAG, etShelfNum.getText()
							.toString());
					startActivity(intent);
					break;
				}
				if (STATE == 1) {
					etShelfNum.setText("");
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				boolean bool=false;
				if (bool=MyConnection.getMyConnection().isAdd()) {
					dialog.setContent("货位号 " + etShelfNum.getText().toString()
							+ " 有库存冻结不能盘点，请确认是否需要添加至盘点任务列表");
					dialog.show();
				}
				if(!bool){
					MyTool.toastShow(SFCCheckAll.this, strMsg);
				}
				break;
			default:
				break;
			}
		};
	};

	private MyBroadCast broadCast;

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

				if (etShelfNum.hasFocus()) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								etShelfNum.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					etShelfNum.setText("");
					etShelfNum.append(showstr);
					STATE=0;
					getData();
				}
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_check_all);
		init();
	}

	public void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		etShelfNum = (EditText) findViewById(R.id.etShelfNum);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(this);
		etShelfNum.setOnEditorActionListener(this);

		dialog = new MyDialog(this);
		dialog.setDialogCallback(new Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				STATE = 1;
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);
				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_CHECK,
						MyConnection.getMyConnection()
								.writeJsonWithUserInfo(
										new String[] { "ws_code" },
										new String[] { etShelfNum.getText()
												.toString() },
										"addPendingStock"), handler);
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 113) {
			etShelfNum.setText("");
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadCast);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == 0 || actionId == 6) {
			if (event != null && event.getAction() == 1) {
				return true;
			}
			if (MyConfig.getMyConfig().getNetGood()) {
				if (etShelfNum.getText().toString().equals("")) {
					MyTool.playFailedSound();
					MyTool.toastShow(SFCCheckAll.this, "请输入货位号");
				} else {
					MyTool.hideInputKeyBroad(SFCCheckAll.this);
					STATE = 0;
					getData();
				}
				return true;
			} else {
				MyTool.toastShow(SFCCheckAll.this, "暂无网络");
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}

	// 获取数据
	private void getData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_CHECK,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "ws_code" },
						new String[] { etShelfNum.getText().toString() },
						"getSkuByWs"), handler);
	}
}
