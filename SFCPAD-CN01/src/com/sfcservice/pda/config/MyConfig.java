package com.sfcservice.pda.config;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.sfcservice.bean.CheckBean;
import com.sfcservice.bean.DisBean;
import com.sfcservice.pda.R;

public class MyConfig {
	public static final int ACCESSS = 1;
	public static final int ACCESSF = 2;
	public static final int RESULTF = 3;
	public static final int RESULTS = 4;
	public static final int LACKPRO = 110;
	public static final int BREAK_TIME = 25000;
	public static final int TIME_OUT = 35000;
	public static final int IMG_TIME_OUT = 10000;
	public static final int DB_LOADING = 15;
	public static final int DELETE_DAY = 20;
	public static final int LOADING_ITEM = 5;
	public static final int LOCKTIME = 1000 * 60 * 10;
	public static final int MAX_ITEM = 10;
	public static final String TAG = "SFC";

	// public static final String URL_LOGIN =
	// "http://190.168.1.19/default/svc/author/params/";
	// public static final String URL_COMMON =
	// "http://190.168.1.19/default/svc/svc";
	// public static final String URL_CHECK =
	// "http://190.168.1.19/default/svc/check";
	// public static final String URL_PRE = "http://190.168.1.19";
	// public static final String URL_UPDATE =
	// "http://190.168.1.27/moblieapp/android/version.xml";
	// cfftest2
	// chinafulfill
	// public static final String URL_PRE = "http://admin.chinafulfill.com";
	public static final String URL_PRE = "http://190.168.1.19";
	public static final String URL_LOGIN = URL_PRE
			+ "/default/svc/author/params/";
	public static final String URL_COMMON = URL_PRE + "/default/svc/svc";
	public static final String URL_CHECK = URL_PRE + "/default/svc/check";

	public static final String URL_UPDATE = URL_PRE
			+ "/moblieapp/android/version-dg.xml";

	// public static final String URL_LOGIN =
	// "http://192.168.1.27/default/svc/author/params/";
	// public static final String URL_DETECTING =
	// "http://192.168.1.27/default/svc/svc";
	// public static final String URL_COMMON =
	// "http://192.168.1.27/default/svc/svc";
	// public static final String URL_CHECK =
	// "http://192.168.1.27/default/svc/check";
	// public static final String URL_PRE = "http://192.168.1.27";
	// public static final String URL_UPDATE =
	// "http://admin.chinafulfill.com/moblieapp/android/version.xml";

	// public static final String URL_LOGIN =
	// "http://192.168.1.13/default/svc/author/params/";
	// public static final String URL_DETECTING =
	// "http://192.168.1.13/default/svc/svc";
	// public static final String URL_COMMON =
	// "http://192.168.1.13/default/svc/svc";
	// public static final String URL_CHECK =
	// "http://192.168.1.13/default/svc/check";
	// public static final String URL_PRE = "http://192.168.1.13";
	// public static final String URL_UPDATE =
	// "http://admin.chinafulfill.com/moblieapp/android/version.xml";

	public static final String ACTION = "urovo.rcv.message";
	public static final String[] SFCHomeItemText = { "检测货架", "查询货位", "截单返架",
			"产品上架", "中转箱转移", "容器绑定", "盘点", "配货", "特殊分箱", "容器下架", "库存调拨" };
	public static final int[] SFCHomeItemImg = {
			R.drawable.sfc_detection_shelves, R.drawable.detecting_sku,
			R.drawable.sfc_cut_sheet_back, R.drawable.sfc_new_product,
			R.drawable.sfc_stock_transfer_merge,
			R.drawable.sfc_container_shelves_binding, R.drawable.sfc_checked,
			R.drawable.dis, R.drawable.sfc_box_dis_pic,
			R.drawable.sfc_container_shelves_binding,
			R.drawable.sfc_container_transfer };

	/**
	 * usercode,tokken key
	 */
	private String[] users = new String[3];
	private String[] temUsers = new String[3];
	/**
	 * 配货一票多件提交后如果还有未配完的货需要更新数据
	 */
	private String[] disNoCompleteData = null;

	public String[] getDisNoCompleteData() {
		return disNoCompleteData;
	}

	public void setDisNoCompleteData(String[] disNoCompleteData) {
		this.disNoCompleteData = disNoCompleteData;
	}

	public String[] getDisOrdersCode() {
		return disOrdersCode;
	}

	public void setDisOrdersCode(String[] disOrdersCode) {
		this.disOrdersCode = disOrdersCode;
	}

