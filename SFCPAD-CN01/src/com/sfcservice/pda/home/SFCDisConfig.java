package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.DisBean;
import com.sfcservice.bean.DisMoreBean;
import com.sfcservice.component.SFCDateDialog;
import com.sfcservice.component.SFCDisNoDataDialog;
import com.sfcservice.component.SFCDisNoDataDialog.Dialogcallback;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisConfig extends Activity implements OnClickListener,
		OnEditorActionListener {
	private EditText etShelfNum;
	private EditText etBoxNum;
	private Button btnBack;
	private Button btnMore;
	private LinearLayout linePro;
	private LinearLayout line1, line2, line3, line01, line02;
	private TextView tvShow;
	private TextView tvDate;
	private TextView tvAreaShow;
	private LinearLayout lineDate;
	private ImageView img1, img2, img3, img01, img02;
	private PopupWindow p;
	private View pRootView;
	/**
	 * 2表示点击一票多件多SKU获取数据
	 */
	private int STATE = 0;
	private ArrayList<DisBean> listBean;
	private SFCDateDialog dialogDate;
	/**
	 * 表示一票一件0还是一票多件单1，多2
	 */
	private String T = "0";
	/**
	 * 表示升序与降序
	 */
	private String SORT = "asc";
	private SFCDisNoDataDialog dialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisConfig.this, "连接服务器失败");
				if(STATE==2){
					tvAreaShow.setVisibility(View.GONE);
				}
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					if (T.equals("0")) {
						System.out.println("--------->>.");
						String[] strs = MyConnection.getMyConnection()
								.getDistributionInfo();
						if (strs == null) {
							dialog.show();
							break;
						}
						String[] strsI = new String[strs.length + 5];
						for (int i = 0; i < strs.length; i++) {
							strsI[i] = strs[i];
						}
						strsI[strs.length] = T + "";
						strsI[strs.length + 1] = SORT;
						strsI[strs.length + 2] = etShelfNum.getText()
								.toString();
						strsI[strs.length + 3] = etBoxNum.getText().toString();
						strsI[strs.length + 4] = tvDate.getText().toString();
						Intent intent = new Intent(SFCDisConfig.this,
								SFCDisOnLine.class);
						intent.putExtra("S", strsI);
						startActivity(intent);
						break;
					}
					if (T.equals("1")) {
						String[] strInits = MyConnection.getMyConnection()
								.getDistributionMoreInfo(
										new ArrayList<DisMoreBean>());
						if (strInits.length == 1) {
							dialog.show();
							break;
						}
						String[] strs = new String[4];
						strs[0] = etBoxNum.getText().toString();
						strs[1] = etShelfNum.getText().toString();
						strs[2] = T;
						strs[3] = SORT;

						Intent intent = new Intent(SFCDisConfig.this,
								SFCDisOnlineManyOneSKU.class);
						intent.putExtra("S", strs);
						startActivity(intent);
						break;
					}

					if (T.equals("2")) {
						if (MyConnection.getMyConnection().isDisMorehave()) {
							dialog.show();
							break;
						}
						Intent intent = new Intent(SFCDisConfig.this,
								SFCDisOnlineManyMoreSKU.class);

						String[] strs = new String[4];
						strs[0] = etBoxNum.getText().toString();
						strs[1] = etShelfNum.getText().toString();
						strs[2] = T;
						strs[3] = SORT;
						intent.putExtra("S", strs);
						startActivity(intent);
						break;
					}
					break;
				}
				if (STATE == 1) {
					Intent intent = new Intent(SFCDisConfig.this,
							SFCDisDis.class);
					intent.putExtra(MyConfig.TAG, T);
					startActivity(intent);
					break;
				}
				if (STATE == 2) {
					listBean = new ArrayList<DisBean>();
					MyConnection.getMyConnection().getDisArea(listBean);
					MyConfig.getMyConfig().setListDisAll(listBean);
					Intent intentXX = new Intent(SFCDisConfig.this,
							SFCDisArea.class);
					startActivity(intentXX);
					T = "2";
					img1.setImageResource(R.drawable.unchecked);
					img2.setImageResource(R.drawable.unchecked);
					img3.setImageResource(R.drawable.checked);
					line01.requestFocus();
					line01.setFocusable(true);
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisConfig.this, strMsg);
				if(STATE==2){
					tvAreaShow.setVisibility(View.GONE);
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_dis_config);
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ArrayList<DisBean> listR=MyConfig.getMyConfig().getListDisRemain();
		if (listR != null && T.equals("2")) {
			tvAreaShow.setVisibility(View.VISIBLE);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < listR.size(); i++) {
				sb.append(listR.get(i).getAbo_name() + " ; ");
			}
			tvAreaShow.setText("配货区域 ： " + sb.toString());
		}
	}

	public void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);

		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		etShelfNum = (EditText) findViewById(R.id.etShelfNum);
		tvDate = (TextView) findViewById(R.id.tvDate);
		etBoxNum = (EditText) findViewById(R.id.etBoxNum);
		btnBack = (Button) findViewById(R.id.btn_back);
		line01 = (LinearLayout) findViewById(R.id.line01);
		line02 = (LinearLayout) findViewById(R.id.line02);
		line1 = (LinearLayout) findViewById(R.id.line1);
		line2 = (LinearLayout) findViewById(R.id.line2);
		line3 = (LinearLayout) findViewById(R.id.line3);
		img01 = (ImageView) findViewById(R.id.img01);
		img02 = (ImageView) findViewById(R.id.img02);
		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		btnMore = (Button) findViewById(R.id.btn_more);
		lineDate = (LinearLayout) findViewById(R.id.lineDate);
		tvAreaShow = (TextView) findViewById(R.id.tvAreaShow);
		tvAreaShow.setOnClickListener(this);
		lineDate.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		line01.setOnClickListener(this);
		line02.setOnClickListener(this);
		line1.setOnClickListener(this);
		line2.setOnClickListener(this);
		line3.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		tvDate.setText(MyTool.getSFCTime());

		etBoxNum.setOnKeyListener(new OnKeyListener() {
			boolean bool = false;

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == 66) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						bool = true;
					}
					if (event.getAction() == KeyEvent.ACTION_UP) {
						if (!bool) {
							bool = false;
							return true;
						}
						bool = false;
					}
				}
				return false;
			}
		});
		etShelfNum.setOnEditorActionListener(this);

		dialog = new SFCDisNoDataDialog(this);
		dialog.setConfirmText("确定");
		dialog.setContent("暂时没有任何产品可供配货");
		dialog.setDialogCallback(new Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub

			}
		});
		dialogDate = new SFCDateDialog(this);
		dialogDate.setDialogCallback(new SFCDateDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				tvDate.setText(dialogDate.getTime());
				etShelfNum.setFocusable(true);
				etShelfNum.requestFocus();
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub
				etShelfNum.setFocusable(true);
				etShelfNum.requestFocus();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == 113) {
			etShelfNum.setText("");
			etBoxNum.setText("");
			img1.setImageResource(R.drawable.checked);
			img2.setImageResource(R.drawable.unchecked);
			img01.setImageResource(R.drawable.checked);
			img02.setImageResource(R.drawable.unchecked);
			T = "0";
			SORT = "asc";
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
		case R.id.etShelfNum:
			if (actionId == 0 || actionId == 6) {
				if (event != null && event.getAction() == 1) {
					return true;
				}
				if (etBoxNum.getText().toString().equals("")) {
					MyTool.playFailedSound();
					MyTool.toastShow(SFCDisConfig.this, "请输入配货箱号");
					etBoxNum.requestFocus();
					etBoxNum.setFocusable(true);
					return true;
				}
				if (etShelfNum.getText().toString().equals("")) {
					MyTool.playFailedSound();
					MyTool.toastShow(SFCDisConfig.this, "请输入货架号");
					return true;
				}
				MyTool.hideInputKeyBroad(SFCDisConfig.this);
				getData(T, SORT);
				return true;
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
		case R.id.line01:
			SORT = "asc";
			img01.setImageResource(R.drawable.checked);
			img02.setImageResource(R.drawable.unchecked);
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
			break;
		case R.id.line02:
			SORT = "desc";
			img01.setImageResource(R.drawable.unchecked);
			img02.setImageResource(R.drawable.checked);
			etBoxNum.requestFocus();
			etBoxNum.setFocusable(true);
			break;
		case R.id.line1:
			T = "0";
			img1.setImageResource(R.drawable.checked);
			img2.setImageResource(R.drawable.unchecked);
			img3.setImageResource(R.drawable.unchecked);
			line01.requestFocus();
			line01.setFocusable(true);
			tvAreaShow.setVisibility(View.GONE);
			break;
		case R.id.line2:
			T = "1";
			img1.setImageResource(R.drawable.unchecked);
			img2.setImageResource(R.drawable.checked);
			img3.setImageResource(R.drawable.unchecked);
			line01.requestFocus();
			line01.setFocusable(true);
			tvAreaShow.setVisibility(View.GONE);
			break;
		case R.id.line3:
			linePro.setVisibility(View.VISIBLE);
			linePro.requestFocus();
			linePro.setFocusable(true);
			STATE = 2;
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] {}, null, "pdaUserArea"), handler);
			break;
		case R.id.btn_more:
			popShow();
			break;
		case R.id.btn1:
			p.dismiss();
			STATE = 1;
			getDisOrderData(1);
			break;
		case R.id.lineDate:
			dialogDate.show();
			break;
		case R.id.tvAreaShow:
			Intent intent = new Intent(SFCDisConfig.this, SFCDisArea.class);
			intent.putExtra("S", MyConfig.getMyConfig().getInts());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param s
	 *            0一票一件还是一票多件单1，多2
	 * @param s1
	 *            0升序还是降序1
	 */
	private void getData(String T, String SORT) {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		STATE = 0;
		if (T.equals("2")) {
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeDisMoreJsonWithUserInfo(
									new String[] { "ws_code", "order_type",
											"sortBy", "container_code", "pass",
											"end_time" },
									new String[] {
											etShelfNum.getText().toString(), T,
											SORT,
											etBoxNum.getText().toString(), "1",
											//MyTool.getSFCTimeAddFive() },
											dialogDate.getTime()},
									MyConfig.getMyConfig().getListDisRemain(),
									"pdaPickup"), handler);
			return;
		}
		MyConnection.getMyConnection().acceptServerWithImg(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "ws_code", "order_type", "sortBy",
								"container_code", "pass", "end_time" },
						new String[] { etShelfNum.getText().toString(), T,
								SORT, etBoxNum.getText().toString(), "1",
								//MyTool.getSFCTimeAddFive() },
								dialogDate.getTime()},
								"pdaPickup"),
				handler);
	}

	/**
	 * 获取订单分布
	 * 
	 * @param state
	 */
	private void getDisOrderData(int state) {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "order_type" }, new String[] { T },
						"countOrdersDetail"), handler);
	}

	/**
	 * 判断editText谁获得焦点
	 */
	private void SFCFocus(String showstr) {
		if (etBoxNum.hasFocus()) {
			etBoxNum.setText("");
			etBoxNum.append(showstr);
			etShelfNum.requestFocus();
			etShelfNum.setFocusable(true);
			return;
		}
		if (etShelfNum.hasFocus()) {
			etShelfNum.setText("");
			etShelfNum.append(showstr);
			if (etBoxNum.getText().toString().equals("")) {
				MyTool.toastShow(SFCDisConfig.this, "请输入箱子编号");
				etBoxNum.requestFocus();
				etBoxNum.setFocusable(true);
				return;
			}
			if (etShelfNum.getText().toString().equals("")) {
				MyTool.toastShow(SFCDisConfig.this, "请输入货架号");
				return;
			}
			getData(T, SORT);
			return;
		}
	}

	private void popShow() {
		if (p != null) {
			pRootView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.top_in));
			p.showAsDropDown(btnMore);
			return;
		}

		pRootView = LayoutInflater.from(this).inflate(
				R.layout.sfc_dis_config_more, null);
		pRootView.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.top_in));

		Button btn1 = (Button) pRootView.findViewById(R.id.btn1);
		btn1.setOnClickListener(this);

		p = new PopupWindow(pRootView, 120, LayoutParams.WRAP_CONTENT);
		p.setFocusable(true);
		p.setBackgroundDrawable(new BitmapDrawable());
		p.setOutsideTouchable(true);
		p.showAsDropDown(btnMore);

	}
}
