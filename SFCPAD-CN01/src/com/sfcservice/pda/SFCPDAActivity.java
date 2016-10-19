package com.sfcservice.pda;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfcservice.bean.CheckBean;
import com.sfcservice.component.ProDialog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.SFCPopWindow.BtnClickCallBack;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;
import com.sfcservice.pda.home.SFCBindingTransfer;
import com.sfcservice.pda.home.SFCBoxSpecial;
import com.sfcservice.pda.home.SFCCheckAll;
import com.sfcservice.pda.home.SFCCheckAllSKU;
import com.sfcservice.pda.home.SFCContainerShelvesBinding;
import com.sfcservice.pda.home.SFCContainerShelvesBindingDown;
import com.sfcservice.pda.home.SFCCutSheetBack;
import com.sfcservice.pda.home.SFCDetectionSKU;
import com.sfcservice.pda.home.SFCDetectionShelves;
import com.sfcservice.pda.home.SFCDistribution;
import com.sfcservice.pda.home.SFCNewProduct;
import com.sfcservice.pda.home.SFCStockTransfer;

public class SFCPDAActivity extends Activity implements OnClickListener {
	/**
	 * 解决在activity刚加载的时候跳转至检测货架
	 */
	private boolean bool;
	private TextView tvUserName, tvCode;
	private ProDialog dialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				dialog.setTvShow("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				dialog.dismiss();
				MyTool.toastShow(SFCPDAActivity.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				// MyTool.playSuccessSound();
				// 有未完成的盘点单
				dialog.dismiss();
				ArrayList<CheckBean> listBean = new ArrayList<CheckBean>();
				MyConnection.getMyConnection().getSKUInfo(listBean);
				MyConfig.getMyConfig().setListBean(listBean);
				Intent intent1 = new Intent(SFCPDAActivity.this,
						SFCCheckAllSKU.class);
				intent1.putExtra(MyConfig.TAG, MyConnection.getMyConnection()
						.getWsCode());
				intent1.putExtra("PDA", true);
				startActivity(intent1);
				break;
			case MyConfig.RESULTF:
				// MyTool.playFailedSound();
				// 没有未完成的盘点单
				dialog.dismiss();
				Intent intent = new Intent(SFCPDAActivity.this,
						SFCCheckAll.class);
				startActivity(intent);
				// Bundle data = msg.getData();
				// String strMsg = data.getString(MyConfig.TAG);
				// MyTool.toastShow(SFCPDAActivity.this, strMsg);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_home);
		init();
	}

	private void init() {
		bool = true;
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvCode = (TextView) findViewById(R.id.tvCode);
		tvUserName.setText(MyConfig.getMyConfig().getUsers()[0]);
		GridView g = (GridView) findViewById(R.id.SFCGrid);
		g.setAdapter(new MyAdapter());
		tvUserName.setOnClickListener(this);
		dialog = new ProDialog(this);
		tvCode.setText(getVerName(this));
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return MyConfig.SFCHomeItemText.length;
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
			final int myposition = position;
			View v = LayoutInflater.from(SFCPDAActivity.this).inflate(
					R.layout.sfc_home_item, null);

			LinearLayout lineItem = (LinearLayout) v
					.findViewById(R.id.lineItem);
			TextView tv = (TextView) v.findViewById(R.id.item_tv);
			ImageView img = (ImageView) v.findViewById(R.id.item_img);
			img.setImageResource(MyConfig.SFCHomeItemImg[position]);
			tv.setText(MyConfig.SFCHomeItemText[position]);

			lineItem.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus && v.isInTouchMode()) {
						switch (myposition) {
						case 0:
							if (bool) {
								break;
							}
							SFCStartActivity(SFCDetectionShelves.class);
							break;
						case 1:
							SFCStartActivity(SFCDetectionSKU.class);
							break;
						case 2:
							SFCStartActivity(SFCCutSheetBack.class);
							break;
						// case 2:
						// SFCStartActivity(SFCBindingShelves.class);
						// break;
						case 3:
							SFCStartActivity(SFCNewProduct.class);
							break;
						case 4:
							// SFCStartActivity(SFCStockTransferMerge.class);
							SFCStartActivity(SFCBindingTransfer.class);
							break;
						case 5:
							SFCStartActivity(SFCContainerShelvesBinding.class);
							break;
						case 6:
							// SFCStartActivity(SFCCheckAll.class);
							dialog.show();
							getData();
							break;
						case 7:
							SFCStartActivity(SFCDistribution.class);
							break;
						case 8:
							SFCStartActivity(SFCBoxSpecial.class);
							break;
						case 9:
							SFCStartActivity(SFCContainerShelvesBindingDown.class);
							break;
						case 10:
							SFCStartActivity(SFCStockTransfer.class);
							break;
						default:
							break;
						}
						bool = false;
					}
				}
			});
			lineItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bool = false;
					switch (myposition) {
					case 0:
						SFCStartActivity(SFCDetectionShelves.class);
						break;
					case 1:
						SFCStartActivity(SFCDetectionSKU.class);
						break;
					case 2:
						SFCStartActivity(SFCCutSheetBack.class);
						break;
					// case 2:
					// SFCStartActivity(SFCBindingShelves.class);
					// break;
					case 3:
						SFCStartActivity(SFCNewProduct.class);
						break;
					case 4:
						// SFCStartActivity(SFCStockTransferMerge.class);
						SFCStartActivity(SFCBindingTransfer.class);
						break;
					case 5:
						SFCStartActivity(SFCContainerShelvesBinding.class);
						break;
					case 6:
						SFCStartActivity(SFCCheckAll.class);
						dialog.show();
						getData();
						break;
					case 7:
						SFCStartActivity(SFCDistribution.class);
						break;
					case 8:
						SFCStartActivity(SFCBoxSpecial.class);
						break;
					case 9:
						SFCStartActivity(SFCContainerShelvesBindingDown.class);
						break;
					case 10:
						// SharedPreferences sp = getSharedPreferences("test",
						// Activity.MODE_PRIVATE);
						// int index =
						// MyConnection.getMyConnection().QueryopCode(
						// "stock_transfer_detail",
						// sp.getString("opCode", ""));
						// String opcode = sp.getString("opCode", "");
						// if (!opcode.equals("")) {// 断电情况
						// SFCStartActivity(SFCStockTransferDetail.class);
						// } else {
						SFCStartActivity(SFCStockTransfer.class);
						// }
						break;
					default:
						break;
					}
				}
			});
			return v;
		}
	}

	// 快捷键功能省去-----------------------------------------
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// System.out.println("------------>");
	// if (keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_G) {
	// SFCStartActivity(SFCDetectionShelves.class);
	// return true;
	// }
	// if (keyCode == KeyEvent.KEYCODE_5 || keyCode == KeyEvent.KEYCODE_J) {
	// SFCStartActivity(SFCCutSheetBack.class);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	private void SFCStartActivity(Class<?> c) {
		Intent intent = new Intent(SFCPDAActivity.this, c);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		bool = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			SFCPopWindow.getSFCPopWindow().show(this, "是否退出",
					findViewById(R.id.line), new BtnClickCallBack() {

						@Override
						public void btnClick() {
							// TODO Auto-generated method stub
							finish();
						}
					});
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SFCPopWindow.getSFCPopWindow().show(this, "是否退出",
				findViewById(R.id.line), new BtnClickCallBack() {

					@Override
					public void btnClick() {
						// TODO Auto-generated method stub
						finish();
					}
				});
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	private String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.sfcservice.pda", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "版本号 : " + verName;

	}

	/**
	 * 获取数据
	 */
	public void getData() {
		dialog.show();
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_CHECK,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] {}, null, "getSkuByLastWs"), handler);
	}
}