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

public class SFCDistributionOrder extends Activity implements OnClickListener,
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
	 * 0��ȡ��ʼ����,1�ύ�ɹ�,2ɾ���ɹ�
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
				tvShow.setText("���ڼ������...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDistributionOrder.this, "���ӷ�����ʧ��");
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
					MyConnection.getMyConnection().getDisOrder(listBean);
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
					// �ύ�ɹ�
					MyConfig.getMyConfig().setOrderCommit(true);
					finish();
					break;
				}
				if (STATE == 2) {
					MyTool.playSuccessSound();
					listBean.remove(myposition);
					adapter.notifyDataSetChanged();
					MyTool.toastShow(SFCDistributionOrder.this, "ɾ���ɹ�");
					if (listBean.size() == 0) {
						MyConfig.getMyConfig().setOrderDeleteAll(true);
						finish();
						break;
					}
					if (!complete) {
						loading = false;
						PAGE = 0;
						// ���û��ȫ��������ϣ������ڻ��������������ɾ��
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
					// ˵��û����һ����ҳ
					complete = true;
					listView.removeFooterView(footView);
					MyTool.toastShow(SFCDistributionOrder.this, "ȫ���������");
					break;
				}
				MyTool.playFailedSound();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDistributionOrder.this, strMsg);
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
		tvCodeNum.setText("������� : " + OP_CODE);
		dialog = new MyDialog(this);
		dialog.setConfirmText("ɾ��");
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
								new String[] { "product_id", "op_code" },
								new String[] { PRODUCT_ID, OP_CODE },
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
				MyTool.toastShow(this, "�������ӷ�����,���Ե�...");
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
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
				// ��ȡ����
				convertView = LayoutInflater.from(SFCDistributionOrder.this)
						.inflate(R.layout.sfc_dis_oder_item, null);

				// ��ȡ���������ImageView���
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.img);

				// ��ȡ��ǰ��ͼƬ��url��ַ
				final String url = MyConfig.URL_PRE
						+ listBean.get(position).getPic();
				// ΪͼƬ����һ��tag �������Ҫ������Ŀ����Ϊ�˿���ÿ��ImageView�������ʾ��Ӧ��ͼƬ
				imageView.setTag(url);
				// ��ʼ�첽����ͼƬ
				Drawable drawable = asyncLoadImage.loadDrawable(url,
						new AsyncLoadImage.ImageCallback() {
							@Override
							public void imageLoad(Drawable image,
									String imageUrl) {
								// �жϵ�ǰ��url��ַ�Ƿ�Ϊ��ǰ�����url��ַ �������ͼƬ
								if (imageUrl.equals(imageView.getTag())) {
									imageView.setImageDrawable(image);
								}
							}
						});
				if (drawable == null) { // ����Ҳ����Ҫ ���û��������һ��Ĭ�ϵ�ͼƬ
										// ��������ñ�ʾ��������ء��Ҿ�����Ϊ����ط��˷���һ�����ʱ��
					imageView.setImageResource(R.drawable.img_load);
				} else {// ������Ǽ���ͼƬ������
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
					Intent intent = new Intent(SFCDistributionOrder.this,
							Img.class);
					startActivity(intent);
				}
			});

			TextView tvSC = (TextView) convertView
					.findViewById(R.id.ShelfAndCount);
			TextView tvCN = (TextView) convertView
					.findViewById(R.id.ProClientAndName);
			tvSC.setText("���ܺ� : " + listBean.get(position).getShelfNum()
					+ " ;���� : " + listBean.get(position).getCount());
			tvCN.setText("�ͻ�ID�� �� " + listBean.get(position).getClientProNum());
			return convertView;
		}

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
		dialog.setContent("ȷ��ɾ��" + "���ܺ�Ϊ "
				+ listBean.get(position).getShelfNum() + " ; ����Ϊ "
				+ listBean.get(position).getCount() + " �������?");
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
			// ���߳�ȥ������������
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
							new String[] { "op_code", "page" },
							new String[] { OP_CODE, PAGE + "" },
							"pdaPickupDetail"), handler);
			break;

		default:
			break;
		}
	}
}
