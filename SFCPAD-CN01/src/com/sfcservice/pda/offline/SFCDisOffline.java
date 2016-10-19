package com.sfcservice.pda.offline;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sfcservice.bean.UnderShelveBean;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisOffline extends Activity implements OnClickListener {

	private ListView lv_under_shelves;
	private LinearLayout linePro;
	private ImageView imgRefresh;
	private TextView tvShow;
	private String userCode;
	List<UnderShelveBean> listStr;
	UnderAdapter youAdapter;
	private boolean isFirst = true;// 第一次进来
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisOffline.this, "连接服务器失败");
				listStr = new ArrayList<UnderShelveBean>();
				listStr.clear();
				imgRefresh.setVisibility(View.VISIBLE);
				lv_under_shelves.setVisibility(View.INVISIBLE);
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				listStr = new ArrayList<UnderShelveBean>();
				// 组装list
				listStr = MyConnection.getMyConnection().getUnderShelves(
						listStr);// 从后台获取下架单
				System.out.println("===onresume=====" + listStr.size());
				if (listStr.size() == 0) {
					MyTool.toastShow(SFCDisOffline.this, "没有需要配货的下架单！");
				}
				ula = new UnderListAdapter(SFCDisOffline.this, listStr);
				lv_under_shelves.setAdapter(ula);
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				Toast.makeText(SFCDisOffline.this, strMsg, Toast.LENGTH_SHORT)
						.show();
				// 清空列表
				imgRefresh.setVisibility(View.VISIBLE);
				lv_under_shelves.setVisibility(View.INVISIBLE);
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
		setContentView(R.layout.sfc_dis_offline);
		SharedPreferences sp = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		userCode = sp.getString("user", "");
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getOfflineData();// 获取下架单提交
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

	UnderListAdapter ula;

	private void initView() {
		findViewById(R.id.btn_back).setOnClickListener(this);
		lv_under_shelves = (ListView) findViewById(R.id.lv_under_shelves);
		imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
		imgRefresh.setOnClickListener(this);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
	}

	class UnderListAdapter extends UnderAdapter {

		public UnderListAdapter(Context context, List<UnderShelveBean> list) {
			super(context, list);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.dis_offline_view_item, null);
				new HolderView(convertView);
			}
			HolderView holder = (HolderView) convertView.getTag();
			UnderShelveBean item = getItem(position);
			holder.tv_single.setText(item.getSingleTosingle());
			holder.tv_more.setText(item.getSingleTomore());
			if (!listStr.get(position).getSingleTosingle().equals("")) {
				holder.ll_single.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 实例化SharedPreferences对象
						SharedPreferences mySharedPreferences = getSharedPreferences(
								"test", Activity.MODE_PRIVATE);
						// 实例化SharedPreferences.Editor对象
						SharedPreferences.Editor editor = mySharedPreferences
								.edit();
						// 用putString的方法保存数据
						editor.putString("single", listStr.get(position)
								.getSingleTosingle());
						editor.putString("opt_type", "0");
						editor.putString("warehouse_id", listStr.get(position)
								.getWarehouse_id());
						// 提交当前数据
						editor.commit();
						int count = MyConnection.getMyConnection().QueryopCode(
								"offline_pickdetail",
								listStr.get(position).getSingleTosingle(),
								userCode);
						if (count != 0) {
							Intent intent2 = new Intent(SFCDisOffline.this,
									SFCDisOfflinePick.class);
							startActivity(intent2);
							MyTool.playSuccessSound();
						} else {
							isPickupAll();
						}
					}
				});
			}
			if (!listStr.get(position).getSingleTomore().equals("")) {
				holder.ll_more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 实例化SharedPreferences对象
						SharedPreferences mySharedPreferences = getSharedPreferences(
								"test", Activity.MODE_PRIVATE);
						// 实例化SharedPreferences.Editor对象
						SharedPreferences.Editor editor = mySharedPreferences
								.edit();
						// 用putString的方法保存数据
						editor.putString("more", listStr.get(position)
								.getSingleTomore());
						editor.putString("opt_type", "1");
						editor.putString("warehouse_id", listStr.get(position)
								.getWarehouse_id());
						// 提交当前数据
						editor.commit();
						int count = MyConnection.getMyConnection().QueryopCode(
								"offline_pickdetail",
								listStr.get(position).getSingleTomore(),
								userCode);
						if (count != 0) {
							Intent intent2 = new Intent(SFCDisOffline.this,
									SFCDisOfflinePick.class);
							startActivity(intent2);
							MyTool.playSuccessSound();
						} else {
							isPickupAll();
						}
					}
				});
			}
			return convertView;
		}

		class HolderView {
			TextView tv_single = null;
			TextView tv_more = null;
			LinearLayout ll_more = null;
			LinearLayout ll_single = null;

			public HolderView(View v) {
				tv_single = (TextView) v.findViewById(R.id.tv_single);
				tv_more = (TextView) v.findViewById(R.id.tv_more);
				ll_single = (LinearLayout) v.findViewById(R.id.ll_single);
				ll_more = (LinearLayout) v.findViewById(R.id.ll_more);
				v.setTag(this);
			}
		}

	}

	/**
	 * 获取所有下架单
	 */
	private void getOfflineData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "op_status" }, new String[] { "0" },
						"pdaGetPickupAllCode"), handler);

	}

	/**
	 * 判断是否有未完成的下架单
	 */
	private void isPickupAll() {
		String opcode = MyConnection.getMyConnection().QueryUnopCode(
				"offline_pickdetail", userCode);
		if (opcode.equals("")) {
			Intent intent = new Intent(SFCDisOffline.this, BindContainer.class);
			startActivity(intent);
			MyTool.playSuccessSound();
		} else {
			MyTool.playFailedSound();
			Toast.makeText(SFCDisOffline.this, "下架单" + opcode + "未配完！",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.imgRefresh:
			imgRefresh.setVisibility(View.INVISIBLE);
			lv_under_shelves.setVisibility(View.VISIBLE);
			getOfflineData();
			break;
		}

	}

}
