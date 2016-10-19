package com.sfcservice.pda.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;
import com.sfcservice.pda.offline.SFCDisOffline;

public class SFCDistribution extends Activity implements OnClickListener {
	private Button btnBack, btnOnline, btnOffline;
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
				MyTool.toastShow(SFCDistribution.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound();
				linePro.setVisibility(View.INVISIBLE);
				String[] strs = MyConnection.getMyConnection().getDisOld();

				if (strs == null) {
					MyConfig.getMyConfig().setGoOnPickup(false);// 初始化一票多件继续配货标记
					Intent intent = new Intent(SFCDistribution.this,
							SFCDisConfig.class);
					startActivity(intent);
					break;
				}
				if (strs[1].equals("0")) {
					Intent intent2 = new Intent(SFCDistribution.this,
							SFCDisOnLine.class);
					intent2.putExtra(MyConfig.TAG, strs);
					startActivity(intent2);
					break;
				}
				if (strs[1].equals("1")) {
					Intent intent2 = new Intent(SFCDistribution.this,
							SFCDisOnlineManyOneSKU.class);
					intent2.putExtra(MyConfig.TAG, strs);
					startActivity(intent2);
					break;
				}
				if (strs[1].equals("2")) {
					strs = MyConnection.getMyConnection().getMoreDisOld();
					if (strs == null) {
						MyTool.toastShow(SFCDistribution.this, "对不起，您没有选择配货分箱");
						break;
					}
					Intent intent2 = new Intent(SFCDistribution.this,
							SFCDisOnlineManyMoreSKU.class);
					intent2.putExtra(MyConfig.TAG, strs);
					startActivity(intent2);
					break;
				}
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCDistribution.this, strMsg);
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
		setContentView(R.layout.sfc_distribution);
		init();
	}

	public void init() {
		btnBack = (Button) findViewById(R.id.btn_back);
		btnOffline = (Button) findViewById(R.id.btnOffline);
		btnOnline = (Button) findViewById(R.id.btnOnline);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnBack.setOnClickListener(this);
		btnOffline.setOnClickListener(this);
		btnOnline.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btnOffline:
			Intent intent = new Intent(SFCDistribution.this,
					SFCDisOffline.class);
			startActivity(intent);
			break;
		// Intent intent1 = new Intent(this, SFCDisOffLine.class);
		// startActivity(intent1);
		// break;
		case R.id.btnOnline:
			getData();
			break;
		default:
			break;
		}
	}

	private void getData() {

		linePro.setVisibility(View.VISIBLE);
		linePro.requestFocus();
		linePro.setFocusable(true);
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_COMMON,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "checkFinished" }, new String[] { "1" },
						"pdaPickup"), handler);

	}
}
