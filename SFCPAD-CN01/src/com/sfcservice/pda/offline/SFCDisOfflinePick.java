package com.sfcservice.pda.offline;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.sfcservice.bean.OfflinePickDetail;
import com.sfcservice.bean.OfflinePickDetail2;
import com.sfcservice.component.MyDialogGood;
import com.sfcservice.component.MyDialogGood.Dialogcallback;
import com.sfcservice.component.MyEditDialog;
import com.sfcservice.img.AsynImageLoader;
import com.sfcservice.img.ImgLoad;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisOfflinePick extends Activity implements OnClickListener,
		OnEditorActionListener {

	private TextView last_shelve_location, last_sku, last_goods_qyt,
			tv_Shelf_loc_Num, tv_pro_sku, tv_ProName;
	private MyBroadCast broadCast;
	private ImageView iv_imgInfo;
	private Button btn_missPro, btn_Done;// �ٻ�
	private EditText et_scan_sku, et_ProCount;
	private String userCode;
	private String shelveloc = "", prosku = "", qytnum = "", primaryID;
	private OfflinePickDetail opd;
	private OfflinePickDetail2 opd2;
	private LinearLayout linePro;
	private TextView tvShow;
	int qyt = 0;// Ӧ��Ҫ�õ�����
	int qyt2 = 0;// ʵ���õ�����
	int offCount = 0;// ȱ������
	int changeContainer = -1;
	MyEditDialog editDialog;
	AsynImageLoader asynImageLoader;
	private String opt_type;// �¼ܵ�����
	private String undershelve_code;// �¼ܵ���
	private String productIds;// ��ƷID
	private String wsCodes;// ���ܺ�
	private String opt_status = "4";// �¼ܵ�״̬��4Ϊ��ɣ�6Ϊ�ٻ�
	private String picStr;// ͼƬ��ַ
	private List<OfflinePickDetail> unPicklist;// �ٻ���ʱ���ȡδ����Ķ����������м��list
	private boolean isLast = true;// �ٻ���ʱ���Ƿ��������
	private boolean isInPut = true;// �ٻ���ʱ���Ƿ�ɱ༭
	List<OfflinePickDetail> opdLackList;// �ٻ���ʱ�򷵻ص�list

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_dis_offline_item);
		SharedPreferences sp = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// ʹ��getString�������value��ע���2��������value��Ĭ��ֵ
		userCode = sp.getString("user", "");
		opt_type = sp.getString("opt_type", "");
		if (opt_type.equals("1")) {
			undershelve_code = sp.getString("more", "");
		} else {
			undershelve_code = sp.getString("single", "");
		}
		opd2 = new OfflinePickDetail2();
		// �ӱ��ػ�ȡ��ϸ�������Ϣ
		opd2 = selectRecord2("0", "''");
		if (opd2 == null) {
			opd2 = selectRecord2("''", "3");
		}
		asynImageLoader = new AsynImageLoader();

		// �ϵ�������жϸ��¼ܵ��Ƿ��ٻ�
		int counts = MyConnection.getMyConnection().QueryLack("2");
		if (counts != 0) {
			opt_status = "6";
		} else {
			opt_status = "4";
		}
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

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
				if (et_scan_sku.hasFocus()) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								et_scan_sku.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
					et_scan_sku.setText("");
					et_scan_sku.append(showstr);
				}
				btn_Done.setFocusable(true);
				btn_Done.requestFocus();
			}
		}

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (!et_scan_sku.getText().toString().equals("")) {
			MyTool.hideInputKeyBroad(SFCDisOfflinePick.this);
			btn_Done.setFocusable(true);
			btn_Done.requestFocus();
		}
		return false;
	}

	/**
	 * ��ѯȻ����ʾ
	 */
	private void selectShow() {
		opd2 = null;
		opd2 = selectRecord2("0", "''");
		System.out.println("------====opdss==" + opd2);
		if (opd2 == null) {
			opd2 = selectRecord2("''", "3");
			System.out.println("------====opds3s==" + opd2);
			if (opd2 == null) {
				CommitAll();// ���һ���ύ
				return;
			}
		}
		setTextAll(opd2);
	}

	/**
	 * �������״̬������˴����ݿ��ѯ��ϸ��Ϣ
	 */
	private void selectRecord(List<OfflinePickDetail> list, String status1,
			String status2) {
		String sql = "select user_login_id ,op_code,opm_id,shelve_loc_num,pro_sku,orders_code,product_id,pro_qyt,pro_name,pro_pic,opm_sortcode,pro_state from offline_pickdetail where pro_state in("
				+ status1
				+ ","
				+ status2
				+ ")"
				+ " and user_login_id="
				+ "'"
				+ userCode + "'";
		opd = new OfflinePickDetail();
		try {
			List<Object> listObj = MyConnection.getMyConnection()
					.queryData2Object(sql, null, opd);
			if (listObj != null) {
				for (Object object : listObj) {
					opd = (OfflinePickDetail) object;
					list.add(opd);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �������״̬������˴����ݿ��ѯ��ϸ��Ϣ
	 */
	private OfflinePickDetail2 selectRecord2(String status1, String status2) {
		String sql = "select *from offline_pickdetail where pro_state in("
				+ status1 + "," + status2 + ")" + " and user_login_id=" + "'"
				+ userCode + "'" + "ORDER BY opm_sortcode limit 1";
		System.out.println("---====sql===" + sql);
		OfflinePickDetail2 opds = MyConnection.getMyConnection().getOPD2(sql,
				null);

		return opds;
	}

	private void initView() {
		broadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyConfig.ACTION);
		registerReceiver(broadCast, filter);
		last_shelve_location = (TextView) findViewById(R.id.last_shelve_location);
		last_sku = (TextView) findViewById(R.id.last_sku);
		last_goods_qyt = (TextView) findViewById(R.id.last_goods_qyt);
		tv_Shelf_loc_Num = (TextView) findViewById(R.id.tv_Shelf_loc_Num);
		tv_pro_sku = (TextView) findViewById(R.id.tv_pro_sku);
		et_ProCount = (EditText) findViewById(R.id.et_ProCount);
		et_ProCount.setInputType(InputType.TYPE_CLASS_NUMBER);
		tv_ProName = (TextView) findViewById(R.id.tv_ProName);
		findViewById(R.id.btn_back).setOnClickListener(this);
		iv_imgInfo = (ImageView) findViewById(R.id.iv_imgInfo);
		iv_imgInfo.setOnClickListener(this);
		btn_missPro = (Button) findViewById(R.id.btn_AddCheck);
		btn_missPro.setOnClickListener(this);
		et_scan_sku = (EditText) findViewById(R.id.et_scan_sku);
		et_scan_sku.setOnEditorActionListener(this);
		btn_Done = (Button) findViewById(R.id.btn_Done);
		btn_Done.setOnClickListener(this);
		findViewById(R.id.btn_change_container).setOnClickListener(this);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		setTextAll(opd2);
	}

	private void setTextAll(OfflinePickDetail2 opds2) {
		last_shelve_location.setText(shelveloc);
		last_sku.setText(prosku);
		last_goods_qyt.setText(qytnum);
		tv_Shelf_loc_Num.setText(opds2.getShelve_loc_num());
		tv_pro_sku.setText(opds2.getPro_sku());
		et_ProCount.setText(opds2.getPro_qyt());
		tv_ProName.setText(opds2.getPro_name());
		productIds = opds2.getProduct_id();
		wsCodes = opds2.getShelve_loc_num();
		primaryID = opds2.get_id();// ����ID
		System.out.println("----===_id==" + primaryID);
		picStr = opds2.getPro_pic();// ͼƬ��ַ
		try {
			asynImageLoader.showImageAsyn(iv_imgInfo,
					MyConfig.URL_PRE + picStr, R.drawable.no_img);
		} catch (Exception ex) {
			System.out.println("IMG Exception Message-->" + ex.getMessage());
		}
		shelveloc = tv_Shelf_loc_Num.getText().toString();
		prosku = tv_pro_sku.getText().toString();
		qyt = Integer.parseInt(et_ProCount.getText().toString());
		et_ProCount.setFocusable(false);
		et_ProCount.setFocusableInTouchMode(false);
		isInPut = true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_AddCheck:// �ٻ�
			if (isInPut) {// �����ٻ�������������
				et_ProCount.setFocusable(true);
				et_ProCount.setFocusableInTouchMode(true);
				et_ProCount.requestFocus();
				isInPut = false;

			}
			et_ProCount.setText("");
			break;
		case R.id.btn_Done:
			if (TextUtils.equals(et_scan_sku.getText().toString().trim(),
					tv_pro_sku.getText().toString())
					&& !TextUtils.equals(et_ProCount.getText().toString(), "")
					|| et_ProCount.getText().toString().trim().equals("0")) {// ���Ϊ0����ɨsku
				qyt2 = Integer.parseInt(et_ProCount.getText().toString());// ʵ���û�������
				qytnum = et_ProCount.getText().toString().trim();
				// ����ʵ���û��Ĳ�
				offCount = qyt - qyt2;

				if (offCount > 0) {

					opt_status = "6";
					// �ٻ���ʱ���ȡʣ��δ����Ķ���
					unPicklist = new ArrayList<OfflinePickDetail>();
					selectRecord(unPicklist, "0", "''");// ��ʣ��Ķ�����ȡ��Ҫ��Ķ���,ֻ��ȡǰ���
					// �ٻ��ύ
					LackProCommit(et_ProCount.getText().toString(), productIds,
							wsCodes);

				} else if (offCount < 0) {
					Toast.makeText(SFCDisOfflinePick.this, "��д��������",
							Toast.LENGTH_SHORT).show();
				} else {
					// �ı���ﵥ�����״̬
					MyConnection.getMyConnection().updateProStateById(
							primaryID, "1");
					selectShow();
					et_scan_sku.setText("");
				}
				MyTool.playSuccessSound();
			} else {
				MyTool.playFailedSound();
				Toast.makeText(getApplicationContext(), "�ͻ���Ʒ�Ų�һ�£�",
						Toast.LENGTH_SHORT).show();
			}
			et_scan_sku.setFocusable(true);
			et_scan_sku.setFocusableInTouchMode(true);
			et_scan_sku.requestFocus();
			break;
		case R.id.iv_imgInfo:
			Intent intent = new Intent(SFCDisOfflinePick.this, ImgLoad.class);
			intent.putExtra(MyConfig.TAG, picStr);//
			startActivity(intent);
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_change_container:
			editDialog = new MyEditDialog(SFCDisOfflinePick.this);
			editDialog.isDismmis();// ���ȷ������dialog��ʧ
			editDialog.setDialogCallback(new MyEditDialog.Dialogcallback() {

				@Override
				public boolean exitActivity() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void btnConfirm() {
					changeContainer = 1;
					linePro.setVisibility(View.VISIBLE);
					linePro.requestFocus();
					linePro.setFocusable(true);

					MyConnection
							.getMyConnection()
							.acceptServer(
									MyConfig.URL_COMMON,
									MyConnection
											.getMyConnection()
											.writeJsonWithUserInfo(
													new String[] { "op_code",
															"container" },
													new String[] {
															undershelve_code,
															editDialog
																	.getAddBoxNum() },
													"pdaOffPickupAddContainer"),
									handler);
				}

				@Override
				public void btnCancel() {
					editDialog.dismiss();

				}
			});
			editDialog.show();
			break;
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("���ڼ������...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCDisOfflinePick.this, "���ӷ�����ʧ��");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (changeContainer == 1) {// �����
					editDialog.dismiss();
					Bundle data = msg.getData();
					String Msg = data.getString(MyConfig.TAG);
					Toast.makeText(SFCDisOfflinePick.this, Msg,
							Toast.LENGTH_SHORT).show();
					changeContainer = -1;
					break;
				}
				// ������
				String content = MyConnection.getMyConnection()
						.getCommitReturn();
				// ֱ����ת���¼ܵ����б����
				final MyDialogGood mydialog = new MyDialogGood(
						SFCDisOfflinePick.this);
				mydialog.hideCancle();// ����cancel��ť
				mydialog.setContent("�¼ܵ���" + undershelve_code + "\n" + content);
				mydialog.setDialogCallback(new Dialogcallback() {

					@Override
					public boolean exitActivity() {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public void btnConfirm() {
						opt_status = "4";
						Intent intent = new Intent(SFCDisOfflinePick.this,
								SFCDisOffline.class);
						startActivity(intent);
						SFCDisOfflinePick.this.finish();
					}

					@Override
					public void btnCancel() {
						mydialog.dismiss();
					}
				});
				mydialog.show();

				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDisOfflinePick.this, strMsg);
				break;
			case MyConfig.LACKPRO:
				et_scan_sku.setText("");
				// �ı���ﵥ�����״̬
				MyConnection.getMyConnection().updateProStateById(primaryID,
						"2");
				// �ٻ�
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				// �����ٻ����ص�����,�������ݿ����
				opdLackList = new ArrayList<OfflinePickDetail>();
				MyConnection.getMyConnection()
						.GetlackPro(userCode, opdLackList);
				System.out
						.println("----===_opdLackList==" + opdLackList.size());
				if (opdLackList.size() > 0) {
					for (int j = 0; j < opdLackList.size(); j++) {
						for (int i = 0; i < unPicklist.size(); i++) {
							if (Integer.parseInt(opdLackList.get(j)
									.getOpm_sortcode()) >= Integer
									.parseInt(unPicklist.get(i)
											.getOpm_sortcode())) {// �ŵ��м�
								isLast = false;
							}
						}
						if (isLast) {// �������
							// ��������� ��״̬��Ϊ3
							MyConnection.getMyConnection()
									.updateProStateByOpmId(
											"offline_pickdetail",
											opdLackList.get(j).getOpm_id(),
											"3",
											"0",
											opdLackList.get(j)
													.getShelve_loc_num());
						}
						isLast = true;
					}
				}
				selectShow();
				break;

			}
		};
	};

	/**
	 * 
	 * @param opmId
	 * @param realQtyʵ��ȡ������
	 */
	private void LackProCommit(String realQty, String productId, String wsCode) {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "op_code", "product_id", "ws_code",
								"real_qty" },
						new String[] { undershelve_code, productId, wsCode,
								realQty }, "pdaPickupCodeShortageManage"),
				handler);
	}

	/**
	 * �����ɺ��ύ����
	 */
	private void CommitAll() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "op_code", "op_status" },
						new String[] { undershelve_code, opt_status },
						"pdaUpdatePickupCodeStatus"), handler);

	}

}