	/**
	 * 配货一票多件提交后如果还有未配完的货需要更新数据，未配完的配货单号列表
	 */
	private String[] disOrdersCode = null;
	private ArrayList<DisBean> listDisAll;
	private ArrayList<DisBean> listDisRemain;

	public ArrayList<DisBean> getListDisAll() {
		return listDisAll;
	}

	public void setListDisAll(ArrayList<DisBean> listDisAll) {
		this.listDisAll = listDisAll;
	}

	public ArrayList<DisBean> getListDisRemain() {
		return listDisRemain;
	}

	public void setListDisRemain(ArrayList<DisBean> listDisRemain) {
		this.listDisRemain = listDisRemain;
	}

	private int[] ints;

	public int[] getInts() {
		return ints;
	}

	public void setInts(int[] ints) {
		this.ints = ints;
	}

	private Bitmap bitmap = null;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String[] getTemUsers() {
		return temUsers;
	}

	public void setTemUsers(String[] temUsers) {
		this.temUsers = temUsers;
	}

	private boolean commitBad = false;

	public boolean isCommitBad() {
		return commitBad;
	}

	public void setCommitBad(boolean commitBad) {
		this.commitBad = commitBad;
	}

	private boolean goOnPickup = false;

	public boolean getGoOnPickup() {
		return goOnPickup;
	}

	public void setGoOnPickup(boolean goOnPickup) {
		this.goOnPickup = goOnPickup;
	}

	private boolean netGood = false;
	private boolean breakTread = false;
	private boolean stop = false;
	private boolean firstInto = true;
	private boolean orderCommit = false;
	private boolean orderDeleteAll = false;
	private boolean boolLock = false;
	private int width;
	private int height;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	private ArrayList<CheckBean> listBean;

	public ArrayList<CheckBean> getListBean() {
		return listBean;
	}

	public void setListBean(ArrayList<CheckBean> listBean) {
		this.listBean = listBean;
	}

	public boolean getBoolLock() {
		return boolLock;
	}

	public void setBoolLock(boolean boolLock) {
		this.boolLock = boolLock;
	}

	public boolean getOrderDeleteAll() {
		return orderDeleteAll;
	}

	public void setOrderDeleteAll(boolean orderDeleteAll) {
		this.orderDeleteAll = orderDeleteAll;
	}

	public boolean getOrderCommit() {
		return orderCommit;
	}

	public void setOrderCommit(boolean orderCommit) {
		this.orderCommit = orderCommit;
	}

	public boolean getFirstInto() {
		return firstInto;
	}

	public void setFirstInto(boolean firstInto) {
		this.firstInto = firstInto;
	}

	public boolean getStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean getBreakTread() {
		return breakTread;
	}

	public void setBreakTread(boolean breakTread) {
		this.breakTread = breakTread;
	}

	public boolean getNetGood() {
		return netGood;
	}

	public void setNetGood(boolean netGood) {
		this.netGood = netGood;
	}

	private MyConfig() {

	}

	public static MyConfig myConfig = null;

	public static MyConfig getMyConfig() {
		if (myConfig == null) {
			myConfig = new MyConfig();
		}
		return myConfig;
	}

	public String[] getUsers() {
		return users;
	}
	// private ArrayList<Activity> activityList = new ArrayList<Activity>();
	// private static ShareData instance;
	// private String[] users=new String[3];
	//
	// public String[] getUsers() {
	// return users;
	// }
	//
	// public void setUsers(String[] users) {
	// this.users = users;
	// }
	//
	// private ShareData() {
	//
	// }
	//
	// /**
	// * 单例模式中获取唯一的MyApplication实例
	// *
	// * @return
	// */
	// public static ShareData getInstance() {
	// if (null == instance) {
	// instance = new ShareData();
	// }
	// return instance;
	//
	// }
	//
	// /**
	// * 添加activity实例
	// *
	// * @param activity
	// */
	// public void addActivity(Activity activity) {
	// for (int i = 0; i < activityList.size(); i++) {
	// if
	// (activityList.get(i).getLocalClassName().equals(activity.getLocalClassName()))
	// {
	// activityList.remove(i);
	// break;
	// }
	// }
	// activityList.add(activity);
	// }
	//
	// /**
	// * 关闭所有的activity
	// */
	//
	// public void exit() {
	//
	// for (Activity activity : activityList) {
	// activity.finish();
	// }
	//
	// System.exit(0);
	//
	// }
	//
	// /**
	// * 关闭以前打开的activity
	// */
	// public void exitCurrentAll() {
	// for (int i = 0; i < activityList.size(); i++) {
	// activityList.get(i).finish();
	// }
	// activityList.clear();
	// }
}
