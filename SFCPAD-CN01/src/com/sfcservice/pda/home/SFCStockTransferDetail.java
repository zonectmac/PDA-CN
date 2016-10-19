package com.sfcservice.pda.home;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sfcservice.bean.StockTransferDetail;
import com.sfcservice.component.MyDialogGood;
import com.sfcservice.component.MyDialogGood.Dialogcallback;
import com.sfcservice.img.AsynImageLoader;
import com.sfcservice.img.ImgLoad;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCStockTransferDetail extends Activity implements OnClickListener {

	private TextView lastQyt, lastShelveStock, lastSku, shelveNum, proSku,
			tvShow;
	private EditText proNum, scanStock;
	private Button lackStock;
	private LinearLayout linePro;
	private ImageView imageStock;
	AsynImageLoader asynImageLoader;
	private String userCode, picStr, inputQty = "", opcode, shelvelocNum = "",
			sku = "";
	private boolean checkRe = true;
	private boolean flag = false;
	private boolean isInPut = true;// �ٻ���ʱ���Ƿ�ɱ༭
	SharedPreferences sp;
	private StockTransferDetail std;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("���ڼ������...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCStockTransferDetail.this, "���ӷ�����ʧ��");
				break;
			case MyConfig.RESULTS:
				linePro.setVisibility(View.INVISIBLE);
				Bundle datas = msg.getData();
				String content = datas.getString(MyConfig.TAG);
				if (flag) {// ��ȡת�Ƶĵ�
					std = MyConnection.getMyConnection().getStockTransfer(
							userCode);
					System.out.println("--==wewe==" + std.toString());
					if (std.getOp_code() != null) {
						setTextAll(std);
						scanStock.setText("");
					} else {// ���һ��
						MyDialogGood myDialogGood = new MyDialogGood(
								SFCStockTransferDetail.this);
						myDialogGood.hideCancle();// ����cancel��ť
						myDialogGood.setContent(content);
						myDialogGood.setDialogCallback(new Dialogcallback() {

							@Override
							public boolean exitActivity() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public void btnConfirm() {
								Intent intent = new Intent(
										SFCStockTransferDetail.this,
										SFCStockTransfer.class);
								startActivity(intent);
								SFCStockTransferDetail.this.finish();

							}

							@Override
							public void btnCancel() {
								// TODO Auto-generated method stub

							}
						});
						myDialogGood.show();
					}
					flag = false;
				} else {
					getData();
				}

				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCStockTransferDetail.this, strMsg);
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
		setContentView(R.layout.sfc_stock_transfer_detail);
		asynImageLoader = new AsynImageLoader();
		sp = getSharedPreferences("test", Activity.MODE_PRIVATE);
		// ʹ��getString�������value��ע���2��������value��Ĭ��ֵ
		userCode = sp.getString("user", "");
		opcode = sp.getString("opCode", "");
		initView();
	}

	private void initView() {
		lastQyt = (TextView) findViewById(R.id.last_goods_qyt_stock);
		lastShelveStock = (TextView) findViewById(R.id.last_shelve_locstock);
		lastSku = (TextView) findViewById(R.id.last_sku_stock);
		shelveNum = (TextView) findViewById(R.id.tv_Shelf_loc_Num_stock);
		proSku = (TextView) findViewById(R.id.tv_pro_sku_stock);
		proNum = (EditText) findViewById(R.id.et_ProCount_stock);
		proNum.setInputType(InputType.TYPE_CLASS_NUMBER);
		lackStock = (Button) findViewById(R.id.btn_AddCheck_stock);
		lackStock.setOnClickListener(this);
		imageStock = (ImageView) findViewById(R.id.iv_imgInfo_stock);
		imageStock.setOnClickListener(this);
		scanStock = (EditText) findViewById(R.id.et_scan_stock);
		findViewById(R.id.btn_Done_stransfer).setOnClickListener(this);
		findViewById(R.id.btn_back).setOnClickListener(this);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		std = new StockTransferDetail();
		Intent intent = this.getIntent();
		std = (StockTransferDetail) intent.getSerializableExtra("std");
		setTextAll(std);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);// ���handler�ڴ�й©����
	}

	private void setTextAll(StockTransferDetail std2) {
		lastShelveStock.setText(shelvelocNum);
		lastSku.setText(sku);
		lastQyt.setText(inputQty);

		shelveNum.setText(std2.getShelve_loc_num());
		proSku.setText(std2.getPro_sku());
		proNum.setText(std2.getPro_qyt());
		try {
			if (std2.getPro_pic().contains("http")) {
				asynImageLoader.showImageAsyn(imageStock, std2.getPro_pic(),
						R.drawable.no_img);
			} else {
				asynImageLoader.showImageAsyn(imageStock, MyConfig.URL_PRE
						+ std2.getPro_pic(), R.drawable.no_img);
			}
		} catch (Exception ex) {
			System.out.println("IMG Exception Message-->" + ex.getMessage());
		}
		picStr = std2.getPro_pic();
		shelvelocNum = shelveNum.getText().toString();
		sku = proSku.getText().toString();
		isInPut = true;
		proNum.setFocusable(false);
		proNum.setFocusableInTouchMode(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_AddCheck_stock:
			if (isInPut) {// �����ٻ�������������
				proNum.setFocusable(true);
				proNum.setFocusableInTouchMode(true);
				proNum.requestFocus();
				isInPut = false;

			}
			proNum.setText("");
			break;
		case R.id.btn_Done_stransfer:
			inputQty = proNum.getText().toString().trim();
			String tranContain = scanStock.getText().toString().trim();
			if (checkQty()) {
				Commit(inputQty, tranContain, std.getOp_code(),
						std.getShelve_loc_num(), std.getProduct_id());
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.iv_imgInfo_stock:
			Intent intent = new Intent(SFCStockTransferDetail.this,
					ImgLoad.class);
			intent.putExtra(MyConfig.TAG, picStr);
			startActivity(intent);
			break;

		}
	}

	private boolean checkQty() {
		if (checkRe == false) {
			return true;
		}
		if (TextUtils.equals(inputQty, "")) {
			Toast.makeText(SFCStockTransferDetail.this, "�������ϼ�����",
					Toast.LENGTH_SHORT).show();
			proNum.requestFocus();
			proNum.setFocusable(true);
			return false;
		}

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(inputQty);
		if (!isNum.matches()) {
			MyTool.playFailedSound();
			Toast.makeText(SFCStockTransferDetail.this, "���������֣�",
					Toast.LENGTH_SHORT).show();
			proNum.requestFocus();
			proNum.setFocusable(true);
			return false;
		}
		int qty = Integer.parseInt(inputQty);

		if (qty < 0) {
			MyTool.playFailedSound();
			Toast.makeText(SFCStockTransferDetail.this, "���������С��0��",
					Toast.LENGTH_SHORT).show();
			proNum.requestFocus();
			proNum.setFocusable(true);
			return false;
		}
		if (scanStock.getText().toString().equals("")) {
			MyTool.playFailedSound();
			Toast.makeText(SFCStockTransferDetail.this, "��������ţ�",
					Toast.LENGTH_SHORT).show();
			scanStock.requestFocus();
			scanStock.setFocusable(true);
			return false;
		}
		return true;

	}

	/**
	 * �����ɺ��ύ����
	 */
	private void Commit(String qyt, String container, String opCode,
			String wsCode, String productId) {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "postnum", "container_code", "op_code",
								"ws_code", "product_id" },
						new String[] { qyt, container, opCode, wsCode,
								productId }, "ContainerBinding"), handler);

	}

	/**
	 * ��ȡҪת�Ƶĵ�
	 */
	private void getData() {
		flag = true;
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "op_code" }, new String[] { opcode },
						"getDetailByOpcode"), handler);
	}
}
