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
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.DetectingBean;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;
public class SFCDetectionShelves extends Activity implements OnClickListener,
		OnEditorActionListener {
	private EditText etShelvesNum;
	private Button btnBack;
	private ListView sfcList;
	private LinearLayout linePro;
	private TextView tvShow;
	private MyAdatper adatper;
	private ArrayList<DetectingBean> listBean = null;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDetectionShelves.this, "连接服务器失败");
				listBean=null;
				adatper.notifyDataSetChanged();
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				listBean = MyConnection.getMyConnection().getDetectingData();
				adatper.notifyDataSetChanged();
				if (listBean.size()==0) {
					MyTool.toastShow(SFCDetectionShelves.this, "暂无货架信息");
				}
				MyTool.toastShow(SFCDetectionShelves.this, "恭喜");
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDetectionShelves.this, strMsg);
				listBean=null;
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
				
				if(!(etShelvesNum.getText().toString().equals(""))){
					etShelvesNum.setText("");
				}
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(etShelvesNum.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				
				etShelvesNum.append(showstr);
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
		setContentView(R.layout.sfc_detection_shelves);
		init();
	}

	public void init() {
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		sfcList = (ListView) findViewById(R.id.sfc_list);
		etShelvesNum = (EditText) findViewById(R.id.etShelvesNum);
		btnBack = (Button) findViewById(R.id.btn_back);
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		btnBack.setOnClickListener(this);
		etShelvesNum.setOnEditorActionListener(this);

		adatper = new MyAdatper();
		sfcList.setAdapter(adatper);

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==113){
			etShelvesNum.setText("");
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
			if(etShelvesNum.getText().toString().equals("")){
				MyTool.playFailedSound();
				MyTool.toastShow(this, "请输入货架号");
				return false;
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(etShelvesNum.getWindowToken(),
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

	// 获取数据
	private void getData() {
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "shelf" },
						new String[] { etShelvesNum.getText().toString() },
						"checkShelf"), handler);
	}

	private class MyAdatper extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (listBean != null&&listBean.size()!=0) {
				return listBean.size();
			}
			return 0;
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
			if (listBean == null&&listBean.size()==0) {
				return null;
			}

			View v = LayoutInflater.from(SFCDetectionShelves.this).inflate(
					R.layout.sfc_detection_shelves_item, null);
			TextView tvSKU = (TextView) v.findViewById(R.id.tv_sku);
			TextView tvCount = (TextView) v.findViewById(R.id.tvCount);
			TextView tvHold = (TextView) v.findViewById(R.id.tvHold);
			TextView tvStatus = (TextView) v.findViewById(R.id.tvStatus);
			tvSKU.setText("SKU: "+listBean.get(position).getSku()+"; ");
			tvCount.setText("数量: "+listBean.get(position).getCount()+"; ");
			tvHold.setText("冻结: "+listBean.get(position).getHoldCount()+"; ");
			tvStatus.setText("状态: "+listBean.get(position).getStatus());
			
			if(!(listBean.get(position).getHoldCount().equals("0"))){
				tvHold.setTextColor(0xffff3300);
			}else{
				tvHold.setTextColor(0xff000000);
			}
			if(!(listBean.get(position).getStatus().equals("正常"))){
				tvStatus.setTextColor(0xffff3300);
			}else{
				tvStatus.setTextColor(0xff000000);
			}
			
			return v;
		}

	}
}
