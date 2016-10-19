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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.SKUBean;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDetectionSKU extends Activity implements OnClickListener,
		OnEditorActionListener {
	private EditText etSKUNum;
	private Button btnBack;
	private TextView tvAll;
	private ListView sfcList;
	private LinearLayout linePro;
	private TextView tvShow;
	private MyAdatper adatper;
	private ArrayList<SKUBean> listBean;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			etSKUNum.selectAll();
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDetectionSKU.this, "连接服务器失败");
				listBean = new ArrayList<SKUBean>();
				adatper.notifyDataSetChanged();
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				listBean = new ArrayList<SKUBean>();
				String strAll = MyConnection.getMyConnection()
						.getDetectingSKUData(listBean);
				tvAll.setText(strAll);
				adatper.notifyDataSetChanged();
				if (listBean.size() == 0) {
					MyTool.toastShow(SFCDetectionSKU.this, "暂无SKU信息");
					break;
				}
				MyTool.toastShow(SFCDetectionSKU.this, "恭喜");
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDetectionSKU.this, strMsg);
				listBean = new ArrayList<SKUBean>();
				adatper.notifyDataSetChanged();
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
				etSKUNum.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(etSKUNum.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				etSKUNum.append(showstr);
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);
				getData();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_detection_sku);
		init();
	}

	public void init() {
		listBean = new ArrayList<SKUBean>();
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		sfcList = (ListView) findViewById(R.id.sfc_list);
		etSKUNum = (EditText) findViewById(R.id.etSKUNum);
		btnBack = (Button) findViewById(R.id.btn_back);
		tvAll = (TextView) findViewById(R.id.tvAll);
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		btnBack.setOnClickListener(this);
		etSKUNum.setOnEditorActionListener(this);

		adatper = new MyAdatper();
		sfcList.setAdapter(adatper);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 113) {
			etSKUNum.setText("");
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
		if (actionId == 0 || actionId == 6) {
			if (etSKUNum.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "请输入货架号");
				return false;
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(etSKUNum.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);
			getData();
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

	private class MyAdatper extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listBean.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = LayoutInflater.from(SFCDetectionSKU.this).inflate(
					R.layout.sfc_detection_sku_item, null);
			TextView tvCount = (TextView) v.findViewById(R.id.tvCount);
			TextView tvShelfNum = (TextView) v.findViewById(R.id.tvShelfNum);
			tvShelfNum.setText("货架号 : " + listBean.get(position).getWsCode()
					+ " ; ");
			tvCount.setText("可用 : " + listBean.get(position).getWpb_quantity()
					+ "冻结 : " + listBean.get(position).getWpb_quantity_hold());
			return v;
		}
	}

	// 获取数据
	private void getData() {
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "barcode" },
						new String[] { etSKUNum.getText().toString() },
						"selectShelf"), handler);
	}
}
