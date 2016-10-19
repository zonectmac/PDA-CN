package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
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

import com.sfcservice.bean.BTBean;
import com.sfcservice.component.AsyncLoadImage;
import com.sfcservice.component.HorizontalListView;
import com.sfcservice.img.Img;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCBindingTransfer extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private Button btn_back, btnTransfer, btnClear;
	private EditText etBoxNum, etCar;
	private TextView tvBS,tvBSNum;
	private HorizontalListView listView;
	private Animation animation;
	private View vDot;
	private MyAdapter myAdapter;
	private MyBroadCast broadCast;
	private String showstr = "";
	private String wsCode = "";
	private String containerCode2="";
	private ArrayList<BTBean> btBeanList;
	private boolean scan = false, down = false;
	private AsyncLoadImage asyncLoadImage;
	private int STATE = 0;
	private LinearLayout linePro;
	private TextView tvShow;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCBindingTransfer.this, "连接服务器失败");
				if (STATE == 0) {
					vDot.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					tvBS.setText("货架/箱子 : ");
					tvBSNum.setText("");
				}
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				switch (STATE) {
				case 0:
					btBeanList = new ArrayList<BTBean>();
					String[] strs=MyConnection.getMyConnection().getBTBoxInfo(btBeanList);
					wsCode=strs[0];
					containerCode2=strs[1];
					myAdapter.notifyDataSetChanged();
					listView.setVisibility(View.VISIBLE);
					vDot.setVisibility(View.VISIBLE);
					listView.startAnimation(animation);
					
					if(etBoxNum.getText().toString().toUpperCase().equals(containerCode2)){
						tvBS.setText("货架编号 : ");
						tvBSNum.setText(wsCode);
					}
					if(etBoxNum.getText().toString().toUpperCase().equals(wsCode)){
						tvBS.setText("箱子编号 : ");
						tvBSNum.setText(containerCode2);
					}
					etCar.setFocusable(true);
					etCar.requestFocus();
					break;
				case 1:
					MyTool.playSuccessSound();
					etBoxNum.setText("");
					listView.setVisibility(View.GONE);
					vDot.setVisibility(View.GONE);
					tvBS.setText("货架/箱子");
					tvBSNum.setText("");
					etCar.setText("");
					etBoxNum.requestFocus();
					etBoxNum.setFocusable(true);
					break;
				default:
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCBindingTransfer.this, strMsg);

				if (STATE == 0) {
					vDot.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					btnTransfer.setVisibility(View.VISIBLE);
					tvBS.setText("货架/箱子 : ");
					tvBSNum.setText("");
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_binding_transfer);
		init();
	}

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		btBeanList = new ArrayList<BTBean>();
		asyncLoadImage = new AsyncLoadImage();
		vDot = findViewById(R.id.vDot);
		animation = AnimationUtils.loadAnimation(this, R.anim.right_in);
		etCar = (EditText) findViewById(R.id.etCar);
		tvBS=(TextView)findViewById(R.id.tvBS);
		tvBSNum=(TextView)findViewById(R.id.tvBSNum);
		btnClear = (Button) findViewById(R.id.btnClear);
		listView = (HorizontalListView) findViewById(R.id.horListView);
		etBoxNum = (EditText) findViewById(R.id.etBoxNum);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btn_back = (Button) findViewById(R.id.btn_back);
		btnTransfer = (Button) findViewById(R.id.btnTransfer);
		btn_back.setOnClickListener(this);
		btnTransfer.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnClear.setOnFocusChangeListener(this);
		btnTransfer.setOnFocusChangeListener(this);
		etCar.setOnFocusChangeListener(this);
		etBoxNum.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == 0 || actionId == 6) {
					if (event != null && event.getAction() == 1) {
						return true;
					}
					if (MyConfig.getMyConfig().getNetGood()) {
						MyTool.hideInputKeyBroad(SFCBindingTransfer.this);
						getInfo(0);
						return true;
					}
				}
				return false;
			}
		});

		myAdapter = new MyAdapter();
		listView.setAdapter(myAdapter);

		etCar.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == 0) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(etCar.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					btnTransfer.requestFocus();
					btnTransfer.setFocusable(true);
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
		if(keyCode==113){
			MyTool.hideInputKeyBroad(this);
			etBoxNum.setText("");
			listView.setVisibility(View.GONE);
			vDot.setVisibility(View.GONE);
			tvBS.setText("货架/箱子");
			tvBSNum.setText("");
			etCar.setText("");
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.etCar) {
			if (!hasFocus && down) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(etCar.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
				btnTransfer.requestFocus();
				btnTransfer.setFocusable(true);
				return;
			}
			return;
		}
		if(v.getId()==R.id.btnTransfer){
			if(hasFocus){
				if(scan){
					scan=false;
					return;
				}
			}
			
		}
		if (v.isInTouchMode() && hasFocus) {
			down = false;
			myClick(v);
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

	private void myClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			MyTool.hideInputKeyBroad(this);
			finish();
			break;
		case R.id.btnTransfer:
			MyTool.hideInputKeyBroad(this);
			if(scan){
				scan=false;
				break;
			}
			if (etBoxNum.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "请输入箱号");
				break;
			}
//			if (tvBSNum.getText().toString().equals("")) {
//				MyTool.playFailedSound();
//				MyTool.toastShow(this, "箱子未绑定货架号,不允许转移");
//				break;
//			}
			if (etCar.getText().toString().equals("")) {
				MyTool.playFailedSound();
				MyTool.toastShow(this, "请输入中转箱号");
				break;
			}
			getInfo(1);
			break;
		case R.id.btnClear:
			etBoxNum.setText("");
			listView.setVisibility(View.GONE);
			vDot.setVisibility(View.GONE);
			tvBS.setText("货架/箱子");
			tvBSNum.setText("");
			etCar.setText("");
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
			break;
		default:
			break;
		}
	}

	public void getInfo(int i) {

		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		switch (i) {
		case 0:
			STATE = 0;
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "container_code" },
							new String[] { etBoxNum.getText().toString() },
							"transferBindingCheckEx"), handler);
			break;
		case 1:
			STATE = 1;
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeTJsonWithUserInfo(
							btBeanList,
							containerCode2,
							wsCode,
							etCar.getText().toString(),
							"transfer"), handler);
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
			getInfo(0);
			return;
		}
		if (etCar.hasFocus()) {
			scan=true;
			etCar.setText("");
			etCar.append(showstr);
			btnTransfer.requestFocus();
			btnTransfer.setFocusable(true);
			return;
		}
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return btBeanList.size();
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
				convertView = LayoutInflater.from(SFCBindingTransfer.this)
						.inflate(R.layout.sfc_binding_t_item, null);

				// 获取布局里面的ImageView组件
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.img);

				// 获取当前的图片的url地址
				final String url = MyConfig.URL_PRE
						+ btBeanList.get(position).getPic();
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
						+ btBeanList.get(position).getPic();
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
			if (position == btBeanList.size() - 1) {
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
					Intent intent = new Intent(SFCBindingTransfer.this,
							Img.class);
					startActivity(intent);
				}
			});

			TextView tvSKU = (TextView) convertView.findViewById(R.id.tvSKU);
			TextView tvHoldCount=(TextView)convertView.findViewById(R.id.tvHoldCount);
			TextView tvStatus=(TextView)convertView.findViewById(R.id.tvStatus);
			final EditText etCount = (EditText) convertView
					.findViewById(R.id.etCount);
			Button btnDel = (Button) convertView.findViewById(R.id.btnDel);
			tvHoldCount.setText(btBeanList.get(position).getHoldCount());
			tvSKU.setText(btBeanList.get(position).getSku());
			etCount.setText(btBeanList.get(position).getCount());
			tvStatus.setText(btBeanList.get(position).getStatus());
			final int index = position;
			btnDel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (btBeanList.size() == 1) {
						MyTool.toastShow(SFCBindingTransfer.this, "必须保留一个SKU");
						return;
					}
					btBeanList.remove(index);
					myAdapter.notifyDataSetChanged();
				}
			});
			etCount.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (!hasFocus) {
						btBeanList.get(index).setCount(
								etCount.getText().toString());
					}
				}
			});
			return convertView;
		}

	}

}
