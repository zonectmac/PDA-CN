package com.sfcservice.pda.offline;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class BindContainer extends Activity implements OnClickListener,
		OnEditorActionListener {

	private EditText et_opt_type, et_undershelve, et_container;
	private String single, opt_type, more, warehouse_id, userCode;
	private LinearLayout linePro;
	private TextView tvShow;
	private Button btn_picking;
	private String undershelve_code;// 下架单号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_bindcontainer);
		// 同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
		SharedPreferences sp = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		single = sp.getString("single", "");
		more = sp.getString("more", "");
		opt_type = sp.getString("opt_type", "");
		warehouse_id = sp.getString("warehouse_id", "");
		userCode = sp.getString("user", "");
		if (opt_type.equals("1")) {
			undershelve_code = more;
		} else {
			undershelve_code = single;
		}
		initView();
	}

	private void initView() {
		et_opt_type = (EditText) findViewById(R.id.et_opt_type);
		et_undershelve = (EditText) findViewById(R.id.et_undershelve);
		et_container = (EditText) findViewById(R.id.et_container);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		findViewById(R.id.btn_back).setOnClickListener(this);
		btn_picking = (Button) findViewById(R.id.btn_picking);
		btn_picking.setOnClickListener(this);
		et_container.setOnEditorActionListener(this);
		tvShow = (TextView) findViewById(R.id.tv_show);
		if (opt_type.equals("1")) {
			et_opt_type.setText("一票多件");
			et_undershelve.setText(more);
		} else {
			et_opt_type.setText("一票一件");
			et_undershelve.setText(single);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				MyTool.toastShow(BindContainer.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				Intent intent = new Intent(BindContainer.this,
						SFCDisOfflinePick.class);
				if (!et_container.getText().toString().equals("")) {
					System.out.println("==et=="
							+ et_container.getText().toString());
					// 解析离线配货单货品信息并插入到数据库
					MyConnection.getMyConnection().insertPickDetail(userCode);
					startActivity(intent);
					BindContainer.this.finish();
				} else {
					Toast.makeText(BindContainer.this, "请输入中转箱号！",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(BindContainer.this, strMsg);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_picking:
			if (!et_container.getText().toString().equals("")) {
				int count = MyConnection.getMyConnection().QueryopCode(
						"offline_pickdetail", undershelve_code, userCode);
				System.out.println("===duan====" + count);
				if (count != 0) {// 断电情况
					Intent intent = new Intent(BindContainer.this,
							SFCDisOfflinePick.class);
					System.out.println("==et=="
							+ et_container.getText().toString());
					startActivity(intent);
					BindContainer.this.finish();
				} else {
					// 提交数据
					getOfflineDatas();
				}
			} else {
				Toast.makeText(BindContainer.this, "请输入中转箱号！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}

	}

	private void getOfflineDatas() {
		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection()
						.writeJsonWithUserInfo(
								new String[] { "op_code", "container",
										"warehouse_id" },
								new String[] {
										et_undershelve.getText().toString(),
										et_container.getText().toString(),
										warehouse_id },
								"pdaGetPickupCodeDetail"), handler);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		btn_picking.requestFocus();
		btn_picking.setFocusable(true);
		return false;
	}
}
