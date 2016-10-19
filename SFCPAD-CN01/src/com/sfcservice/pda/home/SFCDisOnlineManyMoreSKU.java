package com.sfcservice.pda.home;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.bean.DisBean;
import com.sfcservice.bean.DisMoreItemBean;
import com.sfcservice.component.AsyncLoadImage;
import com.sfcservice.component.MyDialog;
import com.sfcservice.component.SFCDisCompleteDialog;
import com.sfcservice.component.SFCDisDialog;
import com.sfcservice.component.SFCDisDialog.Dialogcallback;
import com.sfcservice.component.SFCDisExceptionDialog;
import com.sfcservice.component.SFCDisMoreItemDialog;
import com.sfcservice.component.SFCDisNoDataDialog;
import com.sfcservice.img.AsynImageLoader;
import com.sfcservice.img.ImgLoad;
import com.sfcservice.lock.LockActivity;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool; 



public class SFCDisOnlineManyMoreSKU extends Activity implements
		OnClickListener, OnEditorActionListener {
	/** Called when the activity is first created. */
	private LinearLayout lineTitle;
	private TextView tvShow;
	private LinearLayout linePro;
//	private ListView listView;
	private TextView tvShelfNum, tvCustomerID,totalQty;
	private ImageView imgInfo;
	private TextView tvCountAll;
	private TextView tvTitle;
	private EditText scantext;
	private ArrayList<DisMoreItemBean> listBean;
	private ArrayList<DisMoreItemBean> listViewBean;
	private ArrayList<DisBean> listDisBean;
	private AsyncLoadImage asyncLoadImage;
	private MyBroadCast broadCast;
	private Button btnDone;
	private Button btnPass;
	private Button btnBack;
	private Button btnMore;
	private Button btnAddException;
	private PopupWindow p;
	private View pRootView;
//	private MyAdapter adapter;
	private final String CONFIG_ORDER_TYPE = "2";
	private String CONFIG_ORDER_SORT = "";
	private String CONFIG_SHELF_NUM = "";
	private String CONFIG_BOX_NUM = "1";
	private String OP_CODE = "";
	private String OPM_TIME = "";
	private String WS_CODE = "";
	private String OPM_QUANTITY = "";
	private String PAGE = "";
	private String ORDER_COUNT = "";
	private String QTY = "";
	private String SKU = "";
	private String COUNT = "";
	private String CUSTOMER_ID = "";
	private String OPM_ID = "";
	private String COMPLETE = "";
	private String ORDER_OPM_QUANTITY = "";
	private String END_TIME = "";
	private String NEW_CONTAINER = "1";
	private String ORDERS_CODE = "";
	private String SPIC = "";
	private String PICKUP_ORDERS = "";
	private String PRODUCT_ID = "";
	private String UPDATE_QTY = "";
	private String PRE_WSCODE = "";
	private String barcode = "";
	private String showstr="";
	private boolean startPickup = false;
	private int OLD_STATE;
	private ArrayList<String> listStr;
	private boolean pause = false;
	private boolean touch = false;
	private boolean addException = false;
	private boolean dataFromComplete = false;
	/**
	 * 0表示确认配货,1表示已配货,2表示跳过,3表示添加异常,4表示继续,5表示未完成的对话框提交, 6表示没有下一个配货对话框的提交
	 * ,7表示配货异常冲突时候的确认按钮获取下一条数据,8提交之后获取下一条数据,9表示提交的complete为1时返回的数据，然后去获取下一条数据
	 * 10表示解锁获取数据
	 */
	private int STATE = -1;
	/**
	 * 分配口已经满了
	 */
	private SFCDisCompleteDialog dialogDisFull;
	/**
	 * 添加异常的dialog
	 */
	private SFCDisExceptionDialog dialogException;
	/**
	 * 添加异常成功返回信息
	 */
	private SFCDisNoDataDialog dialogExceptionBack;
	/**
	 * 跳过至最后一个返回信息
	 */
	private SFCDisNoDataDialog dialogNoData;
	/**
	 * 有未完成的配货
	 */
	private SFCDisDialog dialogNoComplete;
	/**
	 * 返回退出的时候弹出的对话框
	 */
	private MyDialog dialogBack;
	/**
	 * 配货冲突的对话框
	 */
	private SFCDisNoDataDialog dialogConflict;
	/**
	 * 显示分配口信息的dialog
	 */
	private SFCDisMoreItemDialog dialogItem;
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
						|| dialogNoData.isShowing()
						|| dialogDisFull.isShowing()
						|| dialogException.isShowing()
						|| dialogExceptionBack.isShowing()
						|| dialogItem.isShowing()
						|| dialogNoComplete.isShowing()) {
					handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
					return;
				}
				if (addException) {
					handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
					return;
				}
				// 锁屏
				Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
						LockActivity.class);
				startActivity(intent);
			}
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			System.out.print(STATE);
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisOnlineManyMoreSKU.this, "连接服务器失败");
				if (STATE == 4) {
					dialogNoComplete.show();
					break;
				}
				if (STATE == 5) {
					dialogNoComplete.show();
					break;
				}
				if (STATE == 6) {
					// dialogNoNex.show();
				}
				if (STATE == 8) {
					if (OLD_STATE == 5) {
						dialogNoComplete.show();
					} else if (OLD_STATE == 6) {
						// dialogNoNex.show();
					}
					break;
				}
				if (OLD_STATE == 4) {
					dialogNoComplete.show();
					break;
				}
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					dataFromComplete = false;
					refresh();
					break;
				}
				if (STATE == 1 || STATE == 7 || STATE == 8 || STATE == 9) {
					if (STATE != 9) {
						dataFromComplete = false;
					}
					refresh();
					if (STATE == 1) {
						NEW_CONTAINER = "0";
					}
					break;
				}
				if (STATE == 2 || STATE == 10) {
					ArrayList<DisMoreItemBean> listBean1 = new ArrayList<DisMoreItemBean>();
					String[] strs = MyConnection.getMyConnection()
							.getDisManyMoreInfo(listBean1);
					if (strs.length == 1) {
						if (!reInit(strs, true)) {
							dataFromComplete = sfcComplete();
						}
						// if (!btnDone.isEnabled()
						// && btnDone.getText().toString().equals("已配货")) {
						// dialogNoComplete.show();
						// }
					} else {
						listBean = listBean1;
						listBean1 = null;
						reInit(strs, false);
					}
					btnAddException.setVisibility(View.VISIBLE);
					scantext.setEnabled(true);
					scantext.setFocusable(true);
					scantext.requestFocus();
					break;
				}
				if (STATE == 3) {
					// 修改添加异常后的分配口的值
					addException = true;
					ArrayList<DisMoreItemBean> listB = new ArrayList<DisMoreItemBean>();
					String content = MyConnection.getMyConnection()
							.getDisMoreException(listB);
					updateDis(listB);

					btnAddException.setVisibility(View.INVISIBLE);
					COUNT = dialogException.getGoodCount();
					if (COUNT.equals("0")) {
						scantext.setEnabled(false);
						scantext.setFocusable(false);
						// 弹出对话框
						dialogExceptionBack.setContent("请将此次配货所有物品放回原处");
						dialogExceptionBack.show();
						break;
					}
					// 弹出对话框
					dialogExceptionBack.setContent(content);
					dialogExceptionBack.show();
					break;
				}
				if (STATE == 4) {
					dataFromComplete = false;
					refresh();
					break;
				}
				if (STATE == 5 || STATE == 6) {
					Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
							SFCDisConfig.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}
				break;
			case MyConfig.RESULTF:
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0 || STATE == 1) {
					if (sfcComplete()) {
						break;
					}
					ArrayList<DisMoreItemBean> listB = new ArrayList<DisMoreItemBean>();
					if (MyConnection.getMyConnection()
							.getDisMoreDataOfConflict(listB)) {
						StringBuffer sb = new StringBuffer();
						
						sb.append("配货有冲突，请进行如下操作:\n\n");
						for (int i = 0; i < listB.size(); i++) {
							if (i == listB.size() - 1) {

								sb.append("分配口" + listB.get(i).getLocation()
										+ "放回" + listB.get(i).getCount());
								break;

							}
							sb.append("分配口" + listB.get(i).getLocation() + "放回"
									+ listB.get(i).getCount() + "\n");

						}
						updateDis(listB);
						dialogConflict.setContent(sb.toString());
						dialogConflict.show();
						break;
					}else{
						MyTool.toastShow(SFCDisOnlineManyMoreSKU.this, MyConnection.getMyConnection().getMessage()); 

					}
				}
				if (STATE == 5 || STATE == 6) {
					listStr = new ArrayList<String>();
					String[] strs = MyConnection.getMyConnection()
							.getCommitResult(listStr);
					if (strs != null) {
						OP_CODE = strs[0];
						// END_TIME = strs[2];
						CONFIG_ORDER_SORT = strs[3];
						PICKUP_ORDERS = strs[4];
						ORDER_COUNT = strs[5];

						STATE = 8;
						getData();

						break;
					}
					MyTool.playFailedSound();
					Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
							SFCDistribution.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}
				if (STATE == 8) {
					Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
							SFCDistribution.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}

				if (STATE == 4 || STATE == 2 || STATE == 7 || STATE == 10) {
					sfcComplete();
					break;
				}
				if (STATE == 9) {
					if (OLD_STATE == 4) {
						Intent intent = new Intent(
								SFCDisOnlineManyMoreSKU.this,
								SFCDistribution.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
				}
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisOnlineManyMoreSKU.this, strMsg);
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
	 * 判断editText谁获得焦点
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (MyConfig.getMyConfig().isCommitBad()) {
			System.out.println("------->>bad");
			// 订单列表界面提交失败
			MyConfig.getMyConfig().setCommitBad(false);
			if (dialogNoComplete.isShowing()) {
				dialogNoComplete.dismiss();
			}
			String[] strs = MyConfig.getMyConfig().getDisNoCompleteData();

			String[] strsx = MyConfig.getMyConfig().getDisOrdersCode();
			listStr = new ArrayList<String>();
			for (int i = 0; i < strsx.length; i++) {
				listStr.add(strsx[i]);
			}
			OP_CODE = strs[0];
			// END_TIME = strs[2];
			CONFIG_ORDER_SORT = strs[3];
			PICKUP_ORDERS = strs[4];
			ORDER_COUNT = strs[5];
			STATE = 8;
			getData();
		}
		// 下架单列表提交成功
		if (MyConfig.getMyConfig().getOrderCommit()) {
			System.out.println("-----------commit");
			MyConfig.getMyConfig().setOrderCommit(false);
			MyConfig.getMyConfig().setOrderDeleteAll(false);
			if (dialogNoComplete.isShowing()) {
				dialogNoComplete.dismiss();
			}
			Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
					SFCDisConfig.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}

		// // 如果是onpause然后再onresume
		if (pause && MyConfig.getMyConfig().getBoolLock()) {
			MyConfig.getMyConfig().setBoolLock(false);
			STATE = 10;
			getData();
		}
		pause = false;
		
		handler.postDelayed(myRunnable, MyConfig.LOCKTIME);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_dis_online_many_more_sku);
		init();
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

	private void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		dialogNoData = new SFCDisNoDataDialog(this);
		dialogNoData.setConfirmText("确定");
		dialogNoData.setContent("您没有配任何产品,请退出后重新进入配货");
		dialogNoData.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				finish();
			}
		});

		dialogItem = new SFCDisMoreItemDialog(this);
		dialogConflict = new SFCDisNoDataDialog(this);
		dialogConflict.setConfirmText("确定");
		dialogConflict
				.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void btnConfirm() {
						// 访问下一条数据
						STATE = 7;
						getData();
					}
				});
		dialogBack = new MyDialog(this);
		dialogBack.setContent("退出后，您的配货信息还将保留以便您下次配货，确定退出?");
		dialogBack.setConfirmText("确定");
		dialogBack.setDialogCallback(new MyDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
						SFCDistribution.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SFCDisOnlineManyMoreSKU.this.finish();
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub
			}
		});
		dialogNoComplete = new SFCDisDialog(this);
		dialogNoComplete.setDialogCallback(new Dialogcallback() {

			@Override
			public void btnContinue() {
				// TODO Auto-generated method stub
				dialogNoComplete.dismiss();
				STATE = 4;
				OLD_STATE = 4;
				getData();
			}

			@Override
			public void btnCommit() {
				// TODO Auto-generated method stub
				dialogNoComplete.dismiss();
				OLD_STATE = 5;
				STATE = 5;
				getData();
			}

			@Override
			public void btnCheck() {
				// TODO Auto-generated method stub
				startOrderActivity();
			}

			@Override
			public void exitActivity() {
				// TODO Auto-generated method stub
				dialogNoComplete.dismiss();
				SFCDisOnlineManyMoreSKU.this.finish();
			}
		});
		dialogExceptionBack = new SFCDisNoDataDialog(this);
		dialogExceptionBack.setConfirmText("确定");
		dialogExceptionBack
				.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void btnConfirm() {
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
							MyTool.toastShow(SFCDisOnlineManyMoreSKU.this,
									"请确认异常数大于0");
						}

					}

					@Override
					public void btnCancel() {
						// TODO Auto-generated method stub

					}
				});

		dialogDisFull = new SFCDisCompleteDialog(this);
		dialogDisFull.setContent("已完成配货，请确认提交配货");
