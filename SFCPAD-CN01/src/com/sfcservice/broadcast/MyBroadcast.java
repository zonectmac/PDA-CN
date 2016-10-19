package com.sfcservice.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.config.MyConfig;

public class MyBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			MyConfig.getMyConfig().setStop(true);
			MyConnection.getMyConnection().initDB(context);
			// ֻҪ�ǿ��������Ͳ���Ҫ��SFCLogin�������߳�����������
			MyConfig.getMyConfig().setFirstInto(false);
			final Context c = context;
			new Thread() {
				public void run() {
					while (true) {
						Intent service = new Intent();
						service.setAction("com.sfcservice.ServiceTag");
						c.startService(service);
						try {
							Thread.sleep(MyConfig.BREAK_TIME);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}.start();
		}
		if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// ��ȡ��������״̬��NetWorkInfo����
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			// ��ȡ��ǰ�����������Ƿ����
			if (networkInfo == null) {
				MyConfig.getMyConfig().setNetGood(false);
				return;
			}
			if (!networkInfo.isAvailable()) {
				MyConfig.getMyConfig().setNetGood(false);
				return;
			}

			State state = connManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();

			if (State.CONNECTED == state) {
				MyConfig.getMyConfig().setNetGood(true);
				return;
			}

			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			if (State.CONNECTED == state) {
				MyConfig.getMyConfig().setNetGood(true);
				return;
			}
		}

	}
}
