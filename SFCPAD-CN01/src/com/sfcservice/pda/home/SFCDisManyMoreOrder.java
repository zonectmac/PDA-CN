package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfcservice.bean.DisMoreBoxBeanP;
import com.sfcservice.component.AsyncLoadImage;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisManyMoreOrder extends Activity implements OnClickListener {
	private ExpandableListView exListView;
	private LinearLayout linePro;
	private Button btnBack, btnCommit;
	private TextView tvShow;
	private TextView tvCodeNum;
	private ImageView imgRefresh;
	private MyAdapter adapter;
	private String OP_CODE;
	private String OPM_TIME;
	private ArrayList<DisMoreBoxBeanP> listBean;
	private AsyncLoadImage asyncLoadImage;
	/**
	 * 0,获取初始数据；1，提交
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
				MyTool.toastShow(SFCDisManyMoreOrder.this, "连接服务器失败");
				if (STATE == 0) {
					exListView.setVisibility(View.INVISIBLE);
					imgRefresh.setVisibility(View.VISIBLE);
				}
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					exListView.setVisibility(View.VISIBLE);
					imgRefresh.setVisibility(View.INVISIBLE);
					ArrayList<DisMoreBoxBeanP> listBean1 = new ArrayList<DisMoreBoxBeanP>();
					MyConnection.getMyConnection().getDisMoreShelfList(
							listBean1);
					listBean = listBean1;
					listBean1 = null;
					adapter.notifyDataSetChanged();
					break;
				}
				if (STATE == 1) {
					// 提交成功
					MyConfig.getMyConfig().setOrderCommit(true);
					MyTool.toastShow(SFCDisManyMoreOrder.this, "提交成功，请重新开始配货");
					finish();
					break;
				}
				break;
			case MyConfig.RESULTF:
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 1) {
					ArrayList<String> list = new ArrayList<String>();
					String[] strs = MyConnection.getMyConnection()
							.getCommitResult(list);
					if (strs != null) {
						MyConfig.getMyConfig().setDisNoCompleteData(strs);
						String[] array = (String[]) list
								.toArray(new String[list.size()]);
						MyConfig.getMyConfig().setDisOrdersCode(array);
						MyConfig.getMyConfig().setCommitBad(true);
						MyConfig.getMyConfig().setGoOnPickup(true);
						MyTool.toastShow(SFCDisManyMoreOrder.this,
								"还有未完成订单,请先将其完成");
						finish();
						break;
					} else {
						MyConfig.getMyConfig().setGoOnPickup(false);
					}
				}
				MyTool.playFailedSound();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisManyMoreOrder.this, strMsg);
				if (STATE == 0) {
					exListView.setVisibility(View.INVISIBLE);
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
		setContentView(R.layout.sfc_dis_more_order);
		init();
	}

	public void init() {
		asyncLoadImage = new AsyncLoadImage();
		Intent intent = getIntent();
		String[] strs = intent.getStringArrayExtra(MyConfig.TAG);
		Log.v("Intent", strs.toString());
		OP_CODE = strs[0];
		OPM_TIME = strs[1];
		listBean = new ArrayList<DisMoreBoxBeanP>();
		imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
		btnBack = (Button) findViewById(R.id.btn_back);
		exListView = (ExpandableListView) findViewById(R.id.exListView);
		tvCodeNum = (TextView) findViewById(R.id.tvCodeNum);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnCommit = (Button) findViewById(R.id.btn_commit);
		btnCommit.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		imgRefresh.setOnClickListener(this);

		exListView.setGroupIndicator(null);
		adapter = new MyAdapter();
		exListView.setAdapter(adapter);
		tvCodeNum.setText("配货单号 : " + OP_CODE);
		STATE = 0;
		getData();
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

			getData();
			break;
		case R.id.btn_commit:
			STATE = 1;
			getData();
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

	private class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return listBean.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return listBean.get(groupPosition).getListBean().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return listBean.get(groupPosition).getShelfNum();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return listBean.get(groupPosition).getListBean().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(SFCDisManyMoreOrder.this).inflate(
					R.layout.sfc_dis_dis_item, null);
			ImageView img = (ImageView) v.findViewById(R.id.img);
			if (!isExpanded) {
				img.setImageResource(R.drawable.img_up);
			} else {
				img.setImageResource(R.drawable.img_down);
			}
			TextView tvArea = (TextView) v.findViewById(R.id.tvArea);
			tvArea.setText(listBean.get(groupPosition).getShelfNum());
			return v;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(SFCDisManyMoreOrder.this).inflate(
					R.layout.sfc_dis_more_order_item, null);
			final ImageView imageView = (ImageView) v.findViewById(R.id.img);
			final String url = MyConfig.URL_PRE
					+ listBean.get(groupPosition).getListBean()
							.get(childPosition).getPic();
			imageView.setTag(url);
			Drawable drawable = asyncLoadImage.loadDrawable(url,
					new AsyncLoadImage.ImageCallback() {
						@Override
						public void imageLoad(Drawable image, String imageUrl) {
							if (imageUrl.equals(imageView.getTag())) {
								imageView.setImageDrawable(image);
							}
						}
					});
			if (drawable == null) {
				imageView.setImageResource(R.drawable.img_load);
			} else {
				imageView.setImageDrawable(drawable);
			}
			TextView tvCount = (TextView) v.findViewById(R.id.tv_count);
			TextView tvBarcode = (TextView) v.findViewById(R.id.tv_barcode);
			TextView tvDis = (TextView) v.findViewById(R.id.tv_dis);
			tvCount.setText(listBean.get(groupPosition).getListBean()
					.get(childPosition).getOpm_quantity());
			tvBarcode.setText(listBean.get(groupPosition).getListBean()
					.get(childPosition).getBarcode());
			tvDis.setText(listBean.get(groupPosition).getListBean()
					.get(childPosition).getSort_number());
			return v;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
	}

	private void getData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		switch (STATE) {
		case 0:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "order_type" },
							new String[] { OP_CODE, "2" }, "pdaPickupDetail"),
					handler);
			break;
		case 1:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "status", "order_type",
									"end_time" },
							new String[] { OP_CODE, "1", "2", OPM_TIME },
							"pdaPickupSubmit1"), handler);
			break;

		default:
			break;
		}
	}
}
