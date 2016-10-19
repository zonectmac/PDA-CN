package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfcservice.bean.CheckBean;
import com.sfcservice.component.MyDialogGood;
import com.sfcservice.component.MyDialogGood.Dialogcallback;
import com.sfcservice.img.Img;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.SFCPDAActivity;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCCheckAllSKU extends Activity implements OnClickListener {
	private TextView tvExplain, tvSKU, tvBoxNum, tvStatus;
	private ImageView imgInfo;
	private EditText etCount;
	private Button btnNext, btnStart, btnBack, btnCancel, btnPre, btnCheckList;
	private LinearLayout linePro;
	private TextView tvShow;
	private boolean imgBool;
	/**
	 * 0��ʼ�̵㣻1ȷ��������2��ȡ��һ��δ����Ļ��ܣ�3ȡ����4��һ��;5����̵������б�; 6��һ��;7�̵��ǲ��ǻ���δ����Ļ���
	 */
	private int STATE = -1;
	private int currentItem = 0;
	private MyDialogGood dialog;
	private String shelfNum;
	private ArrayList<CheckBean> listBean;
	/**
	 * �ж��ǲ��Ǵ�PDA�����������Ҳ������δ��ɵ��̵㵥
	 */
	private boolean pdaCome = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("���ڼ������...");
				break;
			case MyConfig.ACCESSF:
				if (STATE == -1) {
					break;
				}
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCCheckAllSKU.this, "���ӷ�����ʧ��");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 0) {
					etCount.setEnabled(true);
					etCount.setFocusableInTouchMode(true);
					etCount.setFocusable(true);
					etCount.requestFocus();
					btnStart.setText("ȷ������");
					btnNext.setVisibility(View.GONE);
					btnPre.setVisibility(View.GONE);
					btnCancel.setVisibility(View.VISIBLE);
					btnCheckList.setVisibility(View.GONE);
					break;
				}
				if (STATE == 1) {
					listBean.get(currentItem).setUsable("0");
					listBean.get(currentItem).setStatus("���̵�");
					next();
					break;
				}
				if (STATE == 2) {
					listBean = new ArrayList<CheckBean>();
					MyConnection.getMyConnection().getSKUInfo(listBean);
					MyConfig.getMyConfig().setListBean(listBean);

					currentItem = 0;
					showBtn(listBean.size());
					showInfo(0);
					break;
				}
				if (STATE == 3) {
					btnStart.setText("��ʼ�̵�");
					btnNext.setVisibility(View.VISIBLE);
					btnCancel.setVisibility(View.INVISIBLE);
					btnCheckList.setVisibility(View.GONE);
					etCount.setEnabled(false);
					etCount.setText("");
					etCount.setFocusable(false);
					btnStart.setFocusable(true);
					btnStart.requestFocus();

					if (currentItem == 0) {
						btnPre.setVisibility(View.GONE);
					} else {
						btnPre.setVisibility(View.VISIBLE);
					}
					break;
				}
				if (STATE == 4) {

					break;
				}
				if (STATE == 5) {
					listBean.get(currentItem).setStatus("��������̵������б�");
					next();
					break;
				}
				if (STATE == 6) {
					break;
				}
				if (STATE == 7) {
					// ��δ��ɵ��̵㵥
					Intent intent = new Intent(SFCCheckAllSKU.this,
							SFCPDAActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					break;
				}
				break;
			case MyConfig.RESULTF:
				linePro.setVisibility(View.INVISIBLE);
				if (STATE == 2) {
					Intent intent = new Intent(SFCCheckAllSKU.this,
							SFCCheckAll.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}
				if (STATE == 7) {
					// û��δ��ɵ��̵㵥
					Intent intent = new Intent(SFCCheckAllSKU.this,
							SFCCheckAll.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					break;
				}
				MyTool.playFailedSound();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCCheckAllSKU.this, strMsg);
				break;
			case 10:
				imgInfo.setImageBitmap(MyConnection.getMyConnection()
						.getBitmap());
				imgBool = true;
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
		setContentView(R.layout.sfc_check_all_sku);
		init();
	}

	public void init() {
		tvExplain = (TextView) findViewById(R.id.tvExplain);
		tvSKU = (TextView) findViewById(R.id.tvSKU);
		tvBoxNum = (TextView) findViewById(R.id.tvBoxNum);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		imgInfo = (ImageView) findViewById(R.id.imgInfo);
		etCount = (EditText) findViewById(R.id.etCount);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnStart = (Button) findViewById(R.id.btnStartCheck);
		tvShow = (TextView) findViewById(R.id.tv_show);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnPre = (Button) findViewById(R.id.btnPre);
		btnCheckList = (Button) findViewById(R.id.btnCheckList);
		imgInfo.setDrawingCacheEnabled(true);

		btnNext.setOnClickListener(this);
		btnPre.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		imgInfo.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnCheckList.setOnClickListener(this);
		etCount.setFocusable(false);
		etCount.setEnabled(false);

		pdaCome = getIntent().getBooleanExtra("PDA", false);
		shelfNum = getIntent().getStringExtra(MyConfig.TAG);
		listBean = MyConfig.getMyConfig().getListBean();
		tvExplain.setText("��λ�� : " + shelfNum + " ; �� 1 ���̵���Ϣ ( �� "
				+ listBean.size() + " �� )");

		showBtn(listBean.size());
		showInfo(0);
		currentItem = 0;

		dialog = new MyDialogGood(this);
		dialog.setContent("�̵��˳����´ν�������δ������б�����������λ�ţ���һ���Ǵ˴�����Ļ�λ���Ƿ�����˳�");
		dialog.setConfirmText("�˳�");
		dialog.setDialogCallback(new Dialogcallback() {

			@Override
			public boolean exitActivity() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void btnConfirm() {
				// TODO Auto-generated method stub
				boolean bool = false;
				//���������������ֱ���˳�
				//�����ʼ�̵�����ȷ����������ֱ���˳�
				bool = btnStart.getText().toString().equals("ȷ������");
				if (bool) {
					Intent intent = new Intent(SFCCheckAllSKU.this,
							SFCPDAActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					return;
				}
				//û�п�ʼ�̵㵫�Ǵ�ɨ����ܽ����Ŀ���ֱ���˳�
				if (!bool && !pdaCome) {
					Intent intent = new Intent(SFCCheckAllSKU.this,
							SFCCheckAll.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					return;
				}
				STATE = 7;
				getData(2);
			}

			@Override
			public void btnCancel() {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnNext:
			next();
			break;
		case R.id.btnStartCheck:
			// �����ʼ�̵�
			if (btnStart.getText().toString().equals("��ʼ�̵�")) {
				STATE = 0;
				getData(STATE);
				break;
			}
			// ���ȷ������
			if (btnStart.getText().toString().equals("ȷ������")) {
				String str = etCount.getText().toString();
				if (str.equals("")) {
					MyTool.toastShow(SFCCheckAllSKU.this, "�������̵�����");
					break;
				}
				int i = 0;
				try {
					i = Integer.parseInt(str);
				} catch (Exception e) {
					// TODO: handle exception
					MyTool.toastShow(SFCCheckAllSKU.this, "�̵�������ʽ����ȷ,����������");
					break;
				}
				if (i < 0) {
					MyTool.toastShow(SFCCheckAllSKU.this, "�̵�����������ڵ���0");
					break;
				}
				// �ύ����
				STATE = 1;
				getData(1);
				break;
			}
			if (btnStart.getText().toString().equals("������λ")) {
				goBack();
				break;
			}
			break;
		case R.id.imgInfo:
			if (imgBool) {
				MyConfig.getMyConfig().setBitmap(imgInfo.getDrawingCache());
				Intent intent = new Intent(SFCCheckAllSKU.this, Img.class);
				startActivity(intent);
			} else {
				MyTool.toastShow(this, "ͼƬ����ʧ��...");
			}
			break;
		case R.id.btn_back:
			dialog.show();
			break;
		case R.id.btn_cancel:
			STATE = 3;
			getData(3);
			break;
		case R.id.btnPre:
			currentItem--;
			showCurrentInfo(currentItem, listBean.get(currentItem));
			break;
		case R.id.btnCheckList:
			STATE = 5;
			getData(5);
			break;
		default:
			break;
		}
	}

	public void showBtn(int listSize) {
		if (listSize == 1) {
			// �����̵�
			if (listBean.get(0).getUsable().equals("0")) {
				// �����̵�
				btnStart.setVisibility(View.VISIBLE);
				btnStart.setText("������λ");
				etCount.setEnabled(false);
				etCount.setFocusable(false);
				btnNext.setVisibility(View.GONE);
				btnPre.setVisibility(View.GONE);
				btnCancel.setVisibility(View.INVISIBLE);
				etCount.setText("");
				etCount.setHint("��ʱ�����̵�...");
				if (listBean.get(0).getStatus().endsWith("����")) {
					btnCheckList.setVisibility(View.VISIBLE);
				} else {
					btnCheckList.setVisibility(View.GONE);
				}
			} else {
				// �����̵�,���ǲ���Ҫ�����ʼ�̵�
				if (listBean.get(0).getStart().equals("0")) {
					btnStart.setText("ȷ������");
					etCount.setEnabled(true);
					etCount.setFocusable(true);
					etCount.setFocusableInTouchMode(true);
					etCount.requestFocus();
					btnStart.setVisibility(View.VISIBLE);
					btnNext.setVisibility(View.GONE);
					if (listBean.get(currentItem).getPda().equals("0")) {
						btnCancel.setVisibility(View.INVISIBLE);
					} else {
						btnCancel.setVisibility(View.VISIBLE);
					}
				} else {
					btnStart.setText("��ʼ�̵�");
					etCount.setEnabled(false);
					etCount.setFocusable(false);
					btnStart.setVisibility(View.VISIBLE);
					btnNext.setVisibility(View.VISIBLE);
					btnCancel.setVisibility(View.INVISIBLE);
				}
			}
			return;
		}
		// �ж��Ƿ���̵�
		if (listBean.get(currentItem).getUsable().equals("0")) {
			// �����̵�
			btnStart.setVisibility(View.GONE);
			btnNext.setVisibility(View.VISIBLE);
			btnCancel.setVisibility(View.INVISIBLE);
			etCount.setEnabled(false);
			etCount.setFocusable(false);
			etCount.setText("");
			etCount.setHint("��ʱ�����̵�...");
			if (listBean.get(currentItem).getStatus().equals("����")) {
				btnCheckList.setVisibility(View.VISIBLE);
			} else {
				btnCheckList.setVisibility(View.GONE);
			}
			if (currentItem == 0) {
				btnPre.setVisibility(View.GONE);
			} else {
				btnPre.setVisibility(View.VISIBLE);
			}
		} else {
			// �����̵�
			etCount.setText("");
			etCount.setHint("�������̵�����");
			btnCheckList.setVisibility(View.GONE);
			if (listBean.get(currentItem).getStart().equals("0")) {
				btnStart.setText("ȷ������");
				etCount.setEnabled(true);
				etCount.setFocusable(true);
				etCount.setFocusableInTouchMode(true);
				etCount.requestFocus();
				btnStart.setVisibility(View.VISIBLE);
				btnPre.setVisibility(View.GONE);
				btnNext.setVisibility(View.GONE);
				if (listBean.get(currentItem).getPda().equals("0")) {
					btnCancel.setVisibility(View.INVISIBLE);
				} else {
					btnCancel.setVisibility(View.VISIBLE);
				}
			} else {
				btnStart.setText("��ʼ�̵�");
				etCount.setFocusable(false);
				etCount.setEnabled(false);
				btnStart.setVisibility(View.VISIBLE);
				btnNext.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.INVISIBLE);
				if (currentItem == 0) {
					btnPre.setVisibility(View.GONE);
				} else {
					btnPre.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public void next() {
		// �����һ��
		currentItem++;
		if ((currentItem) == listBean.size()) {
			goBack();
			return;
		}
		showCurrentInfo(currentItem, listBean.get(currentItem));
	}

	public void showInfo(int index) {
		tvSKU.setText(listBean.get(index).getProductSku());
		tvBoxNum.setText(listBean.get(index).getContainerCode());
		tvStatus.setText(listBean.get(index).getStatus());
		tvExplain.setText("��λ�� : " + shelfNum.toUpperCase() + " ; �� "
				+ (index + 1) + " ���̵���Ϣ ( �� " + listBean.size() + " �� )");
		etCount.setText("");
		imgBool = false;
		STATE = -1;
		MyConnection.getMyConnection().getImg(
				MyConfig.URL_PRE + listBean.get(index).getPic(), handler);
	}

	public void showCurrentInfo(int index, CheckBean bean) {
		// �ж��Ƿ���̵�
		if (bean.getUsable().equals("0")) {
			// �����̵�
			btnStart.setVisibility(View.GONE);
			btnNext.setVisibility(View.VISIBLE);
			btnCancel.setVisibility(View.INVISIBLE);
			etCount.setEnabled(false);
			etCount.setFocusable(false);
			etCount.setText("");
			etCount.setHint("��ʱ�����̵�...");
			if (bean.getStatus().equals("����")) {
				btnCheckList.setVisibility(View.VISIBLE);
			} else {
				btnCheckList.setVisibility(View.GONE);
			}
			if (currentItem == 0) {
				btnPre.setVisibility(View.GONE);
			} else {
				btnPre.setVisibility(View.VISIBLE);
			}
		} else {
			// �����̵�
			etCount.setText("");
			etCount.setHint("�������̵�����");
			btnCheckList.setVisibility(View.GONE);
			if (bean.getStart().equals("0")) {
				btnStart.setText("ȷ������");
				etCount.setEnabled(true);
				etCount.setFocusable(true);
				etCount.setFocusableInTouchMode(true);
				etCount.requestFocus();
				btnStart.setVisibility(View.VISIBLE);
				btnPre.setVisibility(View.GONE);
				btnNext.setVisibility(View.GONE);
				if (listBean.get(currentItem).getPda().equals("0")) {
					btnCancel.setVisibility(View.INVISIBLE);
				} else {
					btnCancel.setVisibility(View.VISIBLE);
				}
			} else {
				btnStart.setText("��ʼ�̵�");
				etCount.setFocusable(false);
				etCount.setEnabled(false);
				btnStart.setVisibility(View.VISIBLE);
				btnNext.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.INVISIBLE);
				if (currentItem == 0) {
					btnPre.setVisibility(View.GONE);
				} else {
					btnPre.setVisibility(View.VISIBLE);
				}
			}
		}

		tvSKU.setText(bean.getProductSku());
		tvBoxNum.setText(bean.getContainerCode());
		tvStatus.setText(bean.getStatus());
		tvExplain.setText("��λ�� : " + shelfNum.toUpperCase() + " ; �� "
				+ (index + 1) + " ���̵���Ϣ ( �� " + listBean.size() + " �� )");
		etCount.setText("");
		imgBool = false;
		STATE = -1;
		MyConnection.getMyConnection().getImg(MyConfig.URL_PRE + bean.getPic(),
				handler);
	}

	public void goBack() {
		if (pdaCome) {
			// ��ȡ����
			STATE = 2;
			getData(2);
			return;

		}
		Intent intent = new Intent(SFCCheckAllSKU.this, SFCCheckAll.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void getData(int state) {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);

		switch (state) {
		case 0:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection()
							.writeJsonWithUserInfo(
									new String[] { "ws_code", "sku" },
									new String[] {
											shelfNum,
											listBean.get(currentItem)
													.getProductSku() },
									"startStock"), handler);
			break;
		case 1:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "ws_code", "sku", "qty" },
							new String[] { shelfNum,
									listBean.get(currentItem).getProductSku(),
									etCount.getText().toString() }, "stock"),
					handler);
			break;
		case 2:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] {}, null, "getSkuByLastWs"), handler);
			break;
		case 3:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection()
							.writeJsonWithUserInfo(
									new String[] { "ws_code", "sku" },
									new String[] {
											shelfNum,
											listBean.get(currentItem)
													.getProductSku(), },
									"cancelStock"), handler);
			break;
		case 4:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "ws_code", "sku" },
							new String[] {
									shelfNum,
									listBean.get(currentItem - 1)
											.getProductSku(), }, "previous"),
					handler);
			break;
		case 5:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection()
							.writeJsonWithUserInfo(
									new String[] { "ws_code", "sku" },
									new String[] {
											shelfNum,
											listBean.get(currentItem)
													.getProductSku(), },
									"createStocktake"), handler);
			break;
		case 6:
			MyConnection.getMyConnection().acceptServer(
					MyConfig.URL_CHECK,
					MyConnection.getMyConnection().writeJsonWithUserInfo(
							new String[] { "ws_code", "sku" },
							new String[] {
									shelfNum,
									listBean.get(currentItem + 1)
											.getProductSku(), }, "previous"),
					handler);
			break;
		default:
			break;
		}
	}
}