//		dialogDisFull.setBtnContinueText("取消");
		dialogDisFull.setDialogCallback(new SFCDisCompleteDialog.Dialogcallback() {

			@Override
			public void exitActivity() {
				// TODO Auto-generated method stub
			}

//			@Override
//			public void btnContinue() {
//				// 取消
//				dialogDisFull.dismiss();
//				Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
//						SFCDistribution.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//				finish();
//			}

			@Override
			public void btnCommit() {
				dialogDisFull.dismiss();
				OLD_STATE = 6;
				STATE = 6;
				getData();
			}

			@Override
			public void btnCheck() {
				// 查看
				startOrderActivity();
			}
		});
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);

//		listView = (ListView) findViewById(R.id.disListView);
		tvCustomerID = (TextView) findViewById(R.id.tvCustomerID);
		totalQty = (TextView) findViewById(R.id.totalQty);
		tvShelfNum = (TextView) findViewById(R.id.tvShelfNum);
		btnDone = (Button) findViewById(R.id.btnDone);
		btnPass = (Button) findViewById(R.id.btnPass);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnMore = (Button) findViewById(R.id.btn_more);
		scantext = (EditText) findViewById(R.id.scantext);
		btnAddException = (Button) findViewById(R.id.btnAddException);
		imgInfo = (ImageView) findViewById(R.id.imgInfo);
		imgInfo.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		btnPass.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnMore.setOnClickListener(this);
		btnAddException.setOnClickListener(this);
		scantext.setOnEditorActionListener(this);	
		listBean = new ArrayList<DisMoreItemBean>();
		listViewBean = new ArrayList<DisMoreItemBean>();
