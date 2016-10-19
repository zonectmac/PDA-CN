package com.sfcservice.pda.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
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

public class SFCDisOnLine extends Activity implements OnClickListener,
		OnEditorActionListener {
	private Button btnBack;
	private Button btnDone;
	private Button btnPass, btnFinished;
	private Button btnMore;
	private Button btnAddCheck;
	private LinearLayout linePro;
	private TextView tvShow;
	private TextView tvTitle;
	private LinearLayout lineTop, lineBottom;
	private ImageView imgRefresh;
	private TextView tvProName, tvShelfNum, tvClientProNum;
	private TextView tvProCount;
	private EditText scantext;
	private ImageView imgInfo;
	private String OP_CODE = "";
	private PopupWindow p;
	private View pRootView;
	private String CONFIG_ORDER_TYPE = "";
	private String CONFIG_ORDER_SORT = "";
	// private String CONFIG_SHELF_NUM = "";
	private String CONFIG_BOX_NUM = "";
	private String preWsCode = "";
	private String time;
	private String imgUrl;
	private String updateQty = "";
	private String customerId = "";
	private String sku = "";
	private String OPM_QUANTITY = "";
	private String opm_time = "";
	private MyBroadCast broadCast;
	private String barcode = "";
	private boolean startPickup = false;

	/**
	 * 0表示解锁获取数据 1表示继续 2表示已配货
	 * ;4表示冲突时获取下一条数据;5表示有未完成订单时候的提交；6表示确认配货，7表示跳过，8表示结束配货,9对话框确定添加至盘点
	 * 10表示没有订单时的跳过
	 */
	private int STATE = -1;
	private String count = "";
	private String productId = "";
	private String wsCode = "";
	private boolean passBool = false;

	private String showstr = "";

	/**
	 * 锁屏的时候用到
	 */
	private boolean pause = false;
	private boolean touch = false;
	/**
	 * 三个按钮;继续，查看，提交
	 */
	private SFCDisDialog dialog;
	/**
	 * 三个按钮；取消，查看，提交
	 */
	private SFCDisDialog dialogThree;
	/**
	 * 一个按钮；确认
	 */
	private SFCDisNoDataDialog dialogOne;
	/**
	 * 只有一个退出模块的dialog并且不允许返回消失
	 */
	private SFCDisNoDataDialog dialog2;
	/**
	 * 只有一个按钮退出
	 */
	private SFCDisNoDataDialog dialogOnlyFinish;
	/**
	 * 配货异常时弹出的dialog
	 */
	private SFCDisExceptionDialog dialogException;
	/**
	 * 返回退出的时候弹出的对话框
	 */
	private MyDialog dialog301;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			scantext.requestFocus();
			scantext.setFocusable(true);
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisOnLine.this, "连接服务器失败");

				if (STATE == 0) {
					lineTop.setVisibility(View.INVISIBLE);
					lineBottom.setVisibility(View.INVISIBLE);
					imgRefresh.setVisibility(View.VISIBLE);
					break;
				}
				// 继续的时候超时
				if (STATE == 1) {
					dialog.show();
				}
				// 提交超时
				if (STATE == 3) {
					dialogThree.show();
					break;
				}
				if (STATE == 4) {
					dialogOne.show();
					break;
				}
				if (STATE == 5) {
					dialog.show();
					break;
				}
				break;
			case MyConfig.RESULTS:
				if (STATE != 9) {
					btnAddCheck.setVisibility(View.VISIBLE);
					scantext.setEnabled(true);
					scantext.setFocusable(true);
				}
				if (STATE == 7 || STATE == 10) {
					MyTool.playPassSound();
				} else {
					MyTool.playSuccessSound1();
				}
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					// 没有点击确认配货的时候解锁刷新
					String[] info = MyConnection.getMyConnection()
							.getDistributionInfo();
					// 没有任何产品可供配货
					if (info == null) {
						if (!startPickup) {
							dialog2.setContent("没有任何产品可供配货");
							dialog2.setConfirmText("退出");
							dialog2.show();
							break;
						} else {
							dialogThree.show();
							break;
						}
					}
					showInfo(info[0], info[1], info[2], info[6], info[3],
							info[10]);
					productId = info[4];
					updateQty = info[5];
					count = info[6];
					sku = info[7];
					customerId = info[8];
					opm_time = info[9];
					break;
				}
				// 点击继续 + 已配货+确认配货 + 跳过成功
				if (STATE == 1 || STATE == 2 || STATE == 6 || STATE == 7
						|| STATE == 4) {
					String[] info = MyConnection.getMyConnection()
							.getDistributionNextInfo();

					if (info.length == 1) {
						if (STATE == 1) {
							dialogThree.show();
							break;
						}
						if (STATE == 6) {
							OP_CODE = info[0];
							dialogThree.show();
							break;
						}
						if (STATE == 2) {
							dialogThree.show();
							break;
						}
						if (STATE == 4) {
							dialogThree.show();
							break;
						}
						if (STATE == 7) {
							passBool = true;
							dialogThree.show();
							// MyTool.toastShow(SFCDisOnLine.this,
							// "对不起,没有下一个产品可供配货,请点击结束配货按钮提交配货单");
							// btnFinished.setFocusable(true);
							// btnFinished.requestFocus();
							break;
						}
					}
					// btnDone.setText("已配货");
					startPickup = true;
					// btnFinished.setVisibility(View.VISIBLE);
					btnPass.setVisibility(View.VISIBLE);
					btnMore.setVisibility(View.VISIBLE);
					scantext.setFocusable(true);
					scantext.requestFocus();

					showInfo(info[0], info[1], info[2], info[6], info[3],
							info[11]);
					preWsCode = wsCode;
					wsCode = info[1];
					productId = info[4];
					updateQty = info[5];
					count = info[6];
					sku = info[8];
					customerId = info[9];
					opm_time = info[10];

					if (OP_CODE.equals("")) {
						OP_CODE = info[7];
					}
					break;
				}
				if (STATE == 9) {
					btnAddCheck.setVisibility(View.INVISIBLE);
					tvProCount.setText(dialogException.getGoodCount());
					String str = tvProCount.getText().toString();
					if (str.equals("1") || str.equals("0")) {
						tvProCount.setTextColor(0xff555555);
					} else {
						tvProCount.setTextColor(0xffff5500);
					}
					if (dialogException.getGoodCount().equals("0")) {
						scantext.setEnabled(false);
						scantext.setFocusable(false);
					}
					break;
				}
				if (STATE == 10) {
					String[] strs = MyConnection.getMyConnection()
							.getDistributionInfo();
					if (strs == null) {
						MyTool.toastShow(SFCDisOnLine.this, "对不起,没有下一条数据可供配货");
						break;
					}
					wsCode = strs[1];
					productId = strs[4];
					updateQty = strs[5];
					count = strs[6];
					sku = strs[7];
					customerId = strs[8];
					opm_time = strs[9];
					showInfo(strs[0], strs[1], strs[2], strs[6], strs[3],
							strs[10]);
					break;
				}
				// 提交成功 +结束配货
				if (STATE == 3 || STATE == 8 || STATE == 5) {
					Intent intent = new Intent(SFCDisOnLine.this,
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
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				// 点击继续的时候返回了错误退出当前页面
				if (STATE == 1) {
					dialog.show();
					break;
				}
				// 点击提交的时候返回了错误
				if (STATE == 3) {
					dialogThree.show();
					break;
				}
				if (STATE == 2 || STATE == 6 || STATE == 8) {
					if (STATE == 6
							&& MyConnection.getMyConnection().isBinding()) {
						MyTool.toastShow(SFCDisOnLine.this, MyConnection
								.getMyConnection().getMessage());
						// dialogOne.setContent( MyConnection.getMyConnection()
						// .getMessage());
						// dialogOnlyFinish.setConfirmText("退出");
						// dialogOne.show();
						break;
					}

					String[] strs = MyConnection.getMyConnection()
							.getFailedCountAndOpCode();
					if (!strs[0].equals("0")) {
						OP_CODE = strs[1];
						dialogOne.setContent("系统检测到有 " + strs[0]
								+ " 条数据冲突，请将此次配货的 " + strs[0] + " 个产品放回原处");
						dialogOne.show();
						break;
					} else {
						dialogOne.setContent("系统检测到此次配货失败，请将此次配货的全部产品放回原处");
						dialogOne.show();
						// Intent intent = new Intent(SFCDisOnLine.this,
						// SFCDistribution.class);
						// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// startActivity(intent);
					}
					break;
				}
				if (STATE == 4) {
					Intent intent = new Intent(SFCDisOnLine.this,
							SFCDistribution.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}
				if (STATE == 5) {
					dialog.show();
					break;
				}
				MyTool.toastShow(SFCDisOnLine.this, strMsg);
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
				if (dialog.isShowing() || dialog2.isShowing()
						|| dialog301.isShowing() || dialogOne.isShowing()
						|| dialogThree.isShowing()
						|| dialogOnlyFinish.isShowing()
						|| dialogException.isShowing()
						|| imgRefresh.getVisibility() == View.VISIBLE) {
					return;
				}
				// 锁屏
				Intent intent = new Intent(SFCDisOnLine.this,
						LockActivity.class);
				startActivity(intent);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_dis_online);
		init();
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
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		if (dialogThree.isShowing()) {
			dialogThree.dismiss();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		if (MyConfig.getMyConfig().getOrderCommit()
				|| MyConfig.getMyConfig().getOrderDeleteAll()) {
			MyConfig.getMyConfig().setOrderCommit(false);
			MyConfig.getMyConfig().setOrderDeleteAll(false);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (dialogThree.isShowing()) {
				dialogThree.dismiss();
			}
			Intent intent = new Intent(SFCDisOnLine.this, SFCDisConfig.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		// 如果是onpause然后再onresume
		if (pause && MyConfig.getMyConfig().getBoolLock()) {
			MyConfig.getMyConfig().setBoolLock(false);
			STATE = 0;
			getData(0);
		}

		pause = false;
		handler.postDelayed(myRunnable, MyConfig.LOCKTIME);
	}

	public void init() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnBack = (Button) findViewById(R.id.btn_back);
		tvProName = (TextView) findViewById(R.id.tvProName);
		tvClientProNum = (TextView) findViewById(R.id.tvClientProNum);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvShelfNum = (TextView) findViewById(R.id.tvShelfNum);
		tvProCount = (TextView) findViewById(R.id.tvProCount);
		imgInfo = (ImageView) findViewById(R.id.imgInfo);
		btnPass = (Button) findViewById(R.id.btnPass);
		btnFinished = (Button) findViewById(R.id.btnFinished);
		btnMore = (Button) findViewById(R.id.btn_more);
		btnDone = (Button) findViewById(R.id.btnDone);
		scantext = (EditText) findViewById(R.id.scantext);
		btnAddCheck = (Button) findViewById(R.id.btnAddCheck);
		imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
		lineTop = (LinearLayout) findViewById(R.id.lineTop);
		lineBottom = (LinearLayout) findViewById(R.id.lineBottom);
		tvProName.setMovementMethod(ScrollingMovementMethod.getInstance());
		btnMore.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		btnFinished.setOnClickListener(this);
		btnAddCheck.setOnClickListener(this);
		btnPass.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		imgInfo.setOnClickListener(this);
		imgRefresh.setOnClickListener(this);
		scantext.setOnEditorActionListener(this);
		imgInfo.setDrawingCacheEnabled(true);
		scantext.requestFocus();
		scantext.setFocusable(true);

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
						STATE = 9;
						getData(9);
					}

					@Override
					public void btnCancel() {
						// TODO Auto-generated method stub

					}
				});
		dialogOne = new SFCDisNoDataDialog(this);
		dialogOne.setConfirmText("确定");
		dialogOne.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// 访问下一条数据
				STATE = 4;
				getData(4);
			}
		});
		dialogOnlyFinish = new SFCDisNoDataDialog(this);
		dialogOnlyFinish
				.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

					@Override
					public void btnConfirm() {
						// TODO Auto-generated method stub
						SFCDisOnLine.this.finish();
					}

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return true;
					}
				});
		dialog2 = new SFCDisNoDataDialog(this);
		dialog2.setDialogCallback(new SFCDisNoDataDialog.Dialogcallback() {

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SFCDisOnLine.this,
						SFCDistribution.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SFCDisOnLine.this.finish();
			}

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return true;
			}
		});

		dialog = new SFCDisDialog(this);
		dialog.setDialogCallback(new Dialogcallback() {

			@Override
			public void btnContinue() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				STATE = 1;
				getData(1);
			}

			@Override
			public void btnCommit() {
				// TODO Auto-generated method stub
				STATE = 5;
				dialog.dismiss();
				getData(3);
			}

			@Override
			public void btnCheck() {
				// TODO Auto-generated method stub
				MyConfig.getMyConfig().setOrderCommit(false);
				MyConfig.getMyConfig().setOrderDeleteAll(false);
				Intent intent = new Intent(SFCDisOnLine.this,
						SFCDistributionOrder.class);
				intent.putExtra(MyConfig.TAG, OP_CODE);
				startActivity(intent);
			}

			@Override
			public void exitActivity() {
				// TODO Auto-generated method stub
				SFCDisOnLine.this.finish();
			}
		});
		dialog301 = new MyDialog(this);
		dialog301.setContent("退出后，您的配货信息还将保留以便您下次配货，确定退出?");
		dialog301.setConfirmText("确定");
		dialog301.setDialogCallback(new MyDialog.Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SFCDisOnLine.this,
						SFCDistribution.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SFCDisOnLine.this.finish();
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub
			}
		});
		dialogThree = new SFCDisDialog(this);
		dialogThree.setContent("对不起,没有下一个产品可供配货");
		dialogThree.setBtnContinueText("取消");
		dialogThree.setDialogCallback(new Dialogcallback() {

			@Override
			public void exitActivity() {
				// TODO Auto-generated method stub

			}

			@Override
			public void btnContinue() {
				// 取消
				dialogThree.dismiss();
				Intent intent = new Intent(SFCDisOnLine.this,
						SFCDistribution.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}

			@Override
			public void btnCommit() {
				// 提交
				STATE = 3;
				getData(3);
			}

			@Override
			public void btnCheck() {
				// 查看
				MyConfig.getMyConfig().setOrderCommit(false);
				MyConfig.getMyConfig().setOrderDeleteAll(false);
				Intent intent = new Intent(SFCDisOnLine.this,
						SFCDistributionOrder.class);
				intent.putExtra(MyConfig.TAG, OP_CODE);
				startActivity(intent);
			}
		});
		Intent intent = getIntent();
		String[] strs = intent.getStringArrayExtra(MyConfig.TAG);
		if (strs != null) {
			OP_CODE = strs[0];
			CONFIG_ORDER_TYPE = strs[1];
			CONFIG_ORDER_SORT = strs[2];
			// CONFIG_SHELF_NUM = strs[3];
			if (CONFIG_ORDER_TYPE.equals("0")) {
				tvTitle.setText("一票一件");
			} else {
				tvTitle.setText("一票多件");
			}
			time = MyTool.getSFCTime();
			dialog.show();
			return;
		}
		String[] info = intent.getStringArrayExtra("S");
		if (info != null) {

			// for(int i=0;i<info.length;i++){
			// System.out.println("----------->>>"+info[i]);
			// }
			CONFIG_ORDER_TYPE = info[11];
			CONFIG_ORDER_SORT = info[12];
			// CONFIG_SHELF_NUM = info[12];
			CONFIG_BOX_NUM = info[14];
			time = info[15];

			if (CONFIG_ORDER_TYPE.equals("0")) {
				tvTitle.setText("一票一件");
			} else {
				tvTitle.setText("一票多件");
			}

			wsCode = info[1];
			preWsCode = info[12];
			productId = info[4];
			updateQty = info[5];
			count = info[6];
			sku = info[7];
			customerId = info[8];
			opm_time = info[9];
			showInfo(info[0], info[1], info[2], info[6], info[3], info[10]);
		}
		scantext.setFocusable(true);
		scantext.requestFocus();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		touch = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!startPickup) {
				finish();
				return true;
			}
			dialog301.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		touch = true;
		myClick(v);
	}

	public void myClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			if (!startPickup) {
				finish();
				break;
			}
			dialog301.show();
			break;
		case R.id.btn_more:
			SFCPOP();
			break;
		case R.id.btnDone:
			// 点击确认配货
			MyTool.hideInputKeyBroad(SFCDisOnLine.this);
			// 点击确认配货
			barcode = scantext.getText().toString();
			if (startPickup) {
				STATE = 2;
				getData(2);
			} else {
				STATE = 6;
				getData(6);

			}
			// 已配货
			scantext.setText("");
			scantext.requestFocus();
			scantext.setFocusable(true);
			btnDone.setVisibility(View.INVISIBLE);
			break;
		case R.id.btnPass:
			if (!startPickup) {
				STATE = 10;
				getData(STATE);
				break;
			}
			STATE = 7;
			getData(7);
			btnDone.setVisibility(View.INVISIBLE);
			break;
		case R.id.btnFinished:
			STATE = 8;
			getData(8);
			btnDone.setVisibility(View.INVISIBLE);
			break;
		case R.id.imgInfo:
			Intent intent1 = new Intent(SFCDisOnLine.this, ImgLoad.class);
			intent1.putExtra(MyConfig.TAG, imgUrl);
			startActivity(intent1);
			break;
		case R.id.imgRefresh:
			imgRefresh.setVisibility(View.INVISIBLE);
			lineTop.setVisibility(View.VISIBLE);
			lineBottom.setVisibility(View.VISIBLE);
			OP_CODE = "";
			getData(0);
			break;
		case R.id.btn1:
			p.dismiss();
			MyConfig.getMyConfig().setOrderCommit(false);
			Intent intent = new Intent(SFCDisOnLine.this,
					SFCDistributionOrder.class);
			intent.putExtra(MyConfig.TAG, SFCDisOnLine.this.OP_CODE);
			startActivity(intent);
			break;
		case R.id.btn2:
			p.dismiss();
			// dialog4.show();
			Intent intentBox = new Intent(SFCDisOnLine.this,
					SFCDistributionBox.class);
			intentBox.putExtra(MyConfig.TAG, OP_CODE);
			startActivity(intentBox);
			break;
		case R.id.btnAddCheck:
			int i = 0;
			try {
				i = Integer.parseInt(tvProCount.getText().toString());
			} catch (Exception e) {
				// TODO: handle exception
				MyTool.toastShow(SFCDisOnLine.this, "数量有误,请刷新数据");
				break;
			}
			dialogException.setCountAll(i);
			dialogException.show();
			break;
		default:
			break;
		}
	}

	// /**
	// * 解锁未点击确认配货获取数据
	// */
	// private void getData() {
	// STATE = 0;
	// linePro.setVisibility(View.VISIBLE);
	// linePro.requestFocus();
	// linePro.setFocusable(true);
	// MyConnection.getMyConnection().acceptServer(
	// MyConfig.URL_COMMON,
	// MyConnection.getMyConnection().writeJsonWithUserInfo(
	// new String[] { "ws_code", "order_type", "sortBy",
	// " product_id", "unLock", "isContinue" },
	// new String[] { wsCode, CONFIG_ORDER_TYPE,
	// CONFIG_ORDER_SORT, productId, "1", "1" },
	// "pdaPickup"), handler);
	// }

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

	public void showInfo(String proName, String shelfNum, String clientProNum,
			String proCount, String pic, String tpic) {
		tvProName.setText(proName);
		tvShelfNum.setText(shelfNum);
		tvProCount.setText(proCount);
		tvClientProNum.setText(clientProNum);
		imgUrl = pic;
		// imgInfo.setImageBitmap(MyConfig.getMyConfig().getBitmap());
		String ss = "";
		try {
			ss = MyConfig.URL_PRE + tpic;
			AsynImageLoader asynImageLoader = new AsynImageLoader();

			asynImageLoader.showImageAsyn(imgInfo, ss, R.drawable.no_img);
		} catch (Exception ex) {
			System.out.println(ss);
			System.out.println("IMG Exception Message-->" + ex.getMessage());
		}
		String str = tvProCount.getText().toString();
		if (str.equals("1") || str.equals("0")) {
			tvProCount.setTextColor(0xff555555);
		} else {
			tvProCount.setTextColor(0xffff5500);
		}
	}

	/**
	 * 
	 * @param index
	 *            6 确认配货; 2 已配货; 3点击已配货或确认配货没有下一条数据时候的提交;4配货冲突的时候点击确认获取下一条数据
	 *            ；7表示跳过;8表示结束;0表示解锁,1表示继续,9对话框确定添加至盘点,10表示没有订单时的跳过
	 * 
	 */
	public void getData(int index) {

		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		switch (index) {
		case 0:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "ws_code", "order_type", "sortBy",
									"product_id", "unLock", "isContinue",
									"end_time" },
							new String[] { tvShelfNum.getText().toString(),
									CONFIG_ORDER_TYPE, CONFIG_ORDER_SORT,
									productId, "1", "1", time }, "pdaPickup"),
					handler);
			break;
		case 1:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "opm_orders_type",
									"sortBy", "isContinue", "pass", "end_time",
									"product_id" },
							new String[] { OP_CODE, CONFIG_ORDER_TYPE,
									CONFIG_ORDER_SORT, "1", "1", time,
									productId }, "pdaPickup"), handler);
			break;
		case 2:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "product_id",
									"container_code", "ws_code",
									"new_container", "sortBy", "order_type",
									"updateQty", "num", "end_time",
									"old_count", "barcode" },
							new String[] { OP_CODE, productId, CONFIG_BOX_NUM,
									wsCode, "0", CONFIG_ORDER_SORT,
									CONFIG_ORDER_TYPE, updateQty,
									tvProCount.getText().toString(), time,
									count, barcode }, "pdaPickupSubmit"),
					handler);
			break;
		case 3:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "status", "product_id" },
							new String[] { OP_CODE, "1", productId },
							"pdaPickupSubmit1"), handler);
			break;
		case 4:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "ws_code", "order_type", "sortBy",
									"isContinue", "end_time", "product_id" },
							new String[] { wsCode, CONFIG_ORDER_TYPE,
									CONFIG_ORDER_SORT, "1", time, productId },
							"pdaPickup"), handler);
			break;
		case 6:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "product_id",
									"container_code", "ws_code",
									"new_container", "sortBy", "order_type",
									"updateQty", "num", "end_time",
									"old_count", "barcode" },
							new String[] { OP_CODE, productId, CONFIG_BOX_NUM,
									wsCode, "1", CONFIG_ORDER_SORT,
									CONFIG_ORDER_TYPE, updateQty,
									tvProCount.getText().toString(), time,
									count, barcode }, "pdaPickupSubmit"),
					handler);
			break;
		case 7:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "product_id",
									"container_code", "ws_code",
									"new_container", "sortBy", "order_type",
									"updateQty", "num", "pass", "end_time",
									"old_count", "barcode" },
							new String[] { OP_CODE, productId, CONFIG_BOX_NUM,
									wsCode, "0", CONFIG_ORDER_SORT,
									CONFIG_ORDER_TYPE, updateQty,
									tvProCount.getText().toString(), "1", time,
									count, barcode }, "pdaPickupSubmit"),
					handler);
			break;
		case 8:
			if (passBool) {
				MyConnection.getMyConnection().acceptServer(
						MyConfig.URL_COMMON,
						MyConnection.getMyConnection().writeJsonWithUserInfo(
								new String[] { "op_code", "product_id",
										"container_code", "ws_code",
										"new_container", "sortBy",
										"order_type", "updateQty", "num",
										"status", "pass", "end_time",
										"old_count", "barcode" },
								new String[] { OP_CODE, productId,
										CONFIG_BOX_NUM, wsCode, "0",
										CONFIG_ORDER_SORT, CONFIG_ORDER_TYPE,
										updateQty,
										tvProCount.getText().toString(), "1",
										"1", time, count, barcode },
								"pdaPickupSubmit"), handler);
				break;
			}
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "op_code", "product_id",
									"container_code", "ws_code",
									"new_container", "sortBy", "order_type",
									"updateQty", "num", "status", "end_time",
									"old_count", "barcode" },
							new String[] { OP_CODE, productId, CONFIG_BOX_NUM,
									wsCode, "0", CONFIG_ORDER_SORT,
									CONFIG_ORDER_TYPE, updateQty,
									tvProCount.getText().toString(), "1", time,
									count, barcode }, "pdaPickupSubmit"),
					handler);
			break;
		case 9:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "updateQty", "order_type",
									"ws_code", "opm_time", "num", "oldNum",
									"customer_id", "sku" },
							new String[] { dialogException.getCoutAll(),
									CONFIG_ORDER_TYPE, wsCode, opm_time,
									dialogException.getExceptionCount(),
									dialogException.getCoutAll(), customerId,
									sku }, "pdaCreateStocktake"), handler);
			break;
		case 10:
			MyConnection.getMyConnection().acceptServerWithImg(
					MyConfig.URL_COMMON,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "ws_code", "order_type", "sortBy",
									"container_code", "pass", "end_time",
									"barcode", "product_id" },
							new String[] { wsCode, CONFIG_ORDER_TYPE,
									CONFIG_ORDER_SORT, CONFIG_BOX_NUM, "1",
									MyTool.getSFCTime(), barcode, productId },
							"pdaPickupSubmit"), handler);
			break;
		default:
			break;
		}
	}

	private boolean screen() {

		if (scantext.getText().toString().equals("")) {
			MyTool.playFailedSound();
			MyTool.toastShow(SFCDisOnLine.this, "请输入SKU条码");
			return true;
		}
		OPM_QUANTITY = tvProCount.getText().toString();

		if (Integer.parseInt(OPM_QUANTITY) > 1) {

			btnDone.setVisibility(View.VISIBLE);
			btnDone.requestFocus();
			btnDone.setFocusable(true);
			return true;
		}

		MyTool.hideInputKeyBroad(SFCDisOnLine.this);
		// 点击确认配货
		barcode = scantext.getText().toString();
		if (startPickup) {
			STATE = 2;
			getData(2);
		} else {
			STATE = 6;
			getData(6);

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
