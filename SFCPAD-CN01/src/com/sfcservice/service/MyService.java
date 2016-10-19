package com.sfcservice.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.sfcservice.lock.LockActivity;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.config.MyConfig;

public class MyService extends Service {
	private Intent lockIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		System.out.println("---------------onstart------sfcservice");
		super.onStart(intent, startId);
		if (MyConfig.getMyConfig().getStop()) {
			System.out.println("------------------>�ϴ���ʼ");
			MyConfig.getMyConfig().setStop(false);
			MyConnection.getMyConnection().uploadingNewPro();
			return;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		lockIntent = new Intent(MyService.this, LockActivity.class);
		lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* ע��㲥 */
		IntentFilter mScreenOnFilter = new IntentFilter(
				"android.intent.action.SCREEN_ON");
		MyService.this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);

		/* ע��㲥 */
		IntentFilter mScreenOffFilter = new IntentFilter(
				"android.intent.action.SCREEN_OFF");
		MyService.this.registerReceiver(mScreenOffReceiver, mScreenOffFilter);
	}

	private KeyguardManager mKeyguardManager = null;
	private KeyguardManager.KeyguardLock mKeyguardLock = null;
	// ��Ļ�����Ĺ㲥,����Ҫ����Ĭ�ϵ���������
	private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
				// mKeyguardManager =
				// (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
				// mKeyguardLock = mKeyguardManager.newKeyguardLock("zdLock 1");
				// mKeyguardLock.disableKeyguard();
			}
		}

	};

	// ��Ļ�䰵/�����Ĺ㲥 �� ����Ҫ����KeyguardManager����Ӧ����ȥ�����Ļ����
	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.intent.action.SCREEN_OFF")
					|| action.equals("android.intent.action.SCREEN_ON")) {
				mKeyguardManager = (KeyguardManager) context
						.getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager.newKeyguardLock("SFC");
				mKeyguardLock.disableKeyguard();
				startActivity(lockIntent);
			}
		}

	};
}
