package com.sfcservice.pda.home;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.NewSkuBean;
import com.sfcservice.bean.OnShelfBean;
import com.sfcservice.component.AsyncLoadImage;
import com.sfcservice.component.HorizontalListView;
import com.sfcservice.img.Img;
import com.sfcservice.log.SFCNewProLog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCNewProduct extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private Button btn_back, btnClear, btnConfirm, btnLog;
	private EditText etBoxNum, etShelfNum, etQty;
	private TextView tvRecommend;
	private HorizontalListView listView;
	private ArrayList<NewSkuBean> listSkuBean;
	private MyAdapter adapter;
	private View vDot;
	private MyBroadCast broadCast;
	private Animation animation;
	private String showstr = "", pqty;
	private boolean down = false, checkRe = false;
	private int state = -1, subQty, qty, sysQty;
	private Button btnRefresh;
	private LinearLayout linePro, qtylayout;
	private AsyncLoadImage asyncLoadImage;
	private TextView tvShow;
	private OnShelfBean listBean = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				if (state == 0) {
					tvRecommend.setText("");
					vDot.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					etBoxNum.requestFocus();
					etBoxNum.setFocusable(true);
				}
				if (state == 1) {
					tvRecommend.setText("");
					etShelfNum.requestFocus();
					etShelfNum.setFocusable(true);
				}
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCNewProduct.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				if (state == 0) {
					vDot.setVisibility(View.VISIBLE);
					listView.setVisibility(View.VISIBLE);
					listView.startAnimation(animation);

					listBean = MyConnection.getMyConnection().getBoxInfo(
							listSkuBean);

					if (listBean.getType().equals("0")) {
						checkRe = true;
						qtylayout.setVisibility(View.VISIBLE);
						pqty = listBean.getQty();
						sysQty = Integer.parseInt(pqty);
						etQty.setText(pqty);
						etQty.requestFocus();
						etQty.setFocusable(true);

					} else {
						etShelfNum.requestFocus();
						etShelfNum.setFocusable(true);
					}
					tvRecommend.setText(listBean.getWscode());

					adapter.notifyDataSetChanged();
					break;
				}
				if (state == 1) {
					tvRecommend.setText(MyConnection.getMyConnection()
							.getRecommendShelf());
					etShelfNum.requestFocus();
					etShelfNum.setFocusable(true);
					break;
				}
				if (state == 2) {
					tvRecommend.setText("");
					etShelfNum.setText("");
					if (subQty < sysQty) {
						getBoxInfo();
						MyTool.playSuccessSound();

					} else {
						MyTool.playSuccessSound();
						etBoxNum.setText("");
						listView.setVisibility(View.GONE);
						vDot.setVisibility(View.GONE);
						etBoxNum.requestFocus();
						etBoxNum.setFocusable(true);
					}
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCNewProduct.this, strMsg);
				if (state == 0) {
					tvRecommend.setText("");
					vDot.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					etBoxNum.requestFocus();
					etBoxNum.setFocusable(true);
				}
				if (state == 1) {
					tvRecommend.setText("");
					etShelfNum.requestFocus();
					etShelfNum.setFocusable(true);
				}
				break;
			default:
				break;
			}
		};
	};

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("urovo.rcv.message")) {
				MyTool.playSound();
				byte[] barocode = intent.getByteArrayExtra("barocode");
				int barocodelen = intent.getIntExtra("length", 0);
				showstr = new String(barocode, 0, barocodelen);
				SFCFocus();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_new_product);
		init();
	}

	private void init() {
		asyncLoadImage = new AsyncLoadImage();
		listSkuBean = new ArrayList<NewSkuBean>();
		animation = AnimationUtils.loadAnimation(this, R.anim.right_in);
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		adapter = new MyAdapter();
		listView = (HorizontalListView) findViewById(R.id.sfc_list);
		vDot = findViewById(R.id.vDot);
		listView.setAdapter(adapter);
		etBoxNum = (EditText) findViewById(R.id.etBoxNum);
		etQty = (EditText) findViewById(R.id.etQty);
		tvRecommend = (TextView) findViewById(R.id.tvRecommend);
		etShelfNum = (EditText) findViewById(R.id.etShelfNum);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		qtylayout = (LinearLayout) findViewById(R.id.qtylayout);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btn_back = (Button) findViewById(R.id.btn_back);
		btnRefresh = (Button) findViewById(R.id.btnRefresh);
		btnLog = (Button) findViewById(R.id.btn_log);
		btnLog.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		etShelfNum.setOnFocusChangeListener(this);
		btnConfirm.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);

		qtylayout.setVisibility(View.GONE);

		btnRefresh.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.shake_x));
		etShelfNum.setOnFocusChangeListener(this);
		etBoxNum.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				Log.i("FFF", String.valueOf(actionId));
				// TODO Auto-generated method stub
				if (actionId == 0 || actionId == 6) {
					// Log.i("EEE",String.valueOf(event.getAction()));
					// if (event != null && event.getAction() == 1) {
					// return true;
					// }
					Log.i("B", etBoxNum.getText().toString());
					if (MyConfig.getMyConfig().getNetGood()) {
						if (etBoxNum.getText().toString().equals("")) {
							MyTool.toastShow(SFCNewProduct.this, "请输入产品箱号");
						} else {
							MyTool.hideInputKeyBroad(SFCNewProduct.this);
							getBoxInfo();
						}
						return true;
					} else {
						MyTool.toastShow(SFCNewProduct.this, "暂无网络");
						return true;
					}
				}
				return false;
			}

		});

		etQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == 0 || actionId == 6) {
					if (event != null && event.getAction() == 1) {
						return true;
					}
					checkQty();
					// etShelfNum.requestFocus();
					// etShelfNum.setFocusable(true);
					// return true;
				}
				return false;
			}

		});
		etShelfNum.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				// TODO Auto-generated method stub
				if (actionId == 0) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								etShelfNum.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					btnConfirm.requestFocus();
					btnConfirm.setFocusable(true);
				}
				return false;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			down = true;
		} else {
			down = false;
		}
		// Log.i("TTTT", String.valueOf(keyCode));
		if (keyCode == 113) {
			MyTool.hideInputKeyBroad(this);
			etBoxNum.setText("");
			etShelfNum.setText("");
			tvRecommend.setText("");
			listView.setVisibility(View.GONE);
			vDot.setVisibility(View.GONE);
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.etShelfNum) {
			if (!hasFocus && down) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(etShelfNum.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}

				btnConfirm.requestFocus();
				btnConfirm.setFocusable(true);
			}
			if (hasFocus) {
				down = false;
			}
			return;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		myClick(v);
	}

	private boolean checkQty() {

		String inputQty = etQty.getText().toString();
		if (checkRe == false) {
			return true;
		}
		if (inputQty.equals("")) {
			MyTool.toastShow(SFCNewProduct.this, "请输入上架数量");
			etQty.requestFocus();
			etQty.setFocusable(true);
			return false;
		}

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(inputQty.trim());
		if (!isNum.matches()) {
			MyTool.playFailedSound();
			MyTool.toastShow(SFCNewProduct.this, "请输入数字！");
			etQty.requestFocus();
			etQty.setFocusable(true);
			return false;
		}
		int qty = Integer.parseInt(inputQty);

		if (qty > Integer.parseInt(pqty) || qty <= 0) {
			MyTool.playFailedSound();
			MyTool.toastShow(SFCNewProduct.this, "输入的数量大于箱子SKU数量/小于等于0！");
			etQty.requestFocus();
			etQty.setFocusable(true);
			return false;
		}
		return true;

	}

	private void myClick(View v) {
		MyTool.hideInputKeyBroad(this);
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btnClear:
			listView.setVisibility(View.GONE);
			vDot.setVisibility(View.GONE);
			etBoxNum.setText("");
			etShelfNum.setText("");
			tvRecommend.setText("");
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
			break;
		case R.id.btnRefresh:
			if (!MyConfig.getMyConfig().getNetGood()) {
				MyTool.toastShow(SFCNewProduct.this, "暂无网络");
				return;
			}
			if (etBoxNum.getText().toString().trim().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "请输入产品箱号");
				break;
			}
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);
			state = 1;
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "container_code" },
							new String[] { etBoxNum.getText().toString() },
							"getRecommendWs"), handler);

			break;
		case R.id.btnConfirm:
			if (!MyConfig.getMyConfig().getNetGood()) {
				MyTool.toastShow(SFCNewProduct.this, "暂无网络");
				return;
			}
			if (etBoxNum.getText().toString().equals("")
					|| etShelfNum.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "您还有未输入选项,请核查后输入");

			}
			if (checkQty() == false) {

				break;
			}
			// 如果有网络直接提交

			if (MyConfig.getMyConfig().getNetGood()) {
				state = 2;
				linePro.setVisibility(View.VISIBLE);
				linePro.requestFocus();
				linePro.setFocusable(true);
				if (checkRe) {
					subQty = Integer
							.parseInt(etQty.getText().toString().trim());
				}

				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection().writeJsonWithUserInfo(
								new String[] { "container_code",
										"putaway_ws_code", "qty" },
								new String[] {
										etBoxNum.getText().toString().trim(),
										etShelfNum.getText().toString().trim(),
										etQty.getText().toString().trim() },
								"pdaSubmitPutawayNew"), handler);
				break;
			}

			// 获取系统时间

			String date = MyTool.getTime();

			// 存储数据

			MyConnection.getMyConnection()
					.saveData(
							"new_product",
							new String[] { "user_login_id", "box_num",
									"shelf_num", "storage_date", "upload_date",
									"status" },
							new String[] {
									MyConfig.getMyConfig().getUsers()[0],
									etBoxNum.getText().toString(),
									etShelfNum.getText().toString(), date,
									"00-00", "1" });
			MyTool.playSuccessSound();
			listView.setVisibility(View.GONE);
			vDot.setVisibility(View.GONE);
			etBoxNum.setText("");
			etShelfNum.setText("");
			tvRecommend.setText("");
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
			break;
		case R.id.btn_log:
			Intent intent = new Intent(this, SFCNewProLog.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus() {
		if (etBoxNum.hasFocus()) {
			etBoxNum.setText("");
			etBoxNum.append(showstr);

			if (MyConfig.getMyConfig().getNetGood()) {
				getBoxInfo();
			} else {
				etShelfNum.requestFocus();
				etShelfNum.setFocusable(true);
			}
			return;
		}

		if (etShelfNum.hasFocus()) {
			etShelfNum.setText("");
			etShelfNum.append(showstr);
			btnConfirm.requestFocus();
			btnConfirm.setFocusable(true);
			return;
		}

	}

	// 根据箱号获取箱子信息
	public void getBoxInfo() {
		state = 0;
		listSkuBean = new ArrayList<NewSkuBean>();
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "container_code" },
						new String[] { etBoxNum.getText().toString() },
						"pdaPutawayNew"), handler);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listSkuBean.size();
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
				convertView = LayoutInflater.from(SFCNewProduct.this).inflate(
						R.layout.sfc_new_sku_item, null);

				// 获取布局里面的ImageView组件
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.img);

				// 获取当前的图片的url地址
				final String url = MyConfig.URL_PRE
						+ listSkuBean.get(position).getPic();
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
						+ listSkuBean.get(position).getPic();
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
			if (position == listSkuBean.size() - 1) {
				convertView.findViewById(R.id.vLine).setVisibility(
						View.INVISIBLE);
			}
			final ImageView img = (ImageView) convertView
					.findViewById(R.id.img);
			img.setDrawingCacheEnabled(true);
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					MyConfig.getMyConfig().setBitmap(img.getDrawingCache());
					Intent intent = new Intent(SFCNewProduct.this, Img.class);
					startActivity(intent);
				}
			});

			TextView tvSKU = (TextView) convertView.findViewById(R.id.tvSKU);
			TextView tvID = (TextView) convertView.findViewById(R.id.tvID);
			TextView tvCount = (TextView) convertView
					.findViewById(R.id.tvCount);
			tvSKU.setText(listSkuBean.get(position).getSku());
			tvID.setText(listSkuBean.get(position).getId());
			tvCount.setText(listSkuBean.get(position).getCount());
			return convertView;
		}

	}
}
