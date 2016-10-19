package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sfcservice.bean.DstributionBean;
import com.sfcservice.component.AsyncLoadImage;
import com.sfcservice.component.MyDialog;
import com.sfcservice.component.MyDialog.Dialogcallback;
import com.sfcservice.img.Img;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisManyOneOrder extends Activity implements OnClickListener,
		OnItemClickListener, OnScrollListener {
	private ListView listView;
	private LinearLayout linePro;
	private Button btnBack, btnCommit;
	private TextView tvShow;
	private TextView tvCodeNum;
	private ImageView imgRefresh;
	private MyAdapter adapter;
	private String OP_CODE, PRODUCT_ID;
	private int myposition;
	private ArrayList<DstributionBean> listBean;
	private AsyncLoadImage asyncLoadImage;
	private View footView;
	/**
	 * 0获取初始数据,1提交成功,2删除成功
	 */
	private int STATE = -1;
	private boolean complete = false;
	private boolean loading = false;
	private boolean isVisible = false;
	private int PAGE = 0;
	private MyDialog dialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisManyOneOrder.this, "连接服务器失败");
				if (STATE == 0) {
					listView.setVisibility(View.INVISIBLE);
					imgRefresh.setVisibility(View.VISIBLE);
				}
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					listView.setVisibility(View.VISIBLE);
					imgRefresh.setVisibility(View.INVISIBLE);
					if (PAGE == 1) {
						listBean = new ArrayList<DstributionBean>();
					}
					int SIZE = listBean.size();
					MyConnection.getMyConnection().getDisManyOneOrder(listBean);
					int SIZE1 = listBean.size();
					if (SIZE1 - SIZE < MyConfig.LOADING_ITEM) {
						complete = true;
						listView.removeFooterView(footView);
						adapter.notifyDataSetChanged();
						return;
					}
					adapter.notifyDataSetChanged();
					loading = false;
					if (PAGE == 1) {
						listView.setSelection(0);
					}
					break;
				}
				if (STATE == 1) {
					// 提交成功
					MyTool.playSuccessSound();
					MyConfig.getMyConfig().setOrderCommit(true);
					finish();
					break;
				}
				if (STATE == 2) {
					MyTool.playSuccessSound();
					listBean.remove(myposition);
					adapter.notifyDataSetChanged();
					MyTool.toastShow(SFCDisManyOneOrder.this, "删除成功");
					if (listBean.size() == 0) {
						MyConfig.getMyConfig().setOrderDeleteAll(true);
						finish();
						break;
					}
					if (!complete) {
						loading = false;
						PAGE = 0;
						// 如果没有全部加载完毕，并且在还可以拉的情况下删除
						if (!isVisible) {
							loading = true;
							STATE = 0;
							PAGE++;
							getData();
						}
					}
					break;
				}
				break;
			case MyConfig.RESULTF:
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					// 说明没有下一个分页
					complete = true;
					listView.removeFooterView(footView);
					MyTool.toastShow(SFCDisManyOneOrder.this, "全部加载完毕");
					break;
				}
				MyTool.playFailedSound();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisManyOneOrder.this, strMsg);
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
		setContentView(R.layout.sfc_dis_oder);
		init();
	}

	public void init() {
		asyncLoadImage = new AsyncLoadImage();
		Intent intent = getIntent();
		OP_CODE = intent.getStringExtra(MyConfig.TAG);

		listBean = new ArrayList<DstributionBean>();
		imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
		btnBack = (Button) findViewById(R.id.btn_back);
		listView = (ListView) findViewById(R.id.listView);
		tvCodeNum = (TextView) findViewById(R.id.tvCodeNum);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnCommit = (Button) findViewById(R.id.btn_commit);
		footView = LayoutInflater.from(this).inflate(R.layout.list_foot_view,
				null);
		btnCommit.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		imgRefresh.setOnClickListener(this);
		listView.setOnScrollListener(this);
		adapter = new MyAdapter();
		listView.addFooterView(footView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		tvCodeNum.setText("配货单号 : " + OP_CODE);
		dialog = new MyDialog(this);
		dialog.setConfirmText("删除");
		dialog.setDialogCallback(new Dialogcallback() {

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				STATE = 2;
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);

				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection().writeJsonWithUserInfo(
								new String[] { "product_id", "op_code",
										"orders_code" },
								new String[] {
										PRODUCT_ID,
										OP_CODE,
										listBean.get(myposition)
												.getOrders_code() },
								"pdaPickupDelOne"), handler);
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
		// STATE = 0;
		// getData();
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
							new String[] { OP_CODE }, "pdaPickupDetail"),
					handler);
			break;
		case R.id.btn_commit:
			STATE = 1;
			linePro.setVisibility(View.VISIBLE);
			linePro.setFocusable(true);
			linePro.requestFocus();
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "status" },
							new String[] { OP_CODE, "1" }, "pdaPickupSubmit1"),
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (position == listBean.size()) {
			return;
		}
		myposition = position;
		PRODUCT_ID = listBean.get(position).getProductId();
		dialog.setContent("确定删除" + "货架号为 "
				+ listBean.get(position).getShelfNum() + " ; 数量为 "
				+ listBean.get(position).getCount() + " 的配货单?");
		dialog.show();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		isVisible = (firstVisibleItem + visibleItemCount == totalItemCount);
		if (firstVisibleItem + visibleItemCount == totalItemCount && !complete
				&& !loading) {
			// 开线程去下载网络数据
			loading = true;
			PAGE++;
			STATE = 0;
			getData();
		}
	}

	public void getData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		switch (STATE) {
		case 0:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "page", "order_type" },
							new String[] { OP_CODE, PAGE + "", "1" },
							"pdaPickupDetail"), handler);
			break;

		default:
			break;
		}
	}

	private class MyAdapter extends BaseAdapter {

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
			// TODO Auto-generated method stub
			if (convertView == null) {
				// 获取布局
				convertView = LayoutInflater.from(SFCDisManyOneOrder.this)
						.inflate(R.layout.sfc_dis_oder_item, null);

				// 获取布局里面的ImageView组件
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.img);

				// 获取当前的图片的url地址
				final String url = MyConfig.URL_PRE
						+ listBean.get(position).getPic();
				// 为图片设置一个tag 这个很重要这样的目的是为了控制每个ImageView组件都显示对应的图片
				imageView.setTag(url);
				// 开始异步加载图片
				Drawable drawable = asyncLoadImage.loadDrawable(url,
						new AsyncLoadImage.ImageCallback() {
							@Override
							public void imageLoad(Drawable image,
									String imageUrl) {
								// 判断当前的url地址是否为当前组件的url地址 是则加载图片
								if (imageUrl.equals(imageView.getTag())) {
									imageView.setImageDrawable(image);
								}
							}
						});
				if (drawable == null) { // 这里也很重要 如果没有则设置一个默认的图片
										// 如果不设置表示后果很严重。我就是因为这个地方浪费了一上午的时间
					imageView.setImageResource(R.drawable.img_load);
				} else {// 这里就是加载图片啦。。
					imageView.setImageDrawable(drawable);
				}
			} else {
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.img);
				final String url = MyConfig.URL_PRE
						+ listBean.get(position).getPic();
				imageView.setTag(url);
				Drawable drawable = asyncLoadImage.loadDrawable(url,
						new AsyncLoadImage.ImageCallback() {
							@Override
							public void imageLoad(Drawable image,
									String imageUrl) {
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
			}
			final ImageView img = (ImageView) convertView
					.findViewById(R.id.img);
			img.setDrawingCacheEnabled(true);
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					MyConfig.getMyConfig().setBitmap(img.getDrawingCache());
					Intent intent = new Intent(SFCDisManyOneOrder.this,
							Img.class);
					startActivity(intent);
				}
			});

			TextView tvSC = (TextView) convertView
					.findViewById(R.id.ShelfAndCount);
			TextView tvCN = (TextView) convertView
					.findViewById(R.id.ProClientAndName);
			tvSC.setText("货架号 : " + listBean.get(position).getShelfNum()
					+ " ;数量 : " + listBean.get(position).getCount());
			tvCN.setText("客户ID号 ： " + listBean.get(position).getClientProNum());
			return convertView;
		}

	}
}