//		adapter = new MyAdapter();
//		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				touch = true;
//				dialogItem.setData(listBean);
//				dialogItem.show();
//			}
//		});
		
		
		btnDone.setVisibility(View.INVISIBLE);
		
		// 有未完成的配货单
		Intent intent = getIntent();
		String[] strs = intent.getStringArrayExtra(MyConfig.TAG);
		if (strs != null) {
			OP_CODE = strs[0];
			CONFIG_ORDER_SORT = strs[2];
			CONFIG_SHELF_NUM = strs[3];
			NEW_CONTAINER = "0";
			PICKUP_ORDERS = strs[4];
			ORDER_COUNT = strs[5];
			END_TIME = strs[6];
			dialogNoComplete.show();
			listDisBean = MyConfig.getMyConfig().getListDisRemain();
			return;
		}

		// 从配置界面过来
		String[] strInits = MyConnection.getMyConnection()
				.getFirstManyMoreInfo(listBean);
		reInit(strInits, false);
		END_TIME = strInits[1];
		String[] strsConfig = getIntent().getStringArrayExtra("S");
		CONFIG_BOX_NUM = strsConfig[0];
		CONFIG_SHELF_NUM = strsConfig[1];
		CONFIG_ORDER_SORT = strsConfig[3];
		listDisBean = MyConfig.getMyConfig().getListDisRemain();
		scantext.requestFocus();
		scantext.setFocusable(true);
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadCast);
		super.onDestroy();
	}


	/**
	 * 更新数据
	 * 
	 * @param strs
	 * @param onlyOpCode
	 */
	private boolean reInit(String[] strs, boolean onlyOpCode) {
		if (onlyOpCode) {
			if (OP_CODE.equals("")) {
				if (strs[0].equals("")) {
					MyTool.toastShow(SFCDisOnlineManyMoreSKU.this,
							"没有下一条数据可供配货");
					return true;
				}
				OP_CODE = strs[0];
			}
			return false;
		} else {
			if (OP_CODE.equals("")) {
				OP_CODE = strs[5];
			}
			OPM_TIME = strs[1];
			PAGE = strs[6];
			PRODUCT_ID = strs[7];
			WS_CODE = strs[0];
			CUSTOMER_ID = strs[10];
			SKU = strs[11];
			COUNT = strs[2];
			PICKUP_ORDERS = strs[3];
			OPM_ID = strs[16];
			ORDER_COUNT = strs[4];
			tvShelfNum.setText(strs[0]);
			tvCustomerID.setText(strs[12]);
			totalQty.setText(strs[2]);
			SPIC = strs[13];
			COMPLETE = strs[15];
			String ss = "";
			try{
				ss = MyConfig.URL_PRE  + strs[17];
				AsynImageLoader asynImageLoader = new AsynImageLoader();  
			
				asynImageLoader.showImageAsyn(imgInfo, ss , R.drawable.no_img); 
			}catch (Exception ex) {
				System.out.println(ss );
				System.out.println("IMG Exception Message-->" + ex.getMessage());
			}
			//imgInfo.setImageBitmap(MyConfig.getMyConfig().getBitmap());
		}
//		refreshOnlyDisHole();
		if (COMPLETE.equals("1")) {
			dialogDisFull.show();
		}
		return false;
	}

	/**
	 * 单独的刷新分配口信息
	 */
