package com.sfcservice.pda.config;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sfcservice.pda.R;

public class MyTool {
	private static SoundPool soundpool = null;
	private static int soundid = 0;
	private static Toast toast;
	private static SoundPool sfcSoundPool;
	private static int successed = 0, failed = 0, pass = 0, successed1 = 0;

	public static void loadSound() {
		soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100);
		soundid = soundpool.load("/etc/Scan_new.ogg", 1);
	}

	public static void loadSFCSound(Context context) {
		sfcSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		successed = sfcSoundPool.load(context, R.raw.s, 1);
		successed1 = sfcSoundPool.load(context, R.raw.s1, 1);
		failed = sfcSoundPool.load(context, R.raw.f, 1);
		pass = sfcSoundPool.load(context, R.raw.p, 1);
	}

	public static void playSound() {
		try {
			soundpool.play(soundid, 1, 1, 0, 0, 1);
		} catch (Exception ex) {
			System.out.println("Sound Exception Message-->" + ex.getMessage());
		}
	}

	public static void playSuccessSound() {
		try {

			sfcSoundPool.play(successed, 1, 1, 0, 0, 1);
		} catch (Exception ex) {
			System.out.println("Sound Exception Message-->" + ex.getMessage());
		}
	}

	public static void playSuccessSound1() {
		try {
			sfcSoundPool.play(successed1, 1, 1, 0, 0, 1);
		} catch (Exception ex) {
			System.out.println("Sound Exception Message-->" + ex.getMessage());
		}
	}

	public static void playFailedSound() {
		try {
			sfcSoundPool.play(failed, 1, 1, 0, 0, 1);
		} catch (Exception ex) {
			System.out.println("Sound Exception Message-->" + ex.getMessage());
		}
	}

	public static void playPassSound() {
		try {
			sfcSoundPool.play(pass, 1, 1, 0, 0, 1);
		} catch (Exception ex) {
			System.out.println("Sound Exception Message-->" + ex.getMessage());
		}
	}

	/**
	 * 隐藏输入法
	 */
	public static void hideInputKeyBroad(Activity activity) {
		if (activity.getCurrentFocus() == null) {
			return;
		}
		if (activity.getCurrentFocus().getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	/**
	 * 显示与隐藏toast
	 * 
	 * @param context
	 * @param msg
	 */
	public final static void toastShow(Context context, String msg) {
		if (toast != null) {
			// toast.cancel();
			toast.setText(msg);
		} else {
			toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		}
		toast.show();

	}

	/**
	 * 检测网络
	 */
	public final static boolean internetAccessful(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public final static String getTime() {
		Calendar c = Calendar.getInstance();
		String date = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1)
				+ "-" + c.get(Calendar.DAY_OF_MONTH) + " "
				+ c.get(Calendar.HOUR_OF_DAY) + ":"
				+ String.format("%02d", c.get(Calendar.MINUTE)) + ":"
				+ String.format("%02d", c.get(Calendar.SECOND));
		return date;
	}

	/**
	 * 判断是否创建了快捷方式
	 * 
	 * @param cx
	 * @return
	 */
	public static boolean hasShortcut(Context cx) {
		// 需要添加权限
		// <uses-permission
		// android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
		// <uses-permission
		// android:name="com.android.launcher.permission.READ_SETTINGS" />
		boolean result = false;
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(cx.getPackageName(),
							PackageManager.GET_META_DATA)).toString();

			System.out.print(title);
		} catch (Exception e) {
		}
		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);
		final Cursor c = cx.getContentResolver().query(CONTENT_URI, null,
				"title=?", new String[] { title }, null);
		if (c != null && c.getCount() > 0) {
			result = true;
		}
		c.close();
		return result;
	}

	/**
	 * 获取系统时间格式2013-1-1 09：00
	 */
	public static String getSFCTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 5);
		int a1 = calendar.get(Calendar.YEAR);
		int a2 = calendar.get(Calendar.MONTH) + 1;
		int a3 = calendar.get(Calendar.DAY_OF_MONTH);
		int a4 = calendar.get(Calendar.HOUR_OF_DAY);
		int a5 = calendar.get(Calendar.MINUTE);

		String str2 = String.format("%02d", a2);
		String str3 = String.format("%02d", a3);
		String str4 = String.format("%02d", a4);
		String str5 = String.format("%02d", a5);

		return (a1 + "-" + str2 + "-" + str3 + " " + str4 + ":" + str5);
	}

	/**
	 * 获取系统时间并且加5小时
	 */
	public static String getSFCTimeAddFive() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 5);

		int a1 = calendar.get(Calendar.YEAR);
		int a2 = calendar.get(Calendar.MONTH) + 1;
		int a3 = calendar.get(Calendar.DAY_OF_MONTH);
		int a4 = calendar.get(Calendar.HOUR_OF_DAY);
		int a5 = calendar.get(Calendar.MINUTE);

		String str2 = String.format("%02d", a2);
		String str3 = String.format("%02d", a3);
		String str4 = String.format("%02d", a4);
		String str5 = String.format("%02d", a5);

		return (a1 + "-" + str2 + "-" + str3 + " " + str4 + ":" + str5);
	}
}
