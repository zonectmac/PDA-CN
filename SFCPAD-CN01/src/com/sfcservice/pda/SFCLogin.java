package com.sfcservice.pda;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCLogin extends Activity implements OnClickListener,
		OnFocusChangeListener, OnEditorActionListener {
	private EditText etUser, etPassword;
	private LinearLayout linePro;
	private FrameLayout lineUpdate;
	private LinearLayout lineProUpdate;
	private LinearLayout lineLoadingAPK;
	private TextView tvLoadingAPK;
	private TextView tvContent;
	private TextView tvShow;
	private Button btnLogin;
	private Button btnConfirm;
	private Button btnCancel;
	private String[] info;
	private String title;
	private long count = 0;
	private long length = 0;
	// 解决PDA自带的软键盘消失BUG
	private boolean down = false;
	private String userCode, password;
	private int STATE = -1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int m = msg.what;
			switch (m) {
			case MyConfig.ACCESSS:
				tvShow.setText("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				lineUpdate.setVisibility(View.INVISIBLE);
				MyTool.toastShow(SFCLogin.this, "连接服务器失败");
				if (STATE == 0) {
					finish();
				}
				break;
			case MyConfig.RESULTS:
				MyTool.playSuccessSound1();
				// 存储数据
				linePro.setVisibility(View.INVISIBLE);
				MyConnection.getMyConnection().insertUser(
						etUser.getText().toString());

				Intent intent = new Intent(SFCLogin.this, SFCPDAActivity.class);
				startActivity(intent);
				finish();
				break;
			case MyConfig.RESULTF:
				MyTool.playFailedSound();
				linePro.setVisibility(View.INVISIBLE);
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCLogin.this, strMsg);
				break;
			case 30:
				linePro.setVisibility(View.INVISIBLE);
				Bundle bundle = msg.getData();
				info = bundle.getStringArray(MyConfig.TAG);
				int code = Integer.parseInt(info[1]);
				if (code > getVerCode(SFCLogin.this)) {
					lineProUpdate.setVisibility(View.VISIBLE);
					tvContent.setText("当前版本 : " + getVerName(SFCLogin.this)
							+ " Code: " + getVerCode(SFCLogin.this)
							+ "; 发现新版本: " + info[0] + " Code: " + code);
					etUser.setFocusable(false);
					etUser.setEnabled(false);
					etPassword.setFocusable(false);
					etPassword.setEnabled(false);
					btnLogin.setFocusable(false);
					btnLogin.setEnabled(false);

				} else {
					lineUpdate.setVisibility(View.INVISIBLE);
				}
				break;
			case 40:
				int a = (int) (((double) count / length) * 100);
				tvLoadingAPK.setText(a + "");
				break;
			case 41:
				tvLoadingAPK.setText("100%");
				break;
			case 42:
				lineUpdate.setVisibility(View.INVISIBLE);
				install();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_login);
		init();
	}

	/**
	 * 初始化
	 */
	public void init() {
		MyTool.loadSound();
		MyTool.loadSFCSound(this);
		MyConnection.getMyConnection().initDB(this);
		MyConfig.getMyConfig().setWidth(
				getWindowManager().getDefaultDisplay().getWidth());
		MyConfig.getMyConfig().setHeight(
				getWindowManager().getDefaultDisplay().getHeight());

		lineUpdate = (FrameLayout) findViewById(R.id.line_update);
		lineProUpdate = (LinearLayout) findViewById(R.id.line_pro_update);
		tvContent = (TextView) findViewById(R.id.tvContent);

		etUser = (EditText) findViewById(R.id.et_user);
		etPassword = (EditText) findViewById(R.id.et_password);
		linePro = (LinearLayout) findViewById(R.id.line_pro);
		tvShow = (TextView) findViewById(R.id.tv_show);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		lineLoadingAPK = (LinearLayout) findViewById(R.id.line_loading_apk);
		tvLoadingAPK = (TextView) findViewById(R.id.tv_loading_apk);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		btnLogin.setOnFocusChangeListener(this);
		etPassword.setOnFocusChangeListener(this);
		etPassword.setOnEditorActionListener(this);
		// 创建快捷方式
		String sdkVersion = android.os.Build.VERSION.SDK;
		if (Integer.parseInt(sdkVersion) < 11) {
			if (!MyTool.hasShortcut(this)) {
				createShorcut();
			}
		}

		// 定期清除数据
		MyConnection.getMyConnection().clearData();

		// 刚刚安装好程序后，并没有跳转至开机启动时的每5秒启动一次服务；所以必须在此设置
		if (MyTool.internetAccessful(this)) {
			MyConfig.getMyConfig().setNetGood(true);
		}
		if (MyConfig.getMyConfig().getFirstInto()) {
			MyConfig.getMyConfig().setStop(true);
			MyConfig.getMyConfig().setFirstInto(false);
			new Thread() {
				public void run() {
					while (true) {
						Intent service = new Intent();
						service.setAction("com.sfcservice.ServiceTag");
						SFCLogin.this.startService(service);
						try {
							Thread.sleep(MyConfig.BREAK_TIME);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}.start();
		}

		STATE = 0;
		linePro.setVisibility(View.VISIBLE);
		linePro.setFocusable(true);
		linePro.requestFocus();
		tvShow.setText("正在检查更新...");
		MyConnection.getMyConnection().update(MyConfig.URL_UPDATE, handler);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == 0) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(etPassword.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			etPassword.clearFocus();
			btnLogin.requestFocus();
			btnLogin.setFocusable(true);
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			down = true;
		} else {
			down = false;
		}
		if (keyCode == 113) {
			etPassword.setText("");
			etUser.setText("");
			etUser.requestFocus();
			etUser.setFocusable(true);
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (lineUpdate.getVisibility() == View.VISIBLE) {
				if (tvContent.getText().toString().equals("")) {
					MyTool.toastShow(this, "正在检测新版本,请稍候...");
				} else {
					MyTool.toastShow(this, "正在升级程序,请稍候...");
				}
				return true;
			}
			if (linePro.getVisibility() == View.VISIBLE) {
				MyTool.toastShow(this, "正在检测新版本,请稍候...");
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirm:
			lineProUpdate.setVisibility(View.INVISIBLE);
			lineLoadingAPK.setVisibility(View.VISIBLE);
			downFile();
			return;
		case R.id.btnCancel:
			lineUpdate.setVisibility(View.INVISIBLE);
			return;
		default:
			break;
		}

		btnClick();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.et_password:
			if (!hasFocus && down) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(etPassword.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			break;

		case R.id.btn_login:
			if (v.isInTouchMode() && hasFocus) {
				down = false;
				btnClick();
			}
			break;
		default:
			break;
		}
	}

	public void btnClick() {
		// 基本判断+获取数据
		userCode = etUser.getText().toString();
		password = etPassword.getText().toString();
		// 实例化SharedPreferences对象
		SharedPreferences mySharedPreferences = getSharedPreferences("test",
				Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("user", userCode);
		// 提交当前数据
		editor.commit();
		if (!(userCode.equals("") || password.equals(""))) {
			getData();
		} else {
			MyTool.toastShow(this, "用户名和密码不能为空");
			MyTool.playFailedSound();
		}

	}

	/**
	 * 获得tokken和key并存储至本地数据库
	 */
	public void getData() {
		STATE = -1;
		linePro.setVisibility(View.VISIBLE);
		linePro.setFocusable(true);
		linePro.setFocusableInTouchMode(true);
		linePro.requestFocus();
		tvShow.setText("正在连接...");
		MyConnection myConnection = MyConnection.getMyConnection();

		myConnection.acceptServer(MyConfig.URL_LOGIN,
				myConnection.writeUserJosnObject(userCode, password), handler);

	}

	public void createShorcut() {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setClassName(this, getClass().getName());
		intent.addCategory("android.intent.category.LAUNCHER");
		// 这里添加2个flag 可以 消除 在按home 键时，再点快捷方式重启程序的bug
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 268435456
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 2097152

		try {
			final PackageManager pm = this.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(this.getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
			title = "SFCPDA";
		}
		Intent intent1 = new Intent();
		intent1.putExtra("android.intent.extra.shortcut.INTENT", intent);
		intent1.putExtra("android.intent.extra.shortcut.NAME", title);
		Intent.ShortcutIconResource localShortcutIconResource = Intent.ShortcutIconResource
				.fromContext(this.getApplicationContext(), R.drawable.sfc_icon);
		intent1.putExtra("android.intent.extra.shortcut.ICON_RESOURCE",
				localShortcutIconResource);
		intent1.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(intent1);
	}

	private int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.sfcservice.pda", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	private String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.sfcservice.pda", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;

	}

	private void install() {
		File file = new File(getFilesDir(), "SFCPDA.apk");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	private void downFile() {
		STATE = -1;
		count = 0;
		new Thread() {
			public void run() {
				try {
					URL url = new URL(info[2]);
					HttpURLConnection httpUrlConnection = (HttpURLConnection) url
							.openConnection();
					httpUrlConnection.setConnectTimeout(MyConfig.TIME_OUT);
					httpUrlConnection.setReadTimeout(MyConfig.TIME_OUT);

					length = httpUrlConnection.getContentLength();
					InputStream is = httpUrlConnection.getInputStream();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						fileOutputStream = openFileOutput("SFCPDA.apk",
								MODE_WORLD_READABLE);
						byte buf[] = new byte[1024];
						while (true) {
							int numread = is.read(buf);
							count += numread;
							if (numread <= 0) {
								// 完成
								Message msg = new Message();
								msg.what = 41;
								handler.sendMessage(msg);
								break;
							}
							fileOutputStream.write(buf, 0, numread);
							Message msg = new Message();
							msg.what = 40;
							handler.sendMessage(msg);
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					is.close();
					httpUrlConnection.disconnect();
					Message msg = new Message();
					msg.what = 42;
					handler.sendMessage(msg);
				} catch (Exception e) {
					handler.sendEmptyMessage(MyConfig.ACCESSF);
				}
			}

		}.start();
	}
}