//	public void refreshOnlyDisHole() {
//		listViewBean = new ArrayList<DisMoreItemBean>();
//		for (int i = 0; i < listBean.size(); i++) {
//			if (i < 8) {
//				listViewBean.add(i, listBean.get(i));
//			}
//		}
//		adapter.notifyDataSetChanged();
//	}

	/**
	 * 刷新界面数据以及更新界面
	 */
	public void refresh() {
		listBean = new ArrayList<DisMoreItemBean>();
		String[] strs = MyConnection.getMyConnection().getDisManyMoreInfo(
				listBean);
		// length为1说明没有下一个数据可供配货
		if (strs.length == 1) {
			reInit(strs, true);
			dataFromComplete = sfcComplete();
		} else {
			
			reInit(strs, false);
		}
		NEW_CONTAINER = "0";
		//btnDone.setText("已配货");
		startPickup = true;
		btnMore.setVisibility(View.VISIBLE);
		btnAddException.setVisibility(View.VISIBLE);
		btnDone.setVisibility(View.INVISIBLE);
		scantext.setFocusable(true);
		scantext.requestFocus();
	}

	/**
	 * 启动订单列表activity
	 */
	private void startOrderActivity() {
		MyConfig.getMyConfig().setOrderCommit(false);
		MyConfig.getMyConfig().setOrderDeleteAll(false);
		MyConfig.getMyConfig().setCommitBad(false);
		Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
				SFCDisManyMoreOrder.class);
		intent.putExtra(MyConfig.TAG, new String[] { OP_CODE, OPM_TIME });
		startActivity(intent);
	}

	/**
	 * 配货冲突与添加异常 更新分配口信息
	 */
	private void updateDis(ArrayList<DisMoreItemBean> listB) {
		// 更新界面数据
		for (int i = 0; i < listB.size(); i++) {
			for (int j = 0; j < listBean.size(); j++) {
				String locationO = listBean.get(j).getLocation();
				String locationN = listB.get(i).getLocation();

				if (locationO.equals(locationN)) {
					int count0 = Integer.parseInt(listBean.get(j).getCount());
					int countN = Integer.parseInt(listB.get(i).getCount());
					listBean.get(j).setCount((count0 - countN) + "");
					break;
				}
			}
		}
		ArrayList<DisMoreItemBean> listX = new ArrayList<DisMoreItemBean>();
		for (int i = 0; i < listBean.size(); i++) {
			if (!listBean.get(i).getCount().equals("0")) {
				listX.add(listBean.get(i));
			}
		}
		// 释放内存
		listB = null;
		listBean = listX;
		listX = null;

		// 更新 分配口信息
//		refreshOnlyDisHole();
	}

	/**
	 * complete有关的操作
	 */
	public boolean sfcComplete() {
		ArrayList<String> listStr = new ArrayList<String>();
		String[] strs = MyConnection.getMyConnection().firstDisManyMoreData(
				listStr);
		if (strs == null) {
			return false;
		}
		if (strs.length == 0) {
			dialogDisFull.show();
			MyConfig.getMyConfig().setGoOnPickup(false);
			return true;
		}
		if (strs.length != 0) {
			STATE = 9;
			
			SFCDisOnlineManyMoreSKU.this.listStr = listStr;
			MyConfig.getMyConfig().setGoOnPickup(true);
			OP_CODE = strs[0];
			// END_TIME = strs[2];
			CONFIG_ORDER_SORT = strs[3];
			PICKUP_ORDERS = strs[4];
			ORDER_COUNT = strs[5];
			getData();
			return true;
		}
		return false;
	}

	/**
	 * 弹出列表选项-->>配货单以及配货箱列表
	 */
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

