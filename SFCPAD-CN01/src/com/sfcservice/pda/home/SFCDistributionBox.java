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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sfcservice.bean.DisBoxBean;
import com.sfcservice.component.MyDialog;
import com.sfcservice.component.MyDialog.Dialogcallback;
import com.sfcservice.component.MyEditDialog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDistributionBox extends Activity implements OnClickListener,
		OnItemClickListener {
	private ListView listView;
	private LinearLayout linePro;
	private Button btnBack;
	private TextView tvShow, tvCodeNum;
	private ImageView imgRefresh;
	private MyAdapter adapter;
	private String OP_CODE;
	private int myposition;
	private ArrayList<DisBoxBean> listBean;
	private int STATE = -1;
	private MyDialog dialog;
	private MyEditDialog dialog2;
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
				dialog2.setAddBoxNum(showstr);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDistributionBox.this, "连接服务器失败");
				if (STATE == 0) {
					listView.setVisibility(View.INVISIBLE);
					imgRefresh.setVisibility(View.VISIBLE);
				}
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					listView.setVisibility(View.VISIBLE);
					imgRefresh.setVisibility(View.INVISIBLE);
					listBean = new ArrayList<DisBoxBean>();
					MyConnection.getMyConnection().getDisBox(listBean);
					adapter.notifyDataSetChanged();
					break;
				}
				if (STATE == 1) {
					listBean.remove(myposition);
					adapter.notifyDataSetChanged();
					MyTool.toastShow(SFCDistributionBox.this, "删除成功");
					break;
				}
				if (STATE == 2) {
					DisBoxBean bean = new DisBoxBean();
					bean.setBoxNum(dialog2.getAddBoxNum());
					listBean.add(bean);
					adapter.notifyDataSetChanged();
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDistributionBox.this, strMsg);
				if (STATE == 0) {
					listView.setVisibility(View.INVISIBLE);
					imgRefresh.setVisibility(View.VISIBLE);
				}
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
		setContentView(R.layout.sfc_dis_box);
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	public void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		Intent intent = getIntent();
		OP_CODE = intent.getStringExtra(MyConfig.TAG);

		listBean = new ArrayList<DisBoxBean>();
		imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
		btnBack = (Button) findViewById(R.id.btn_back);
		listView = (ListView) findViewById(R.id.listView);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		tvCodeNum = (TextView) findViewById(R.id.tvCodeNum);
		tvCodeNum.setText("下架单号 : " + OP_CODE);
		btnBack.setOnClickListener(this);
		imgRefresh.setOnClickListener(this);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		dialog = new MyDialog(this);
		dialog.setConfirmText("删除");
		dialog.setContent("是否确定删除此配货箱");
		dialog.setDialogCallback(new Dialogcallback() {

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				if (listBean.size() == 1) {
					MyTool.toastShow(SFCDistributionBox.this, "必须保留一个配货箱");
					return;
				}

				STATE = 1;
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);

				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection().writeJsonWithUserInfo(
								new String[] { "container_code", "op_code" },
								new String[] {
										listBean.get(myposition).getBoxNum(),
										OP_CODE }, "pdaPickupDelContainer"),
						handler);
			}

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub

			}
		});
		dialog2 = new MyEditDialog(this);
		dialog2.setDialogCallback(new MyEditDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				STATE = 2;
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);

				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection()
								.writeJsonWithUserInfo(
										new String[] { "op_code",
												"container_code" },
										new String[] { OP_CODE,
												dialog2.getAddBoxNum() },
										"pdaPickupAddContainer"), handler);
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub

			}
		});
		STATE = 0;
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "op_code" }, new String[] { OP_CODE },
						"pdaPickupContainers"), handler);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.imgRefresh:
			STATE = 0;
			imgRefresh.setVisibility(View.INVISIBLE);
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code" },
							new String[] { OP_CODE }, "pdaPickupContainers"),
					handler);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (linePro.getVisibility() == View.VISIBLE) {
				MyTool.toastShow(this, "正在连接服务器,请稍等...");
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listBean.size() + 1;
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
			// TODO Auto-generated method stub
			if (position == listBean.size()) {
				View v = LayoutInflater.from(SFCDistributionBox.this).inflate(
						R.layout.sfc_add_item, null);
				return v;
			}
			View v = LayoutInflater.from(SFCDistributionBox.this).inflate(
					R.layout.sfc_dis_box_item, null);
			TextView tvBox = (TextView) v.findViewById(R.id.tvBox);
			TextView tvNum = (TextView) v.findViewById(R.id.tvNum);
			tvNum.setText((position + 1) + "");
			tvBox.setText(listBean.get(position).getBoxNum());
			return v;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (position == listBean.size()) {
			dialog2.show();
			return;
		}
		myposition = position;
		dialog.show();
	}
}
