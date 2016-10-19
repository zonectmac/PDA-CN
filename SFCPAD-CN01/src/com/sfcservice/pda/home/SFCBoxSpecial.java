package com.sfcservice.pda.home;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCBoxSpecial extends Activity implements OnClickListener,
		OnEditorActionListener {
	private EditText etCount, etCustomerID, etContanerCode;
	private ImageView imgInfo;
	private Button btnBack;
	private Button btnConfirm;
	private LinearLayout linePro;
	RadioButton opt1,opt2;
	private ImageView img1, img2;
	private String type = "1";
	private TextView tvShow;
	private RadioGroup rg;
	private boolean down=true;
	/**
	 * 0根据产品号获取数据,1容器编号
	 */
	private int STATE = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			down=true;
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCBoxSpecial.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					imgInfo.setImageBitmap(MyConfig.getMyConfig().getBitmap());
					etContanerCode.requestFocus();
					etContanerCode.setFocusable(true);
					break;
				}
				if(STATE==1){
					etCount.requestFocus();
					etCount.setFocusable(true);
					break;
				}
				if(STATE==2){
					etContanerCode.setText("");
					etCount.setText("");
					etCustomerID.setText("");
					imgInfo.setImageBitmap(null);
					MyTool.toastShow(SFCBoxSpecial.this, "恭喜");
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCBoxSpecial.this, strMsg);
				if(STATE==1){
					etContanerCode.requestFocus();
					etContanerCode.setFocusable(true);
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
				SFCFocus(showstr);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_box_dis);
		init();
	}

	public void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		linePro = (LinearLayout) findViewById(R.id.line_pro);
		imgInfo=(ImageView)findViewById(R.id.imgInfo);
		tvShow = (TextView) findViewById(R.id.tv_show);
		etContanerCode = (EditText) findViewById(R.id.etContainerCode);
		etCount = (EditText) findViewById(R.id.etCount);
		etCustomerID = (EditText) findViewById(R.id.etCustomerID);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		
		rg = (RadioGroup) findViewById(R.id.rg);
		opt1 = (RadioButton)findViewById(R.id.opt1);//(LinearLayout) findViewById(R.id.opt1);
		opt2 = (RadioButton) findViewById(R.id.opt2);
		etCustomerID.setOnEditorActionListener(this);
		etContanerCode.setOnEditorActionListener(this);
		rg.check(R.id.opt1);
//		opt1.setOnClickListener(this);
//		opt1.setSelected(true);
//		opt2.setOnClickListener(this);
//		opt2.setSelected(false);
		etCustomerID.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode==66&&event.getAction()==KeyEvent.ACTION_DOWN){
					if(down){
						down=false;
						STATE=0;
						getData();
					}
					return true;
				}
				if(keyCode==66&&event.getAction()==KeyEvent.ACTION_UP){
					return true;
				}
				return false;
			}
		});
		etContanerCode.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				// TODO Auto-generated method stub
				if(keyCode==66&&event.getAction()==KeyEvent.ACTION_DOWN){
					if(down){
						down=false;
						STATE=1;
						getData();
					}
					return true;
				}
				if(keyCode==66&&event.getAction()==KeyEvent.ACTION_UP){
					return true;
				}
				return false;
			}
		});
		etCount.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				// TODO Auto-generated method stub
				if(keyCode==66&&event.getAction()==KeyEvent.ACTION_DOWN){
					btnConfirm.requestFocus();
					btnConfirm.setFocusable(true);
					 
					return true;
				}
				if(keyCode==66&&event.getAction()==KeyEvent.ACTION_UP){
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// TODO Auto-generated method stub
		if (keyCode == 113) {
			etContanerCode.setText("");
			etCount.setText("");
			etCustomerID.setText("");
			if(opt1.isChecked()){ 
				type = "2";
				rg.check(R.id.opt2);
			}
		}
		if (keyCode == 112) {
			if(opt2.isChecked()){ 
				type = "1";
				rg.check(R.id.opt1);
			}
			
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.etCustomerID:
			if (actionId == 6) {
				STATE = 0;
				getData();
			}
			break;
		case R.id.etContainerCode:
			if (actionId == 6) {
				STATE = 1;
				getData();
			}
			break;
		default:
			break;
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
		case R.id.opt1:
			type = "1";
			rg.check(R.id.opt1);
			 
			 
			break;
		case R.id.opt2:
			type = "2";
			rg.check(R.id.opt2);
			break;
		case R.id.btnConfirm:
			String str=etCount.getText().toString();
			int i=0;
			try {
				i=Integer.parseInt(str);
			} catch (Exception e) {
				// TODO: handle exception
				MyTool.toastShow(SFCBoxSpecial.this, "数量输入有误");
				break;
			}
			if(i<=0){
				MyTool.toastShow(SFCBoxSpecial.this, "数量必须大于0");
				break;
			}
			STATE = 2;
			getData();
			break;
		default:
			break;
		}
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus(String str) {
		if (etCustomerID.hasFocus()) {
			etCustomerID.setText("");
			etCustomerID.append(str);
			STATE=0;
			getData();
			return;
		}
		if (etContanerCode.hasFocus()) {
			etContanerCode.setText("");
			etContanerCode.append(str);
			STATE=1;
			getData();
			return;
		}
		if (etCount.hasFocus()) {
			etCount.setText("");
			etCount.append(str);
			return;
		}
		 
	}

	// 获取数据
	private void getData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		switch (STATE) {
		case 0:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "product_barcode" },
							new String[] { etCustomerID.getText().toString() },
							"specialCheckProduct"), handler);
			break;
		case 1:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeJsonWithUserInfo(
									new String[] { "container_code" },
									new String[] { etContanerCode.getText()
											.toString() }, "specialCheckBox"),
					handler);
			break;
		case 2:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "container_code", "cb_quantity",
									"product_barcode","product_type" },
							new String[] { etContanerCode.getText().toString(),
									etCount.getText().toString(),
									etCustomerID.getText().toString(),type },
							"specialPoints"), handler);
			break;
		default:
			break;
		}
	}
}