//	private class MyAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return listViewBean.size() % 2 == 0 ? listViewBean.size() / 2
//					: (listViewBean.size() / 2 + 1);
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return 0;
//		}

//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			View v = LayoutInflater.from(SFCDisOnlineManyMoreSKU.this).inflate(
//					R.layout.sfc_dis_list_view_item, null);
//			switch (position) {
//			case 0:
//				if (listViewBean.size() == 1) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(0).getLocation());
//					count1.setText(listViewBean.get(0).getCount());
//					dis2.setVisibility(View.INVISIBLE);
//					count2.setVisibility(View.INVISIBLE);
//					break;
//				}
//				if (listViewBean.size() >= 2) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//
//					dis1.setText("#" + listViewBean.get(0).getLocation());
//					count1.setText(listViewBean.get(0).getCount());
//
//					dis2.setText("#" + listViewBean.get(1).getLocation());
//					count2.setText(listViewBean.get(1).getCount());
//					break;
//				}
//				break;
//			case 1:
//				if (listViewBean.size() == 3) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(2).getLocation());
//					count1.setText(listViewBean.get(2).getCount());
//					dis2.setVisibility(View.INVISIBLE);
//					count2.setVisibility(View.INVISIBLE);
//					break;
//				}
//				if (listViewBean.size() >= 4) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(2).getLocation());
//					count1.setText(listViewBean.get(2).getCount());
//					dis2.setText("#" + listViewBean.get(3).getLocation());
//					count2.setText(listViewBean.get(3).getCount());
//					break;
//				}
//
//				break;
//			case 2:
//
//				if (listViewBean.size() == 5) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(4).getLocation());
//					count1.setText(listViewBean.get(4).getCount());
//					dis2.setVisibility(View.INVISIBLE);
//					count2.setVisibility(View.INVISIBLE);
//					break;
//				}
//				if (listViewBean.size() >= 6) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(4).getLocation());
//					count1.setText(listViewBean.get(4).getCount());
//					dis2.setText("#" + listViewBean.get(5).getLocation());
//					count2.setText(listViewBean.get(5).getCount());
//					break;
//				}
//				break;
//			case 3:
//
//				if (listViewBean.size() == 7) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(6).getLocation());
//					count1.setText(listViewBean.get(6).getCount());
//					dis2.setVisibility(View.INVISIBLE);
//					count2.setVisibility(View.INVISIBLE);
//					break;
//				}
//				if (listBean.size() > 8) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setVisibility(View.INVISIBLE);
//					dis2.setVisibility(View.INVISIBLE);
//					count1.setVisibility(View.INVISIBLE);
//					count2.setVisibility(View.INVISIBLE);
//
//					TextView tv = (TextView) v.findViewById(R.id.tvMore);
//					tv.setVisibility(View.VISIBLE);
//					break;
//				}
//				if (listViewBean.size() == 8) {
//					TextView dis1 = (TextView) v.findViewById(R.id.dis1);
//					TextView count1 = (TextView) v.findViewById(R.id.count1);
//					TextView dis2 = (TextView) v.findViewById(R.id.dis2);
//					TextView count2 = (TextView) v.findViewById(R.id.count2);
//					dis1.setText("#" + listViewBean.get(6).getLocation());
//					count1.setText(listViewBean.get(6).getCount());
//					dis2.setText("#" + listViewBean.get(7).getLocation());
//					count2.setText(listViewBean.get(7).getCount());
//					break;
//				}
//				break;
//
//			default:
//				break;
//			}
//			return v;
//		}
//
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		touch = true;
		switch (v.getId()) {
		
		case R.id.btnDone:
			addException = false;	
			barcode = scantext.getText().toString();
			if (startPickup) {
				STATE = 1;
				OLD_STATE = 1;
				getData();
				
			}else{
				STATE = 0;
				getData();
				
			}
			// 已配货
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
		case R.id.btnAddException:
			dialogException.setCountAll(Integer.parseInt(COUNT));
			dialogException.show();
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
			startOrderActivity();
			break;
		case R.id.btn2:
			p.dismiss();
			Intent intentBox = new Intent(SFCDisOnlineManyMoreSKU.this,
					SFCDistributionBox.class);
			intentBox.putExtra(MyConfig.TAG, OP_CODE);
			startActivity(intentBox);
			break;
		case R.id.imgInfo:
			Intent intent = new Intent(SFCDisOnlineManyMoreSKU.this,
					ImgLoad.class);
			intent.putExtra(MyConfig.TAG, SPIC);
			startActivity(intent);
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
		if (btnAddException.getVisibility() == View.INVISIBLE) {
			EXCEPTION = "1";
		}
		 
		String commitBad = new Boolean(MyConfig.getMyConfig().getGoOnPickup()).toString();
		//Log.v("commitBad",commitBad);
		switch (STATE) {
		case 0:
			if (dataFromComplete) {
				MyConnection.getMyConnection().acceptDisMoreServerWithImg(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection()
								.writeDisMorePassJsonWithUserInfo(
										new String[] { "order_type", "sortBy",
												"op_code", "status",
												"end_time", "opm_time",
												"container_code",
												"new_container", "ws_code",
												"pass", "unLock", "isContinue",
												"page", "exception",
												"pickupedOrders", "orderCount",
												"opm_id","haveUnfinished","barcode" },
										new String[] { CONFIG_ORDER_TYPE,
												CONFIG_ORDER_SORT, OP_CODE,
												"0", END_TIME, OPM_TIME,
												CONFIG_BOX_NUM, NEW_CONTAINER,
												WS_CODE, "0", "0", "0", PAGE,
												EXCEPTION, PICKUP_ORDERS,
												ORDER_COUNT, OPM_ID ,commitBad,barcode},
										listBean, listStr, listDisBean,
										PRODUCT_ID, "pdaPickupMultiSubmit"),
						handler);
				break;
			}
			MyConnection.getMyConnection()
					.acceptDisMoreServerWithImg(
							MyConfig.URL_COMMON,
							MyConnection.getMyConnection()
									.writeDisMoreJsonWithUserInfo(
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
													"orderCount", "opm_id","haveUnfinished","barcode" },
											new String[] { CONFIG_ORDER_TYPE,
													CONFIG_ORDER_SORT, OP_CODE,
													"0", END_TIME, OPM_TIME,
													CONFIG_BOX_NUM,
													NEW_CONTAINER, WS_CODE,
													"0", "0", "0", PAGE,
													EXCEPTION, PICKUP_ORDERS,
													ORDER_COUNT, OPM_ID ,commitBad,barcode},
											listBean, listDisBean, PRODUCT_ID,
											"pdaPickupMultiSubmit"), handler);
			break;
		case 1:
			if (dataFromComplete) {
				MyConnection.getMyConnection().acceptDisMoreServerWithImg(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection()
								.writeDisMorePassJsonWithUserInfo(
										new String[] { "order_type", "sortBy",
												"op_code", "status",
												"end_time", "opm_time",
												"container_code",
												"new_container", "ws_code",
												"pass", "unLock", "isContinue",
												"page", "exception",
												"pickupedOrders", "orderCount",
												"opm_id" ,"haveUnfinished" ,"barcode"},
										new String[] { CONFIG_ORDER_TYPE,
												CONFIG_ORDER_SORT, OP_CODE,
												"0", END_TIME, OPM_TIME,
												CONFIG_BOX_NUM, NEW_CONTAINER,
												WS_CODE, "0", "0", "0", PAGE,
												EXCEPTION, PICKUP_ORDERS,
												ORDER_COUNT, OPM_ID ,commitBad,barcode},
										listBean, listStr, listDisBean,
										PRODUCT_ID, "pdaPickupMultiSubmit"),
						handler);
				break;
			}
			MyConnection.getMyConnection()
					.acceptDisMoreServerWithImg(
							MyConfig.URL_COMMON,
							MyConnection.getMyConnection()
									.writeDisMoreJsonWithUserInfo(
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
													"orderCount", "opm_id" ,"haveUnfinished","barcode" },
											new String[] { CONFIG_ORDER_TYPE,
													CONFIG_ORDER_SORT, OP_CODE,
													"0", END_TIME, OPM_TIME,
													CONFIG_BOX_NUM,
													NEW_CONTAINER, WS_CODE,
													"0", "0", "0", PAGE,
													EXCEPTION, PICKUP_ORDERS,
													ORDER_COUNT, OPM_ID, commitBad,barcode },
											listBean, listDisBean, PRODUCT_ID,
											"pdaPickupMultiSubmit"), handler);
			break;
		case 7:
		case 2:
			if (dataFromComplete) {
				MyConnection.getMyConnection().acceptDisMoreServerWithImg(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection()
								.writeDisMorePassJsonWithUserInfo(
										new String[] { "order_type", "sortBy",
												"op_code", "status",
												"end_time", "opm_time",
												"container_code",
												"new_container", "ws_code",
												"pass", "unLock", "isContinue",
												"page", "exception",
												"pickupedOrders", "orderCount",
												"product_id", "opm_id" ,"haveUnfinished","barcode"},
										new String[] { CONFIG_ORDER_TYPE,
												CONFIG_ORDER_SORT, OP_CODE,
												"0", END_TIME, OPM_TIME,
												CONFIG_BOX_NUM, "0", WS_CODE,
												"1", "0", "0", PAGE, EXCEPTION,
												PICKUP_ORDERS, ORDER_COUNT,
												PRODUCT_ID, OPM_ID ,commitBad,barcode}, listBean,
										listStr, listDisBean, PRODUCT_ID,
										"pdaPickupMultiSubmit"), handler);

				break;
			}
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeDisMoreJsonWithUserInfo(
									new String[] { "order_type", "sortBy",
											"op_code", "status", "end_time",
											"opm_time", "container_code",
											"new_container", "ws_code", "pass",
											"unLock", "isContinue", "page",
											"exception", "pickupedOrders",
											"orderCount", "product_id",
											"opm_id","haveUnfinished","barcode" },
									new String[] { CONFIG_ORDER_TYPE,
											CONFIG_ORDER_SORT, OP_CODE, "0",
											END_TIME, OPM_TIME, CONFIG_BOX_NUM,
											"0", WS_CODE, "1", "0", "0", PAGE,
											EXCEPTION, PICKUP_ORDERS,
											ORDER_COUNT, PRODUCT_ID, OPM_ID,commitBad ,barcode},
									listBean, listDisBean, PRODUCT_ID,
									"pdaPickupMultiSubmit"), handler);
			break;
		case 3:
			MyConnection
					.getMyConnection()
					.acceptServer(
							MyConfig.URL_COMMON,
							MyConnection
									.getMyConnection()
									.writeMoreExceptionJsonWithUserInfo(
											new String[] { "customer_id",
													"sku", "num", "order_type",
													"ws_code" },
											new String[] {
													CUSTOMER_ID,
													SKU,
													dialogException
															.getExceptionCount(),
													"2", WS_CODE }, listBean,
											"pdaMultiException"), handler);

			break;
		case 4:
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeMoreContinueJsonWithUserInfo(
									new String[] { "op_code", "order_type",
											"sortBy", "isContinue", "pass",
											"end_time", "pickupedOrders",
											"orderCount","haveUnfinished","product_id" },
									new String[] { OP_CODE, CONFIG_ORDER_TYPE,
											CONFIG_ORDER_SORT, "1", "1",
											MyTool.getSFCTime(), PICKUP_ORDERS,
											ORDER_COUNT ,commitBad , PRODUCT_ID }, listDisBean,
									"pdaPickup"), handler);
			break;
		case 6:
		case 5:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "status", "order_type",
									"end_time","product_id" },
							new String[] { OP_CODE, "1", "2", OPM_TIME , PRODUCT_ID },
							"pdaPickupSubmit1"), handler);
			break;
		case 9:
		case 8:
			MyConnection
					.getMyConnection()
					.acceptDisMoreServerWithImg(
							MyConfig.URL_COMMON,
							MyConnection
									.getMyConnection()
									.writeMoreCommitJsonWithUserInfo(
											new String[] { "op_code",
													"order_type", "sortBy",
													"isContinue", "end_time",
													"pickupedOrders",
													"orderCount", "endPickup","haveUnfinished","product_id" },
											new String[] { OP_CODE,
													CONFIG_ORDER_TYPE,
													CONFIG_ORDER_SORT, "1",
													END_TIME, PICKUP_ORDERS,
													ORDER_COUNT, "1" ,commitBad, PRODUCT_ID},
											listStr, listDisBean, "pdaPickup"),
							handler);

			break;
		case 10:
			if (dataFromComplete) {
				MyConnection.getMyConnection().acceptDisMoreServerWithImg(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection()
								.writeDisMorePassJsonWithUserInfo(
										new String[] { "order_type", "sortBy",
												"op_code", "status",
												"end_time", "opm_time",
												"container_code",
												"new_container", "ws_code",
												"pass", "unLock", "isContinue",
												"page", "exception",
												"pickupedOrders", "orderCount",
												"product_id", "opm_id",
												"endPickup" ,"haveUnfinished"},
										new String[] { CONFIG_ORDER_TYPE,
												CONFIG_ORDER_SORT, OP_CODE,
												"0", OPM_TIME, OPM_TIME, "",
												"0", WS_CODE, "0", "1", "1",
												PAGE, EXCEPTION, PICKUP_ORDERS,
												ORDER_COUNT, PRODUCT_ID,
												OPM_ID, "1" ,commitBad}, listBean,
										listStr, listDisBean, PRODUCT_ID,
										"pdaPickup"), handler);
				break;
			}
			MyConnection.getMyConnection().acceptDisMoreServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection()
							.writeDisMoreJsonWithUserInfo(
									new String[] { "order_type", "sortBy",
											"op_code", "status", "end_time",
											"opm_time", "container_code",
											"new_container", "ws_code", "pass",
											"unLock", "isContinue", "page",
											"exception", "pickupedOrders",
											"orderCount", "product_id",
											"opm_id", "endPickup","haveUnfinished" },
									new String[] { CONFIG_ORDER_TYPE,
											CONFIG_ORDER_SORT, OP_CODE, "0",
											OPM_TIME, OPM_TIME, "", "0",
											WS_CODE, "0", "1", "1", PAGE,
											EXCEPTION, PICKUP_ORDERS,
											ORDER_COUNT, PRODUCT_ID, OPM_ID,
											"1" ,commitBad}, listBean, listDisBean,
									PRODUCT_ID, "pdaPickup"), handler);
			break;
		default:
			break;
		}
	}
	private boolean screen(){
		if (scantext.getText().toString().equals("")) {
			MyTool.playFailedSound();
			MyTool.toastShow(SFCDisOnlineManyMoreSKU.this, "请输入SKU条码");
			return true;
		}
		OPM_QUANTITY = totalQty.getText().toString();
		//System.out.println(OPM_QUANTITY+" >>>>>>>>>>>>>>>>> "+OPM_QUANTITY );
		if(Integer.parseInt(OPM_QUANTITY)>1){			
			  btnDone.setVisibility(View.VISIBLE);
			  btnDone.requestFocus();
			  btnDone.setFocusable(true);
			  return true;
		}
		MyTool.hideInputKeyBroad(SFCDisOnlineManyMoreSKU.this);				 
		// 点击确认配货
		addException = false;	
		barcode = scantext.getText().toString();
		if (startPickup) {
			STATE = 1;
			OLD_STATE = 1;
			getData();
			
		}else{
			STATE = 0;
			getData();
			
		}
		// 已配货
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