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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.DisMoreBean;
import com.sfcservice.component.MyDialog;
import com.sfcservice.component.SFCDisDialog;
import com.sfcservice.component.SFCDisDialog.Dialogcallback;
import com.sfcservice.component.SFCDisExceptionDialog;
import com.sfcservice.component.SFCDisNoDataDialog;
import com.sfcservice.img.AsynImageLoader;
import com.sfcservice.img.ImgLoad;
import com.sfcservice.lock.LockActivity;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisOnlineManyOneSKU extends Activity implements
		OnClickListener, OnEditorActionListener {
	/** Called when the activity is first created. */

	private TextView tvCount, tvShelfNum, tvCustomerID;
	private ImageView imgInfo;

	private TextView tvShow;
	private LinearLayout linePro;
	private ArrayList<DisMoreBean> listBean;
	private Button btnDone;
	private Button btnPass;
	private Button btnBack;
	private Button btnAddCheck;
	private Button btnMore;
	private PopupWindow p;
	private MyBroadCast broadCast;
	private View pRootView;
	private String CONFIG_ORDER_TYPE = "";
	private String CONFIG_ORDER_SORT = "";
	private String CONFIG_SHELF_NUM = "";
	private String CONFIG_BOX_NUM = "1";
	private String OP_CODE = "";
	private String OPM_TIME = "";
	private String WS_CODE = "";
	private String OPM_QUANTITY = "";
	private String PAGE = "";
	private String QTY = "";
	private String SKU = "";
	private String CUSTOMER_ID = "";
	private String OPM_ID = "";
	private String ORDER_OPM_QUANTITY = "";
	private String NEW_CONTAINER = "1";
	private String ORDERS_CODE = "";
	private String PICKUP_ORDERS = "";
	private String ORDER_COUNT = "";
	private String SPIC = "";
	private String PRODUCT_ID = "";
	private String UPDATE_QTY = "";
	private String PRE_WSCODE = "";
	private String showstr = "";
	private EditText scantext;
	private String barcode = "";
	private boolean startPickup = false;

	private int[] btnShow;
	private int clickItem;
	private boolean pause = false;
	private boolean touch = false;
	private boolean addException = false;
	/**
	 * 0��ʾȷ�����,1��ʾ�����,2��ʾ����,3��ʾ����쳣,4��ʾ����,5��ʾδ��ɵĶԻ����ύ, 6��ʾû����һ������Ի�����ύ
	 * ,7��ʾ����쳣��ͻʱ���ȷ�ϰ�ť��ȡ��һ������,8��ʾ�������ȡ��һ������
	 */
	private int STATE = -1;
	/**
	 * û����һ����Ʒ�ɹ���������Ի���������ť
	 */
	private SFCDisDialog dialogNoNex;
	/**
	 * ����쳣��dialog
	 */
	private SFCDisExceptionDialog dialogException;
	/**
	 * ����쳣�ɹ�������Ϣ
	 */
	private SFCDisNoDataDialog dialogExceptionBack;
	/**
	 * ��δ��ɵ����
	 */
	private SFCDisDialog dialogNoComplete;
	/**
	 * �����˳���ʱ�򵯳��ĶԻ���
	 */
	private MyDialog dialogBack;
	/**
	 * �����ͻ�ĶԻ���
	 */
	private SFCDisNoDataDialog dialogConflict;

	private Runnable myRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (pause) {
				return;
			}
			if (touch) {
				touch = false;
				handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
			} else {
				if (dialogBack.isShowing() || dialogConflict.isShowing()
						|| dialogException.isShowing()
						|| dialogExceptionBack.isShowing()
						|| dialogNoComplete.isShowing()
						|| dialogNoNex.isShowing()) {
					handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
					return;
				}
				if (addException) {
					handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
					return;
				}
				// ����
				Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
						LockActivity.class);
				startActivity(intent);
			}
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("���ڼ������...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisOnlineManyOneSKU.this, "���ӷ�����ʧ��");
				if (STATE == 4) {
					dialogNoComplete.show();
					break;
				}
				if (STATE == 5) {
					dialogNoComplete.show();
					break;
				}
				if (STATE == 6) {
					dialogNoNex.show();
				}
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					String[] strs = MyConnection.getMyConnection()
							.getDisManyOneData();

					if (strs.length == 1) {
						if (OP_CODE.equals("")) {
							OP_CODE = strs[0];
						}
						dialogNoNex.show();
						break;
					}
					reInit(strs);
					NEW_CONTAINER = "0";
					break;
				}
				if (STATE == 1 || STATE == 7) {
					String[] strs = MyConnection.getMyConnection()
							.getDisManyOneData();

					if (strs.length == 1) {
						if (OP_CODE.equals("")) {
							OP_CODE = strs[0];
						}
						dialogNoNex.show();
						break;
					}
					reInit(strs);
					if (STATE == 1) {
						NEW_CONTAINER = "0";
					}
					break;
				}
				if (STATE == 2 || STATE == 8) {
					String[] strs = MyConnection.getMyConnection()
							.getDisManyOneData();

					if (strs.length == 1) {
						if (OP_CODE.equals("")) {
							OP_CODE = strs[0];
						}
						MyTool.toastShow(SFCDisOnlineManyOneSKU.this,
								"û����һ�����������");
						scantext.requestFocus();
						scantext.setFocusable(true);
						break;
					}
					reInit(strs);
					break;
				}
				if (STATE == 3) {
					addException = true;
					String content = MyConnection.getMyConnection()
							.getDisManyOneExceptionData();
					tvCount.setText(dialogException.getGoodCount());
					QTY = dialogException.getGoodCount();
					btnAddCheck.setVisibility(View.INVISIBLE);
					if (QTY.equals("0")) {
						scantext.setFocusable(false);
						scantext.setEnabled(false);
						dialogExceptionBack.setContent("�뽫�˴����������Ʒ�Ż�ԭ��");
						dialogExceptionBack.show();
						break;
					}
					dialogExceptionBack.setContent(content);
					dialogExceptionBack.show();
					break;
				}
				if (STATE == 4) {
					String[] strs = MyConnection.getMyConnection()
							.getDisManyOneData();

					if (strs.length == 1) {
						if (OP_CODE.equals("")) {
							OP_CODE = strs[0];
						}
						dialogNoNex.show();
						break;
					}
					reInit(strs);
					break;
				}
				if (STATE == 5 || STATE == 6) {
					Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
							SFCDisConfig.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				// ����г�ͻstatusΪ0
				if (STATE == 0 || STATE == 1) {
					String count = MyConnection.getMyConnection()
							.getManyOneConflictCount();
					if (!count.equals("")) {
						MyTool.toastShow(SFCDisOnlineManyOneSKU.this,
								MyConnection.getMyConnection().getMessage());

						// dialogConflict.setContent("��������쳣,�뽫�˴�����Ĳ�Ʒ�Ż� " +
						// count
						// + " ��");
						// dialogConflict.show();
						break;
					}
				}
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisOnlineManyOneSKU.this, strMsg);
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

	/**
	 * �ж�editText˭��ý���
	 */
	private void SFCFocus() {

		if (scantext.hasFocus()) {
			scantext.setText("");
			scantext.append(showstr);
			this.screen();
			return;
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		pause = true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadCast);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (MyConfig.getMyConfig().getOrderCommit()) {
			MyConfig.getMyConfig().setOrderCommit(false);
			if (dialogNoNex != null && dialogNoNex.isShowing()) {
				dialogNoNex.dismiss();
			}
			if (dialogNoComplete != null && dialogNoComplete.isShowing()) {
				dialogNoComplete.dismiss();
			}
			Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
					SFCDisConfig.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return;
		}
		if (MyConfig.getMyConfig().getOrderDeleteAll()) {
			MyConfig.getMyConfig().setOrderDeleteAll(false);
			if (dialogNoNex != null && dialogNoNex.isShowing()) {
				dialogNoNex.dismiss();
			}
			if (dialogNoComplete != null && dialogNoComplete.isShowing()) {
				dialogNoComplete.dismiss();
			}
			Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
					SFCDisConfig.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return;
		}

		// // �����onpauseȻ����onresume
		if (pause && MyConfig.getMyConfig().getBoolLock()) {
			MyConfig.getMyConfig().setBoolLock(false);
			STATE = 8;
			getData();
		}
		pause = false;
		handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_dis_many_one_sku);
		init();
	}

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		dialogBack = new MyDialog(this);
		dialogBack.setContent("�˳������������Ϣ���������Ա����´������ȷ���˳�?");
		dialogBack.setConfirmText("ȷ��");
		dialogBack.setDialogCallback(new MyDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
						SFCDistribution.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SFCDisOnlineManyOneSKU.this.finish();
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub
			}
		});
		dialogExceptionBack = new SFCDisNoDataDialog(this);
		dialogExceptionBack.setConfirmText("ȷ��");
		dialogExceptionBack
				.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void btnConfirm() {
						if (QTY.equals("0")) {
							btnPass.requestFocus();
							btnPass.setFocusable(true);
						} else {
							scantext.requestFocus();
							scantext.setFocusable(true);
						}
					}
				});
		dialogException = new SFCDisExceptionDialog(this);
		dialogException
				.setDialogCallback(new SFCDisExceptionDialog.Dialogcallback() {

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void btnConfirm() {
						// TODO Auto-generated method stub
						if (dialogException.isAdd()) {
							STATE = 3;
							getData();
						} else {
							MyTool.toastShow(SFCDisOnlineManyOneSKU.this,
									"��ȷ���쳣������0");
						}

					}

					@Override
					public void btnCancel() {
						// TODO Auto-generated method stub

					}
				});

		dialogConflict = new SFCDisNoDataDialog(this);
		dialogConflict.setConfirmText("ȷ��");
		dialogConflict
				.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void btnConfirm() {
						// ������һ������
						STATE = 7;
						getData();
					}
				});

		dialogNoNex = new SFCDisDialog(this);
		dialogNoNex.setContent("�Բ���,û����һ����Ʒ�ɹ����");
		dialogNoNex.setBtnContinueText("ȡ��");
		dialogNoNex.setDialogCallback(new Dialogcallback() {

			@Override
			public void exitActivity() {
				// TODO Auto-generated method stub
			}

			@Override
			public void btnContinue() {
				// ȡ��
				dialogNoNex.dismiss();
				Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
						SFCDistribution.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}

			@Override
			public void btnCommit() {
				dialogNoNex.dismiss();
				STATE = 6;
				getData();
			}

			@Override
			public void btnCheck() {
				// �鿴
				MyConfig.getMyConfig().setOrderCommit(false);
				MyConfig.getMyConfig().setOrderDeleteAll(false);
				Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
						SFCDisManyOneOrder.class);
				intent.putExtra(MyConfig.TAG, OP_CODE);
				startActivity(intent);
			}
		});
		dialogNoComplete = new SFCDisDialog(this);
		dialogNoComplete.setDialogCallback(new Dialogcallback() {

			@Override
			public void btnContinue() {
				// TODO Auto-generated method stub
				dialogNoComplete.dismiss();
				STATE = 4;
				getData();
			}

			@Override
			public void btnCommit() {
				// TODO Auto-generated method stub
				dialogNoComplete.dismiss();
				STATE = 5;
				getData();
			}

			@Override
			public void btnCheck() {
				// TODO Auto-generated method stub
				MyConfig.getMyConfig().setOrderCommit(false);
				MyConfig.getMyConfig().setOrderDeleteAll(false);
				Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
						SFCDisManyOneOrder.class);
				intent.putExtra(MyConfig.TAG, OP_CODE);
				startActivity(intent);
			}

			@Override
			public void exitActivity() {
				// TODO Auto-generated method stub
				dialogNoComplete.dismiss();
				SFCDisOnlineManyOneSKU.this.finish();
			}
		});

		tvCount = (TextView) findViewById(R.id.tvCount);
		tvShelfNum = (TextView) findViewById(R.id.tvShelfNum);
		tvCustomerID = (TextView) findViewById(R.id.tvCustomerID);
		imgInfo = (ImageView) findViewById(R.id.imgInfo);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnDone = (Button) findViewById(R.id.btnDone);

		scantext = (EditText) findViewById(R.id.scantext);
		btnPass = (Button) findViewById(R.id.btnPass);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnMore = (Button) findViewById(R.id.btn_more);
		btnAddCheck = (Button) findViewById(R.id.btnAddCheck);
		imgInfo.setOnClickListener(this);
		btnAddCheck.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		scantext.setOnEditorActionListener(this);
		btnDone.setOnClickListener(this);
		btnPass.setOnClickListener(this);
		// ��δ��ɵ������
		Intent intent = getIntent();
		String[] strs = intent.getStringArrayExtra(MyConfig.TAG);
		if (strs != null) {
			listBean = new ArrayList<DisMoreBean>();

			OP_CODE = strs[0];
			CONFIG_ORDER_TYPE = strs[1];
			CONFIG_ORDER_SORT = strs[2];
			CONFIG_SHELF_NUM = strs[3];
			NEW_CONTAINER = "0";
			dialogNoComplete.show();
			return;
		}
		btnDone.setVisibility(View.INVISIBLE);
		// �����ý������
		String[] strsC = getIntent().getStringArrayExtra("S");
		if (strsC != null) {
			CONFIG_BOX_NUM = strsC[0];
			CONFIG_SHELF_NUM = strsC[1];
			CONFIG_ORDER_TYPE = strsC[2];
			CONFIG_ORDER_SORT = strsC[3];
			String[] strsInit = MyConnection.getMyConnection()
					.getDisManyOneData();

			OP_CODE = "";
			OPM_TIME = strsInit[0];
			WS_CODE = strsInit[1];
			OPM_QUANTITY = strsInit[2];
			PICKUP_ORDERS = strsInit[11];
			ORDER_COUNT = strsInit[12];
			OPM_ID = strsInit[13];
			PRODUCT_ID = strsInit[4];
			QTY = strsInit[5];
			PAGE = strsInit[7];
			ORDERS_CODE = strsInit[8];
			CUSTOMER_ID = strsInit[9];
			SPIC = strsInit[14];
			SKU = strsInit[10];
			showInfo(WS_CODE, strsInit[3], OPM_QUANTITY, strsInit[15]);
		}
		scantext.requestFocus();
		scantext.setFocusable(true);
	}

	private void reInit(String[] strs) {
		PRE_WSCODE = WS_CODE;
		OPM_TIME = strs[0];
		WS_CODE = strs[1];
		OPM_QUANTITY = strs[2];
		PRODUCT_ID = strs[4];
		QTY = strs[5];
		if (OP_CODE.equals("")) {
			OP_CODE = strs[6];
		}
		PAGE = strs[7];
		ORDERS_CODE = strs[8];
		CUSTOMER_ID = strs[9];
		SKU = strs[10];
		PICKUP_ORDERS = strs[11];
		ORDER_COUNT = strs[12];
		OPM_ID = strs[13];
		SPIC = strs[14];
		showInfo(WS_CODE, strs[3], OPM_QUANTITY, strs[15]);

		btnAddCheck.setVisibility(View.VISIBLE);
		btnMore.setVisibility(View.VISIBLE);
		// btnDone.setEnabled(true);
		scantext.setFocusable(true);
		scantext.requestFocus();
		if (STATE == 2 || STATE == 8) {
			return;
		}
		// btnDone.setText("�����");
		startPickup = true;
	}

	private void SFCPOP() {
		if (p != null) {
			pRootView.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.top_in));
			p.showAsDropDown(btnMore);
			return;
		}

		pRootView = LayoutInflater.from(this).inflate(R.layout.sfc_dis_more,
				null);
		pRootView.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.top_in));

		Button btn1 = (Button) pRootView.findViewById(R.id.btn1);
		Button btn2 = (Button) pRootView.findViewById(R.id.btn2);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);

		p = new PopupWindow(pRootView, 100, LayoutParams.WRAP_CONTENT);
		p.setFocusable(true);
		p.setBackgroundDrawable(new BitmapDrawable());
		p.setOutsideTouchable(true);
		p.showAsDropDown(btnMore);

	}

	private void showInfo(String shelf, String id, String count, String pic) {
		tvShelfNum.setText(shelf);
		tvCustomerID.setText(id);
		tvCount.setText(count);
		String ss = "";
		try {
			ss = MyConfig.URL_PRE + pic;
			AsynImageLoader asynImageLoader = new AsynImageLoader();

			asynImageLoader.showImageAsyn(imgInfo, ss, R.drawable.no_img);
		} catch (Exception ex) {
			System.out.println(ss);
			System.out.println("IMG Exception Message-->" + ex.getMessage());
		}
		// imgInfo.setImageBitmap(MyConfig.getMyConfig().getBitmap());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		touch = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!startPickup) {
				if (dialogNoComplete.isShowing()) {
					dialogNoComplete.dismiss();
				}
				finish();
				return true;
			}
			dialogBack.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		touch = true;
		switch (v.getId()) {

		case R.id.btnDone:
			addException = false;
			barcode = scantext.getText().toString();
			if (!startPickup) {
				STATE = 0;
				getData();
			} else {
				STATE = 1;
				getData();
			}
			// �����
			scantext.setText("");
			scantext.requestFocus();
			scantext.setFocusable(true);
			btnDone.setVisibility(View.INVISIBLE);
			break;
		case R.id.btnPass:
			addException = false;
			STATE = 2;
			getData();
			btnDone.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_more:
			SFCPOP();
			break;
		case R.id.btn_back:
			if (!startPickup) {
				if (dialogNoComplete.isShowing()) {
					dialogNoComplete.dismiss();
				}
				finish();
				break;
			}
			dialogBack.show();
			break;
		case R.id.btn1:
			p.dismiss();
			MyConfig.getMyConfig().setOrderDeleteAll(false);
			MyConfig.getMyConfig().setOrderCommit(false);
			Intent intent = new Intent(SFCDisOnlineManyOneSKU.this,
					SFCDisManyOneOrder.class);
			intent.putExtra(MyConfig.TAG, OP_CODE);
			startActivity(intent);
			break;
		case R.id.btn2:
			p.dismiss();
			Intent intentBox = new Intent(SFCDisOnlineManyOneSKU.this,
					SFCDistributionBox.class);
			intentBox.putExtra(MyConfig.TAG, OP_CODE);
			startActivity(intentBox);
			break;
		case R.id.btnAddCheck:
			dialogException.setCountAll(Integer.parseInt(OPM_QUANTITY));
			dialogException.show();
			break;
		case R.id.imgInfo:
			Intent intentXX = new Intent(SFCDisOnlineManyOneSKU.this,
					ImgLoad.class);
			intentXX.putExtra(MyConfig.TAG, SPIC);
			startActivity(intentXX);
			break;
		default:
			break;
		}
	}

	private void getData() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		String EXCEPTION = "0";
		if (btnAddCheck.getVisibility() == View.INVISIBLE) {
			EXCEPTION = "1";
		}
		switch (STATE) {
		case 0:
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeOneJsonWithUserInfo(
									new String[] { "order_type", "sortBy",
											"op_code", "status", "end_time",
											"opm_time", "container_code",
											"new_container", "ws_code", "pass",
											"unLock", "orders_code",
											"isContinue", "page", "exception",
											"pickupedOrders", "orderCount",
											"opm_id", "barcode" },
									new String[] { CONFIG_ORDER_TYPE,
											CONFIG_ORDER_SORT, OP_CODE, "0",
											MyTool.getSFCTime(), OPM_TIME,
											CONFIG_BOX_NUM, NEW_CONTAINER,
											WS_CODE, "0", "0", "", "0", PAGE,
											EXCEPTION, PICKUP_ORDERS,
											ORDER_COUNT, OPM_ID, barcode },
									QTY, PRODUCT_ID, ORDERS_CODE,
									"pdaPickupMultiSubmit"), handler);
			break;
		case 1:
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeOneJsonWithUserInfo(
									new String[] { "order_type", "sortBy",
											"op_code", "status", "end_time",
											"opm_time", "container_code",
											"new_container", "ws_code", "pass",
											"unLock", "orders_code",
											"isContinue", "page", "exception",
											"pickupedOrders", "orderCount",
											"opm_id", "barcode" },
									new String[] { CONFIG_ORDER_TYPE,
											CONFIG_ORDER_SORT, OP_CODE, "0",
											MyTool.getSFCTime(), OPM_TIME,
											CONFIG_BOX_NUM, NEW_CONTAINER,
											WS_CODE, "0", "0", "", "0", PAGE,
											EXCEPTION, PICKUP_ORDERS,
											ORDER_COUNT, OPM_ID, barcode },
									QTY, PRODUCT_ID, ORDERS_CODE,
									"pdaPickupMultiSubmit"), handler);
			break;
		case 7:
		case 2:
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeOneJsonWithUserInfo(
							new String[] { "op_code", "container_code",
									"ws_code", "new_container", "sortBy",
									"order_type", "pass", "end_time", "page",
									"exception", "pickupedOrders",
									"orderCount", "opm_id", "product_id",
									"barcode" },
							new String[] { OP_CODE, CONFIG_BOX_NUM, WS_CODE,
									NEW_CONTAINER, CONFIG_ORDER_SORT,
									CONFIG_ORDER_TYPE, "1",
									MyTool.getSFCTime(), PAGE, EXCEPTION,
									PICKUP_ORDERS, ORDER_COUNT, OPM_ID,
									PRODUCT_ID, barcode }, QTY, PRODUCT_ID,
							ORDERS_CODE, "pdaPickupMultiSubmit"), handler);
			break;
		case 3:
			MyConnection
					.getMyConnection()
					.acceptServer(
							MyConfig.URL_COMMON,
							MyConnection
									.getMyConnection()
									.writeManyOneExceptionJsonWithUserInfo(
											new String[] { "customer_id",
													"num", "order_type", "sku" },
											new String[] {
													CUSTOMER_ID,
													dialogException
															.getExceptionCount(),
													"1", SKU }, ORDERS_CODE,
											"pdaMultiException"), handler);

			break;
		case 4:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "order_type", "sortBy",
									"isContinue", "pass", "end_time",
									"product_id" },
							new String[] { OP_CODE, CONFIG_ORDER_TYPE,
									CONFIG_ORDER_SORT, "1", "1",
									MyTool.getSFCTime(), PRODUCT_ID },
							"pdaPickup"), handler);
			break;
		case 6:
		case 5:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "status", "product_id" },
							new String[] { OP_CODE, "1", PRODUCT_ID },
							"pdaPickupSubmit1"), handler);
			break;
		case 8:
			MyConnection
					.getMyConnection()
					.acceptDisMoreServerWithImg(
							MyConfig.URL_COMMON,
							MyConnection
									.getMyConnection()
									.writeOneJsonWithUserInfo(
											new String[] { "order_type",
													"sortBy", "op_code",
													"status", "end_time",
													"opm_time",
													"container_code",
													"new_container", "ws_code",
													"pass", "unLock",
													"isContinue", "page",
													"exception",
													"pickupedOrders",
													"orderCount", "opm_id",
													"product_id", "endPickup" },
											new String[] { CONFIG_ORDER_TYPE,
													CONFIG_ORDER_SORT, OP_CODE,
													"0", OPM_TIME, OPM_TIME,
													"", "0", WS_CODE, "0", "1",
													"1", PAGE, EXCEPTION,
													PICKUP_ORDERS, ORDER_COUNT,
													OPM_ID, PRODUCT_ID, "1" },
											QTY, PRODUCT_ID, ORDERS_CODE,
											"pdaPickup"), handler);
			break;
		default:
			break;
		}
	}

	private boolean screen() {

		if (scantext.getText().toString().equals("")) {
			MyTool.playFailedSound();
			MyTool.toastShow(SFCDisOnlineManyOneSKU.this, "������SKU����");
			return true;
		}

		if (Integer.parseInt(OPM_QUANTITY) > 1) {

			btnDone.setVisibility(View.VISIBLE);
			btnDone.requestFocus();
			btnDone.setFocusable(true);
			return true;
		}

		MyTool.hideInputKeyBroad(SFCDisOnlineManyOneSKU.this);
		// ���ȷ�����
		addException = false;
		barcode = scantext.getText().toString();
		if (!startPickup) {
			STATE = 0;
			getData();
		} else {
			STATE = 1;
			getData();
		}
		// �����
		scantext.setText("");
		scantext.requestFocus();
		scantext.setFocusable(true);
		return true;

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.scantext:
			if (actionId == 0 || actionId == 6) {
				if (event != null && event.getAction() == 1) {
					return true;
				}
				this.screen();
			}
			break;
		default:
			break;
		}
		return false;
	}
}