package com.sfcservice.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sfcservice.bean.BTBean;
import com.sfcservice.bean.CheckBean;
import com.sfcservice.bean.DetectingBean;
import com.sfcservice.bean.DisBean;
import com.sfcservice.bean.DisBoxBean;
import com.sfcservice.bean.DisDisBean;
import com.sfcservice.bean.DisMoreBean;
import com.sfcservice.bean.DisMoreBoxBean;
import com.sfcservice.bean.DisMoreBoxBeanP;
import com.sfcservice.bean.DisMoreItemBean;
import com.sfcservice.bean.DstributionBean;
import com.sfcservice.bean.NewProBean;
import com.sfcservice.bean.NewSkuBean;
import com.sfcservice.bean.SKUBean;
import com.sfcservice.db.SFCDBAdapter;
import com.sfcservice.pda.config.Base64Coder;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class MyConnection1 {
	private String primaryID;
	private String users[] = MyConfig.getMyConfig().getUsers();
	private String strJsonResult = "";
	private JSONObject jo = null;
	private String warehouseId = "2";
	private SFCDBAdapter dbAdapter;
	private SQLiteDatabase db;
	private Cursor cursor;
	private ArrayList<DetectingBean> listDetecBean = null;
	private Bitmap bitmap;
	/**
	 * 记录上传到了第几条
	 */
	private int position;

	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * 循环调用上传至服务器的方法
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				// 链接服务器成功------------->不做处理
				break;
			case MyConfig.ACCESSF:
				// 链接服务器失败------------->再次连接
				MyConfig.getMyConfig().setStop(true);
				break;
			case MyConfig.RESULTS:
				// 上传成功
				position++;
				updateNewProStatus(2, null);
				uploadingNewPro();
				break;
			case MyConfig.RESULTF:
				// 上传失败
				position++;
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				updateNewProStatus(3, strMsg);
				uploadingNewPro();

				break;
			default:
				break;
			}
		}
	};

	private MyConnection1() {

	}

	private static MyConnection1 myConnection;

	public static MyConnection1 getMyConnection() {
		if (myConnection == null) {
			myConnection = new MyConnection1();
		}
		return myConnection;
	}

	public void initDB(Context context) {
		dbAdapter = new SFCDBAdapter(context, "sfc.db", null, 1);
	}

	public void acceptServer(final String sUrl, final String commitData,
			final Handler handler) {
		System.out.println("commit---------------->" + commitData);
		strJsonResult = "";
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("POST");
					OutputStream os = conn.getOutputStream();
					PrintWriter p = new PrintWriter(os);
					p.print("params=" + commitData);
					// os.flush();
					// os.close();
					p.flush();
					p.close();
					InputStream is = conn.getInputStream();
					handler.sendEmptyMessage(MyConfig.ACCESSS);

					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					is.close();
					conn.disconnect();
					strJsonResult = b.toString();
					System.out.println("---------------------->"
							+ strJsonResult);
					jo = new JSONObject(strJsonResult);
					int status = jo.getInt("status");
					// 获取数据不正确
					if (status == 0) {
						String strMsg = jo.getString("msg");
						Message msg = new Message();
						msg.what = MyConfig.RESULTF;
						Bundle data = new Bundle();
						data.putString(MyConfig.TAG, strMsg);
						msg.setData(data);
						handler.sendMessage(msg);
					} else {
						handler.sendEmptyMessage(MyConfig.RESULTS);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	/**
	 * 图片跟文字在同一个线程里加载（只有配货的时候有这种需要）
	 * 
	 * @param sUrl
	 * @param commitData
	 * @param handler
	 */
	public void acceptServerWithImg(final String sUrl, final String commitData,
			final Handler handler) {
		System.out.println("commit---------------->" + commitData);
		strJsonResult = "";
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("POST");
					OutputStream os = conn.getOutputStream();
					PrintWriter p = new PrintWriter(os);
					p.print("params=" + commitData);
					// os.flush();
					// os.close();
					p.flush();
					p.close();
					InputStream is = conn.getInputStream();
					handler.sendEmptyMessage(MyConfig.ACCESSS);

					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					strJsonResult = b.toString();
					System.out.println("---------------------->"
							+ strJsonResult);
					jo = new JSONObject(strJsonResult);
					int status = jo.getInt("status");
					// 获取数据不正确
					if (status == 0) {
						String strMsg = jo.getString("msg");
						Message msg = new Message();
						msg.what = MyConfig.RESULTF;
						Bundle data = new Bundle();
						data.putString(MyConfig.TAG, strMsg);
						msg.setData(data);
						handler.sendMessage(msg);
						is.close();
						conn.disconnect();
					}
					// 数据正确获取图片的URL
					else {
						String str = jo.getString("data");
						if (str.equals("")) {
							handler.sendEmptyMessage(MyConfig.RESULTS);
							is.close();
							conn.disconnect();
							return;
						}
						JSONObject jo1 = jo.getJSONObject("data");
						if (jo1.isNull("pic")) {
							handler.sendEmptyMessage(MyConfig.RESULTS);
							is.close();
							conn.disconnect();
							return;
						}
						String smallImgUrl = jo1.getString("pic");

						URL urlImg = new URL(MyConfig.URL_PRE + smallImgUrl);
						conn = (HttpURLConnection) urlImg.openConnection();
						conn.setConnectTimeout(MyConfig.TIME_OUT);
						conn.setReadTimeout(MyConfig.TIME_OUT);
						is = conn.getInputStream();
						bitmap = BitmapFactory.decodeStream(is);
						MyConfig.getMyConfig().setBitmap(bitmap);
						is.close();
						conn.disconnect();
						handler.sendEmptyMessage(MyConfig.RESULTS);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	/**
	 * 一票多件多sku访问服务器 图片与数据同步
	 * 
	 * @param sUrl
	 * @param commitData
	 * @param handler
	 */
	public void acceptDisMoreServerWithImg(final String sUrl,
			final String commitData, final Handler handler) {
		System.out.println("commit---------------->" + commitData);
		strJsonResult = "";
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("POST");
					OutputStream os = conn.getOutputStream();
					PrintWriter p = new PrintWriter(os);
					p.print("params=" + commitData);
					// os.flush();
					// os.close();
					p.flush();
					p.close();
					InputStream is = conn.getInputStream();
					handler.sendEmptyMessage(MyConfig.ACCESSS);

					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					strJsonResult = b.toString();
					System.out.println("---------------------->"
							+ strJsonResult);
					jo = new JSONObject(strJsonResult);
					int status = jo.getInt("status");
					// 获取数据不正确
					if (status == 0) {
						String strMsg = jo.getString("msg");
						Message msg = new Message();
						msg.what = MyConfig.RESULTF;
						Bundle data = new Bundle();
						data.putString(MyConfig.TAG, strMsg);
						msg.setData(data);
						handler.sendMessage(msg);
						is.close();
						conn.disconnect();
					}
					// 数据正确获取图片的URL
					else {
						if (jo.getString("noData").equals("1")) {
							handler.sendEmptyMessage(MyConfig.RESULTS);
							is.close();
							conn.disconnect();
							return;
						}
						String str = jo.getString("data");
						if (str.equals("")) {
							handler.sendEmptyMessage(MyConfig.RESULTS);
							is.close();
							conn.disconnect();
							return;
						}
						String smallImgUrl = jo.getJSONObject("data")
								.getJSONArray("data").getJSONObject(0)
								.getString("pic");
						URL urlImg = new URL(MyConfig.URL_PRE + smallImgUrl);
						conn = (HttpURLConnection) urlImg.openConnection();
						conn.setConnectTimeout(MyConfig.TIME_OUT);
						conn.setReadTimeout(MyConfig.TIME_OUT);
						is = conn.getInputStream();
						bitmap = BitmapFactory.decodeStream(is);
						MyConfig.getMyConfig().setBitmap(bitmap);
						is.close();
						conn.disconnect();
						handler.sendEmptyMessage(MyConfig.RESULTS);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	/**
	 * 登录传出去的JSON
	 * 
	 * @param user_code
	 * @param user_password
	 * @return
	 */
	public String writeUserJosnObject(String user_code, String user_password) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();
			js.key("userId");
			js.value(user_code);
			js.key("password");
			js.value(user_password);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception

		}
		return null;
	}

	/**
	 * 其他情况传出去的JSON
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeJsonWithUserInfo(String[] keys, String[] values,
			String methods) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();

			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 一票多件继续传出去的JSON
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeMoreContinueJsonWithUserInfo(String[] keys,
			String[] values, ArrayList<DisBean> listDisBean, String methods) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();
			
			js.key("areas");
			js.array();
			for (int i = 0; i < listDisBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("areaId", listDisBean.get(i).getAbo_id());
				js.value(jb);
			}
			js.endArray();
			
			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();

			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 一票多件配置界面传的json
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeDisMoreJsonWithUserInfo(String[] keys, String[] values,
			ArrayList<DisBean> listBean, String methods) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("areas");
			js.array();
			for (int i = 0; i < listBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("areaId", listBean.get(i).getAbo_id());
				js.value(jb);
			}
			js.endArray();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();

			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 中转车特有的json
	 */
	public String writeTJsonWithUserInfo(ArrayList<BTBean> btListBean,
			String containerCode, String wsCode, String car, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.array();
			for (int i = 0; i < btListBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("product_id", btListBean.get(i).getProductId());
				jb.put("transferQty", btListBean.get(i).getCount());
				jb.put("putaway_lot_number", btListBean.get(i)
						.getPutawayLotNumber());
				js.value(jb);
			}
			js.endArray();
			// js.value(writeArray(btListBean));

			js.key("containerCode");
			js.value(containerCode);

			js.key("wsCode");
			js.value(wsCode);

			js.key("tempContainerCode");
			js.value(car);

			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 一票多件单SKUjson
	 */
	public String writeOneJsonWithUserInfo(String[] keys, String[] values,
			String qty, String product_id, String ordes_code, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("products");
			js.array();
			JSONObject jb = new JSONObject();
			jb.put("updateQty", qty);
			jb.put("num", "1");
			jb.put("sort_number", "1");
			jb.put("product_id", product_id);
			jb.put("orders_code", ordes_code);
			js.value(jb);
			js.endArray();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();
			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 一票多件多SKU配货特有的json
	 */
	public String writeDisMoreJsonWithUserInfo(String[] keys, String[] values,
			ArrayList<DisMoreItemBean> listBean,
			ArrayList<DisBean> listDisBean, String product_id, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("products");
			js.array();
			for (int i = 0; i < listBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("sort_number", listBean.get(i).getLocation());
				jb.put("updateQty", listBean.get(i).getCount());
				jb.put("num", listBean.get(i).getCount());
				jb.put("orders_code", listBean.get(i).getOrders_code());
				jb.put("product_id", product_id);
				js.value(jb);
			}
			js.endArray();

			js.key("areas");
			js.array();
			for (int i = 0; i < listDisBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("areaId", listDisBean.get(i).getAbo_id());
				js.value(jb);
			}
			js.endArray();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();
			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 一票多件多SKU配货跳过,解锁特有的json
	 */
	public String writeDisMorePassJsonWithUserInfo(String[] keys,
			String[] values, ArrayList<DisMoreItemBean> listBean,
			ArrayList<String> listStrs, ArrayList<DisBean> listDisBean,
			String product_id, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("products");
			js.array();
			for (int i = 0; i < listBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("sort_number", listBean.get(i).getLocation());
				jb.put("updateQty", listBean.get(i).getCount());
				jb.put("num", listBean.get(i).getCount());
				jb.put("orders_code", listBean.get(i).getOrders_code());
				jb.put("product_id", product_id);
				js.value(jb);
			}
			js.endArray();

			js.key("unfinishedOrders");
			js.array();
			for (int i = 0; i < listStrs.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("order_code", listStrs.get(i));
				js.value(jb);
			}
			js.endArray();

			js.key("areas");
			js.array();
			for (int i = 0; i < listDisBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("areaId", listDisBean.get(i).getAbo_id());
				js.value(jb);
			}
			js.endArray();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();
			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 一票多件单SKU添加异常特有的json
	 */
	public String writeManyOneExceptionJsonWithUserInfo(String[] keys,
			String[] values, String orders_code, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("orders_code");
			js.array();
			JSONObject jb = new JSONObject();
			jb.put("sort_number", "0");
			jb.put("orders_code", orders_code);
			js.value(jb);
			js.endArray();
			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();
			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 一票多件添加异常特有的json
	 */
	public String writeMoreExceptionJsonWithUserInfo(String[] keys,
			String[] values, ArrayList<DisMoreItemBean> listBean, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("orders_code");
			js.array();
			for (int j = 0; j < listBean.size(); j++) {
				JSONObject jb = new JSONObject();
				jb.put("sort_number", listBean.get(j).getLocation());
				jb.put("orders_code", listBean.get(j).getOrders_code());
				js.value(jb);
			}
			js.endArray();
			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();
			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 只能用tempuser不然上传的 人(usercode) 不准确
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeJsonWithTempUserInfo(String[] keys, String[] values,
			String methods) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			String[] tempUsers = MyConfig.getMyConfig().getTemUsers();
			js.object();
			js.key("userCode");
			js.value(tempUsers[0]);
			js.key("token");
			js.value(tempUsers[1]);
			js.key("key");
			js.value(tempUsers[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();

			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 一票多件多SKU提交之后获取数据提交的内容
	 */
	public String writeMoreCommitJsonWithUserInfo(String[] keys,
			String[] values, ArrayList<String> listBean,
			ArrayList<DisBean> listDisBean, String methods) {

		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("userCode");
			js.value(users[0]);
			js.key("token");
			js.value(users[1]);
			js.key("key");
			js.value(users[2]);
			js.key("warehouseId");
			js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			js.key("unfinishedOrders");
			js.array();
			for (int j = 0; j < listBean.size(); j++) {
				JSONObject jb = new JSONObject();
				jb.put("order_code", listBean.get(j).toString());
				js.value(jb);
			}
			js.endArray();

			js.key("areas");
			js.array();
			for (int i = 0; i < listDisBean.size(); i++) {
				JSONObject jb = new JSONObject();
				jb.put("areaId", listDisBean.get(i).getAbo_id());
				js.value(jb);
			}
			js.endArray();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();
			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解析tokken与key并存储至本地数据库
	 */
	public void insertUser(String user_login_id) {
		try {
			db = dbAdapter.getWritableDatabase();
			String user_last_update = jo.getString("systime");
			JSONObject joData = jo.getJSONObject("data");
			String user_tokken = Base64Coder.encodeString(joData
					.getString("token"));
			String user_key = Base64Coder.encodeString(joData.getString("key"));
			cursor = db.query("user", null, "user_login_id=?",
					new String[] { user_login_id }, null, null, null);

			ContentValues values = new ContentValues();
			values.put("user_login_id", user_login_id);
			values.put("user_tokken", user_tokken);
			values.put("user_key", user_key);
			values.put("user_last_update", user_last_update);

			if (cursor.getCount() != 0) {
				db.update("user", values, "user_login_id=?",
						new String[] { user_login_id });
			} else {
				db.insert("user", null, values);
			}

			users[0] = user_login_id;
			users[1] = user_tokken;
			users[2] = user_key;

			cursor.close();
			db.close();
			dbAdapter.close();

		} catch (Exception e) {
			// TODO: handle exception
			if (cursor != null) {
				cursor.close();
			}
			if (db != null)
				db.close();
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
	}

	/**
	 * 获得检测货架数据
	 */
	public ArrayList<DetectingBean> getDetectingData() {
		listDetecBean = new ArrayList<DetectingBean>();
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				DetectingBean bean = new DetectingBean();
				bean.setSku(joList.getJSONObject(i).getString("product_sku"));
				bean.setCount(joList.getJSONObject(i).getString("wp_quantity"));
				bean.setHoldCount(joList.getJSONObject(i).getString(
						"wp_quantity_hold"));

				String str = joList.getJSONObject(i).getString("wp_status");
				if (str.equals("0")) {
					bean.setStatus("已释放");
				} else if (str.equals("1")) {
					bean.setStatus("正常");
				} else if (str.equals("2")) {
					bean.setStatus("盘点中");
				}
				listDetecBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return listDetecBean;
	}

	/**
	 * 获得检测SKU数据
	 */
	public String getDetectingSKUData(ArrayList<SKUBean> listBean) {
		String strAll = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			strAll = "总可用库存 : " + jo.getString("quantity") + " ; 总冻结库存 : "
					+ jo.getString("quantity_hold");
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				SKUBean bean = new SKUBean();
				bean.setWsCode(joList.getJSONObject(i).getString("ws_code"));
				bean.setWpb_quantity(joList.getJSONObject(i).getString(
						"wpb_quantity"));
				bean.setWpb_quantity_hold(joList.getJSONObject(i).getString(
						"wpb_quantity_hold"));
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strAll;
	}

	// 获取产品上架箱号信息
	public String getBoxInfo(ArrayList<NewSkuBean> listBean) {
		String str = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				NewSkuBean bean = new NewSkuBean();
				bean.setSku(joList.getJSONObject(i).getString("sku"));
				bean.setId(joList.getJSONObject(i).getString("id"));
				bean.setCount(joList.getJSONObject(i).getString("quantity"));
				bean.setPic(joList.getJSONObject(i).getString("picture_path"));
				listBean.add(bean);
			}
			str = jo.getString("ws_code");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return str;
	}

	/**
	 * 获得推荐货位
	 */
	public String getRecommendShelf() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			return jo.getString("data");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "获取失败";
	}

	// 绑定与转移根据原货架号获取箱号
	public String getBoxNumByOldShelfNum() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject jo1 = jo.getJSONObject("data");
			return jo1.getString("container_code");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "获取失败";
	}

	/**
	 * 存储数据
	 */
	public void saveData(String table, String[] keys, String[] datas) {
		ContentValues values = new ContentValues();

		for (int i = 0; i < keys.length; i++) {
			values.put(keys[i], datas[i]);
		}

		db = dbAdapter.getWritableDatabase();
		db.insert(table, null, values);

		db.close();
		dbAdapter.close();
	}

	/**
	 * 获得产品上架的全部信息
	 */
	public boolean getNewProductInfo20(ArrayList<NewProBean> list, int STATUS) {
		try {
			db = dbAdapter.getReadableDatabase();
			// 获取产品上架所有信息

			switch (STATUS) {
			case 0:
				cursor = db.query("new_product", null, null, null, null, null,
						null);
				break;
			case 1:
				cursor = db.query("new_product", null, "status=?",
						new String[] { "1" }, null, null, null);
				break;
			case 2:
				cursor = db.query("new_product", null, "status=?",
						new String[] { "2" }, null, null, null);
				break;
			case 3:
				cursor = db.query("new_product", null, "status=?",
						new String[] { "3" }, null, null, null);
				break;

			default:
				break;
			}
			int position = list.size() > 0 ? list.size() : 0;
			if (cursor.moveToPosition(position)) {

				for (int i = 0; i < MyConfig.DB_LOADING; i++) {
					NewProBean bean = new NewProBean();

					bean.setUploadDate(cursor.getString(cursor
							.getColumnIndex("upload_date")));
					bean.setStorageDate(cursor.getString(cursor
							.getColumnIndex("storage_date")));

					bean.setUser(cursor.getString(cursor
							.getColumnIndex("user_login_id")));

					bean.setBoxNum(cursor.getString(cursor
							.getColumnIndex("box_num")));

					bean.setShelfNum(cursor.getString(cursor
							.getColumnIndex("shelf_num")));

					bean.setStatus(cursor.getString(cursor
							.getColumnIndex("status")));

					if (bean.getStatus().equals("3")) {
						bean.setStatus("上传失败");
						bean.setCause(cursor.getString(cursor
								.getColumnIndex("cause")));
					} else if (bean.getStatus().equals("1")) {
						bean.setStatus("未上传");
					} else if (bean.getStatus().equals("2")) {
						bean.setStatus("已上传");
					}
					list.add(bean);
					if (!cursor.moveToNext()) {
						break;
					}
				}
				closeSFCDB(cursor, db);
				return true;
			}
			closeSFCDB(cursor, db);

		} catch (Exception e) {
			// TODO: handle exception
			closeSFCDB(cursor, db);
		}
		return false;
	}

	/**
	 * 上传产品上架信息至服务器端，取一条传一条
	 */
	public void uploadingNewPro() {
		if (!MyConfig.getMyConfig().getNetGood()) {
			MyConfig.getMyConfig().setStop(true);
			return;
		}
		SQLiteDatabase db = dbAdapter.getReadableDatabase();
		Cursor cursor = db.query("new_product", null, null, null, null, null,
				null);
		if (cursor.moveToPosition(position)) {
			String status = cursor.getString(cursor.getColumnIndex("status"));
			String user = cursor.getString(cursor
					.getColumnIndex("user_login_id"));
			String boxNum = cursor.getString(cursor.getColumnIndex("box_num"));
			String shelfNum = cursor.getString(cursor
					.getColumnIndex("shelf_num"));
			primaryID = cursor.getString(cursor.getColumnIndex("_id"));

			if (status.equals("2")) {
				closeSFCDB(cursor, db);
				position++;
				uploadingNewPro();
				return;
			}
			Cursor cursor1 = db.query("user", null, "user_login_id=?",
					new String[] { user }, null, null, null);
			if (cursor1.moveToFirst()) {
				String tokken = cursor1.getString(cursor1
						.getColumnIndex("user_tokken"));
				String key = cursor1.getString(cursor1
						.getColumnIndex("user_key"));
				MyConfig.getMyConfig().setTemUsers(
						new String[] { user, tokken, key });
				cursor1.close();

				String commitData = writeJsonWithTempUserInfo(new String[] {
						"container_code", "putaway_ws_code" }, new String[] {
						boxNum, shelfNum }, "pdaSubmitPutawayNew");
				closeSFCDB(cursor, db);
				acceptServer(MyConfig.URL_COMMON, commitData, handler);
			}
		} else {
			closeSFCDB(cursor, db);
			MyConfig.getMyConfig().setStop(true);
			position = 0;
		}

	}

	/**
	 * 修改数据库里面表的状态
	 */
	public void updateNewProStatus(int STATE, String cause) {
		SQLiteDatabase db = dbAdapter.getWritableDatabase();
		Cursor cursor = db.query("new_product", null, "_id=?",
				new String[] { primaryID }, null, null, null);

		if (cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put("user_login_id", cursor.getString(1));
			values.put("box_num", cursor.getString(2));
			values.put("shelf_num", cursor.getString(3));
			values.put("upload_date", MyTool.getTime());
			if (STATE == 2) {
				values.put("status", "2");
			}
			if (STATE == 3) {
				values.put("status", "3");
				values.put("cause", cause);
			}
			db.update("new_product", values, "_id=?",
					new String[] { primaryID });
			closeSFCDB(cursor, db);
		}

	}

	// 清空已上传的数据
	public void clearData() {
		SQLiteDatabase db = dbAdapter.getWritableDatabase();
		Cursor cursor = db.query("new_product", null, null, null, null, null,
				null);
		if (cursor.moveToFirst()) {
			do {
				String date = cursor.getString(cursor
						.getColumnIndex("upload_date"));
				String status = cursor.getString(cursor
						.getColumnIndex("status"));
				String id = cursor.getString(cursor.getColumnIndex("_id"));

				if (status.equals("2")) {
					String[] strs = date.split(" ");
					String[] strd = strs[0].split("-");

					int y = Integer.parseInt(strd[0]);
					int m = Integer.parseInt(strd[1]);
					int d = Integer.parseInt(strd[2]);

					if (delete(y, m, d)) {
						db.delete("new_product", "_id=?", new String[] { id });
					}
				}
			} while (cursor.moveToNext());
		}
		closeSFCDB(cursor, db);
	}

	private boolean delete(int y, int m, int d) {
		Calendar c = Calendar.getInstance();

		int cy = c.get(Calendar.YEAR);
		int cm = c.get(Calendar.MONTH) + 1;
		int cd = c.get(Calendar.DAY_OF_MONTH);

		if (cy == y && cm == m) {
			int i = cd - d;
			if (i >= MyConfig.DELETE_DAY) {
				return true;
			}
			return false;
		}
		// 每个月都算作30天
		if (cy == y && cm != m) {
			if ((cm - m) > 1) {
				return true;
			}
			if ((cm - m == 1)) {
				int i = cd + 30 - d;
				if ((i >= MyConfig.DELETE_DAY)) {
					return true;
				}
			}
			return false;
		}
		if (cy != y) {
			if ((cy - y > 1)) {
				return true;
			}
			if ((cy - y) == 1) {
				if (cm == 1 && m == 12) {
					int i = cd + 30 - d;
					if ((i < MyConfig.DELETE_DAY)) {
						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	// 获取图片信息
	public void getImg(final String sUrl, final Handler handler) {
		MyConfig.getMyConfig().setBitmap(null);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);

					InputStream is = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(is);
					handler.sendEmptyMessage(10);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	/**
	 * 配置界面获取配货初始数据一票一件
	 * 
	 * @return
	 */
	public String[] getDistributionInfo() {
		String[] strInfo = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			// strFinished 0,表示有未完成的订单,1,表示正常,因为在前面加了判断所以进来的时候finished一定为1
			// String strFinished=jo.getString("finished");
			String strNoData = jo.getString("noData");
			// 没有任何产品订单
			if (strNoData.equals("1")) {
				return strInfo;
			}
			// 有产品
			JSONObject jo1 = jo.getJSONObject("data");
			strInfo = new String[10];
			strInfo[0] = jo1.getString("product_title");
			strInfo[1] = jo1.getString("ws_code");
			strInfo[2] = jo1.getString("barcode");
			strInfo[3] = jo1.getString("spic");
			strInfo[4] = jo1.getString("product_id");
			strInfo[5] = jo1.getString("opm_quantity");
			strInfo[6] = jo1.getString("count");
			strInfo[7] = jo1.getString("sku");
			strInfo[8] = jo1.getString("customer_id");
			strInfo[9] = jo1.getString("opm_time");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return strInfo;
	}

	// 获取配下一个信息一票一件
	public String[] getDistributionNextInfo() {
		String[] strInfo = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String str = jo.getString("noData");
			if (str.equals("1")) {
				strInfo = new String[1];
				JSONObject jo1 = jo.getJSONObject("data");
				strInfo[0] = jo1.getString("op_code");
				return strInfo;
			}

			JSONObject jo1 = jo.getJSONObject("data");
			strInfo = new String[11];
			strInfo[0] = jo1.getString("product_title");
			strInfo[1] = jo1.getString("ws_code");
			strInfo[2] = jo1.getString("barcode");
			strInfo[3] = jo1.getString("spic");
			strInfo[4] = jo1.getString("product_id");
			strInfo[5] = jo1.getString("opm_quantity");
			strInfo[6] = jo1.getString("count");

			if (jo1.isNull("op_code")) {
				strInfo[7] = "";
			} else {
				strInfo[7] = jo1.getString("op_code");
			}
			strInfo[8] = jo1.getString("sku");
			strInfo[9] = jo1.getString("customer_id");
			strInfo[10] = jo1.getString("opm_time");

			for (int i = 0; i < strInfo.length; i++) {
				System.out.println("------->>" + strInfo[i]);
			}
			// strInfo = new String[8];
			// JSONObject jo1 = jo.getJSONObject("data");
			// strInfo[0]=jo1.getString("");
			// strInfo[1] = jo1.getString("opm_id");
			// strInfo[2] = jo1.getString("op_code");
			//
			// strInfo[3] = jo1.getString("ws_code");
			// strInfo[4] = jo1.getString("barcode");
			// strInfo[5] = jo1.getString("product_title");
			// strInfo[6] = jo1.getString("pic");
			// strInfo[7] = jo1.getString("opm_quantity");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strInfo;
	}

	/**
	 * 获取配货初始数据一票多件多SKU信息
	 * 
	 * @return
	 */
	public String[] getDistributionMoreInfo(ArrayList<DisMoreBean> listBean) {
		String[] strInfo = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			// strFinished 0,表示有未完成的订单,1,表示正常,因为在前面加了判断所以进来的时候finished一定为1
			// String strFinished=jo.getString("finished");
			String strNoData = jo.getString("noData");
			// 没有任何产品订单
			if (strNoData.equals("1")) {
				strInfo = new String[1];
				// 判断是否为json对象
				if (jo.getString("data").equals("")) {
					return strInfo;
				}

				if (jo.getJSONObject("data").isNull("op_code")) {
					strInfo[0] = "";
					return strInfo;
				}
				String op_code = jo.getJSONObject("data").getString("op_code");
				strInfo[0] = op_code;
				return strInfo;
			}
			JSONObject jo1 = jo.getJSONObject("data");
			// 从配货界面来
			if (jo1.isNull("op_code")) {
				strInfo = new String[5];
				strInfo[0] = jo1.getString("ws_code");
				strInfo[1] = jo1.getString("opm_time");
				strInfo[2] = jo1.getString("count");
				strInfo[3] = jo1.getString("orderCount");
				strInfo[4] = jo1.getString("pickupedOrders");
			}
			// 点击确认配货或者已配货的数据
			else {
				strInfo = new String[6];
				strInfo[0] = jo1.getString("ws_code");
				strInfo[1] = jo1.getString("opm_time");
				strInfo[2] = jo1.getString("count");
				strInfo[3] = jo1.getString("op_code");
				strInfo[4] = jo1.getString("orderCount");
				strInfo[5] = jo1.getString("pickupedOrders");
			}
			JSONArray jarray = jo1.getJSONArray("data");
			for (int i = 0; i < jarray.length(); i++) {
				DisMoreBean bean = new DisMoreBean();
				bean.setBarcode(jarray.getJSONObject(i).getString("barcode"));
				bean.setCount(jarray.getJSONObject(i).getString("count"));
				bean.setCustomer_id(jarray.getJSONObject(i).getString(
						"customer_id"));
				bean.setOpm_quantity(jarray.getJSONObject(i).getString(
						"opm_quantity"));
				bean.setOrders_code(jarray.getJSONObject(i).getString(
						"orders_code"));
				bean.setPic(jarray.getJSONObject(i).getString("pic"));
				bean.setProduct_id(jarray.getJSONObject(i).getString(
						"product_id"));
				bean.setProduct_title(jarray.getJSONObject(i).getString(
						"product_title"));
				bean.setSku(jarray.getJSONObject(i).getString("sku"));
				bean.setSpic(jarray.getJSONObject(i).getString("spic"));

				JSONArray jarray1 = jarray.getJSONObject(i).getJSONArray(
						"sort_number");
				ArrayList<DisMoreItemBean> list = new ArrayList<DisMoreItemBean>();

				for (int j = 0; j < jarray1.length(); j++) {
					JSONObject joo = jarray1.getJSONObject(j);
					joo.getString("sort_number");
					DisMoreItemBean bean1 = new DisMoreItemBean();
					bean1.setCount(joo.getString("qty"));
					bean1.setLocation(joo.getString("sort_number"));
					bean1.setOrders_code(joo.getString("orders_code"));
					list.add(bean1);
					// Iterator<?> it = joo.keys();
					// while (it.hasNext()) {
					// DisMoreItemBean bean1 = new DisMoreItemBean();
					// String str = it.next().toString();
					// bean1.setLocation(str);
					// bean1.setCount(joo.getString(str));
					// bean1.setOrders_code(joo.getString("orders_code"));
					// list.add(bean1);
					// }
				}
				bean.setList(list);
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strInfo;
	}

	/**
	 * 获取一票多件单sku的数据
	 */
	public String[] getDisManyOneData() {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String strNoData = jo.getString("noData");
			// 没有任何产品订单
			if (strNoData.equals("1")) {
				strs = new String[1];
				strs[0] = jo.getJSONObject("data").getString("op_code");
				return strs;
			}
			strs = new String[15];
			JSONObject jo1 = jo.getJSONObject("data");
			strs[0] = jo1.getString("opm_time");
			strs[11] = jo1.getString("pickupedOrders");
			strs[12] = jo1.getString("orderCount");
			strs[6] = jo1.getString("op_code");
			strs[7] = jo1.getString("page");
			JSONObject joX = jo1.getJSONArray("data").getJSONObject(0);
			strs[1] = joX.getString("ws_code");
			strs[2] = joX.getString("opm_quantity");
			strs[3] = joX.getString("barcode");
			strs[14] = joX.getString("spic");
			strs[13] = joX.getString("opm_id");
			strs[4] = joX.getString("product_id");
			strs[9] = joX.getString("customer_id");
			strs[10] = joX.getString("sku");
			strs[5] = joX.getJSONArray("sort_number").getJSONObject(0)
					.getString("qty");
			strs[8] = joX.getJSONArray("sort_number").getJSONObject(0)
					.getString("orders_code");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 获得配冲突的数据一票多件多SKU
	 * 
	 * @return false表示没有冲突，true表示有冲突
	 */
	public boolean getDisMoreDataOfConflict(ArrayList<DisMoreItemBean> listBean) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String str = jo.getString("data");
			if (str.equals("")) {
				return false;
			}
			JSONArray array = jo.getJSONObject("data").getJSONArray("undoQty");
			for (int i = 0; i < array.length(); i++) {
				DisMoreItemBean bean = new DisMoreItemBean();
				JSONObject joX = array.getJSONObject(i);
				JSONObject joXX = joX.getJSONObject(joX.keys().next()
						.toString());

				bean.setLocation(joXX.keys().next().toString());
				bean.setCount(joXX.getString(bean.getLocation()));
				listBean.add(bean);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 获取一票多件配货冲突的OP_CODE
	 * 
	 * @return
	 */
	public String getConflictOpCode() {
		String op_code = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject joX = jo.getJSONObject("data");
			op_code = joX.getString("op_code");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return op_code;
	}

	/**
	 * 获取一票的多件单SKU的数据冲突
	 * 
	 * @return
	 */
	public void getManyOneException() {
		try {
			// JSONObject jo = new JSONObject(strJsonResult);
			// JSONObject jo1 = jo.getJSONObject("data");
			// JSONObject jo2 = jo1.getJSONArray("undoQty").getJSONObject(0);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String[] getFailedCountAndOpCode() {
		String strs[] = new String[2];
		String str = "0";
		strs[0] = str;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject jo1 = jo.getJSONObject("data");
			str = jo1.getString("undoQty");
			strs[0] = str;
			strs[1] = jo1.getString("op_code");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return strs;
	}

	/**
	 * 判断是否绑定
	 * 
	 * @return
	 */
	public boolean isBinding() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject jo1 = jo.getJSONObject("data");
			if (jo1.isNull("bindContainer")) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	// 获取绑定于转移里面的箱号信息
	public String[] getBTBoxInfo(ArrayList<BTBean> listBean) {
		String[] strs = { "", "" };
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				BTBean bean = new BTBean();
				bean.setSku(joList.getJSONObject(i).getString("product_sku"));
				bean.setCount(joList.getJSONObject(i).getString("wpb_quantity"));
				bean.setPic(joList.getJSONObject(i).getString("pic"));
				bean.setProductId(joList.getJSONObject(i).getString(
						"product_id"));
				bean.setHoldCount(joList.getJSONObject(i).getString(
						"wpb_quantity_hold"));
				bean.setPutawayLotNumber(joList.getJSONObject(i).getString(
						"putaway_lot_number"));
				bean.setStatus(joList.getJSONObject(i).getString("status_text"));
				listBean.add(bean);
			}

			strs[0] = jo.getString("wsCode");
			strs[1] = jo.getString("containerCode2");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return strs;
	}

	// 获取配货单
	public void getDisOrder(ArrayList<DstributionBean> listBean) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				DstributionBean bean = new DstributionBean();
				bean.setShelfNum(joList.getJSONObject(i).getString("ws_code"));
				bean.setCount(joList.getJSONObject(i).getString("opm_quantity"));
				bean.setPic(joList.getJSONObject(i).getString("pic"));
				bean.setClientProNum(joList.getJSONObject(i).getString(
						"barcode"));
				bean.setOpmId(joList.getJSONObject(i).getString("opm_id"));
				bean.setProductId(joList.getJSONObject(i).getString(
						"product_id"));
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 获取配货单
	public void getDisManyOneOrder(ArrayList<DstributionBean> listBean) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				DstributionBean bean = new DstributionBean();
				bean.setShelfNum(joList.getJSONObject(i).getString("ws_code"));
				bean.setCount(joList.getJSONObject(i).getString("opm_quantity"));
				bean.setPic(joList.getJSONObject(i).getString("pic"));
				bean.setClientProNum(joList.getJSONObject(i).getString(
						"barcode"));
				bean.setOrders_code(joList.getJSONObject(i).getString(
						"orders_code"));
				bean.setOpmId(joList.getJSONObject(i).getString("opm_id"));
				bean.setProductId(joList.getJSONObject(i).getString(
						"product_id"));
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void closeSFCDB(Cursor cursor, SQLiteDatabase db) {
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	public void update(final String sUrl, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);
					InputStream is = conn.getInputStream();

					// int ch;
					// StringBuffer b = new StringBuffer();
					// while ((ch = is.read()) != -1) {
					// b.append((char) ch);
					// }
					// is.close();
					// conn.disconnect();
					// strJsonResult = b.toString();
					// System.out.println("---------------------->"
					// + strJsonResult);

					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document doc = builder.parse(is);
					Element root = doc.getDocumentElement();
					String appname = root.getElementsByTagName("appname")
							.item(0).getTextContent();
					String vercode = root.getElementsByTagName("vercode")
							.item(0).getTextContent();
					String apkurl = root.getElementsByTagName("apkurl").item(0)
							.getTextContent();
					is.close();
					conn.disconnect();

					Bundle bundle = new Bundle();
					bundle.putStringArray(MyConfig.TAG, new String[] { appname,
							vercode, apkurl });
					Message msg = new Message();
					msg.what = 30;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	/**
	 * 获得是否配过货
	 */
	public String[] getDisOld() {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String strFinished = jo.getString("finished");
			if (strFinished.equals("0")) {
				JSONObject jo1 = jo.getJSONObject("data");
				strs = new String[5];
				strs[0] = jo1.getString("op_code");
				strs[1] = jo1.getString("opm_orders_type");
				strs[2] = jo1.getString("sortBy");
				strs[3] = jo1.getString("ws_code");
				strs[4] = jo1.getString("opm_time");
				return strs;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return strs;
	}

	/**
	 * 多SKU获取配货过的数据
	 * 
	 * @return
	 */
	public String[] getMoreDisOld() {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String strFinished = jo.getString("finished");
			if (strFinished.equals("0")) {
				JSONObject jo1 = jo.getJSONObject("data");
				strs = new String[7];
				strs[0] = jo1.getString("op_code");
				strs[1] = jo1.getString("opm_orders_type");
				strs[2] = jo1.getString("sortBy");
				strs[3] = jo1.getString("ws_code");
				strs[4] = jo1.getString("pickupedOrders");
				strs[5] = jo1.getString("orderCount");
				strs[6] = jo1.getString("opm_time");
				if(jo1.getString("areas").equals("")){
					return null;
				}
				JSONArray jarray = jo1.getJSONArray("areas");
				ArrayList<DisBean> list = new ArrayList<DisBean>();
				for (int i = 0; i < jarray.length(); i++) {
					DisBean bean = new DisBean();
					bean.setAbo_id(jarray.getJSONObject(i).getString("areaId"));
					bean.setAbo_name("");
					list.add(bean);
				}
				
				MyConfig.getMyConfig().setListDisRemain(list);
				return strs;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 获取配货箱列表
	 */
	public void getDisBox(ArrayList<DisBoxBean> listBean) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				DisBoxBean bean = new DisBoxBean();
				bean.setBoxNum(joList.getJSONObject(i).getString(
						"container_code"));
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 获取所有SKU信息
	 */
	public void getSKUInfo(ArrayList<CheckBean> list) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				CheckBean bean = new CheckBean();
				bean.setProductSku(joList.getJSONObject(i).getString(
						"product_sku"));
				bean.setContainerCode(joList.getJSONObject(i).getString(
						"container_code"));
				bean.setStatus(joList.getJSONObject(i).getString("status"));
				bean.setPic(joList.getJSONObject(i).getString("pic"));
				bean.setPda(joList.getJSONObject(i).getString("pda"));
				String usable = joList.getJSONObject(i).getString("usable");
				String start = "";
				bean.setUsable(usable);
				if (usable.equals("1")) {
					start = joList.getJSONObject(i).getString("start");
				}
				bean.setStart(start);
				list.add(bean);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 此货架是否要添加到带盘点任务列表里面
	 */
	public boolean isAdd() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String str = jo.getString("dialog");
			if (str.equals("1")) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/**
	 * 获得货架号
	 */
	public String getWsCode() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			return jo.getString("ws_code");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 获取上一个信息
	 */
	public CheckBean getPreInfo() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject jo1 = jo.getJSONObject("data");
			CheckBean bean = new CheckBean();
			bean.setProductSku(jo1.getString("product_sku"));
			bean.setContainerCode(jo1.getString("container_code"));
			bean.setStatus(jo1.getString("status"));
			bean.setPic(jo1.getString("pic"));
			String usable = jo1.getString("usable");
			String start = "";
			bean.setUsable(usable);
			if (usable.equals("1")) {
				start = jo1.getString("start");
			}
			bean.setStart(start);
			return bean;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 获取仓库分区信息
	 */
	public ArrayList<String> getDisInfo(
			ArrayList<ArrayList<DisDisBean>> listBeans) {
		// {"data":{"103":{"name":"2F","area":{"A":{"area":"A","order":{"count":1},"ws_code":{"count":1},"product":{"count":1}}}},"106":{"name":"\u8d35\u91cd\u533a","area":{"B":{"area":"B","order":{"count":1},"ws_code":{"count":1},"product":{"count":1}}}}},"status":1}
		ArrayList<String> list = new ArrayList<String>();
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject jo1 = jo.getJSONObject("data");
			Iterator<?> it = jo1.keys();
			while (it.hasNext()) {
				String str = it.next().toString();
				JSONObject jo2 = jo1.getJSONObject(str);
				String title = jo2.getString("name");
				JSONObject jo3 = jo2.getJSONObject("area");
				Iterator<?> it1 = jo3.keys();
				ArrayList<DisDisBean> listB = new ArrayList<DisDisBean>();
				int order_count = 0;
				while (it1.hasNext()) {
					DisDisBean bean = new DisDisBean();
					JSONObject jo4 = jo3.getJSONObject(it1.next().toString());
					bean.setArea(jo4.getString("area"));

					JSONObject jo5 = jo4.getJSONObject("order");
					JSONObject jo6 = jo4.getJSONObject("ws_code");
					JSONObject jo7 = jo4.getJSONObject("product");

					bean.setOrderCount(jo5.getString("count"));
					bean.setWsCodeCount(jo6.getString("count"));
					bean.setProductCount(jo7.getString("count"));

					order_count += Integer.parseInt(bean.getOrderCount());
					listB.add(bean);
				}
				String strEara = "区域 : " + title + " ; 数量 : " + order_count;
				list.add(strEara);
				listBeans.add(listB);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	/**
	 * 获取一票多件货架列表的数据
	 */
	public void getDisMoreShelfList(ArrayList<DisMoreBoxBeanP> listBean) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray jArray = jo.getJSONArray("data");
			for (int i = 0; i < jArray.length(); i++) {
				DisMoreBoxBeanP beanP = new DisMoreBoxBeanP();
				String shelfNum = jArray.getJSONObject(i).getString("ws_code");
				beanP.setShelfNum(shelfNum);
				JSONArray jArray2 = jArray.getJSONObject(i)
						.getJSONArray("data");

				ArrayList<DisMoreBoxBean> list = new ArrayList<DisMoreBoxBean>();
				for (int j = 0; j < jArray2.length(); j++) {
					DisMoreBoxBean bean = new DisMoreBoxBean();
					bean.setBarcode(jArray2.getJSONObject(j).getString(
							"barcode"));
					bean.setOpm_quantity(jArray2.getJSONObject(j).getString(
							"opm_quantity"));
					bean.setSort_number(jArray2.getJSONObject(j).getString(
							"sort_number"));
					bean.setPic(jArray2.getJSONObject(j).getString("pic"));
					list.add(bean);
				}
				beanP.setListBean(list);
				listBean.add(beanP);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 一票多件多个sku获得添加异常成功时候的返回值
	 */
	public String getDisMoreExceptionData(ArrayList<DisMoreItemBean> listBean) {
		String str = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			str = jo.getString("msg");
			JSONObject jo1 = jo.getJSONObject("data");
			Iterator<?> it = jo1.keys();
			while (it.hasNext()) {
				DisMoreItemBean bean = new DisMoreItemBean();
				String location = it.next().toString();
				bean.setLocation(location);
				bean.setCount(jo1.getString(location));
				listBean.add(bean);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 一票多件单个sku获得添加异常成功时候的返回值
	 */
	public String getDisManyOneExceptionData() {
		String str = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			str = jo.getString("msg");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 一票一件获得配货冲突的值
	 */
	public String getManyOneConflictCount() {
		String count = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			if (!jo.getJSONObject("data").isNull("undoQty")) {
				JSONObject joX = jo.getJSONObject("data")
						.getJSONArray("undoQty").getJSONObject(0);
				String key = joX.keys().next().toString();
				JSONObject joXX = joX.getJSONObject(key);
				count = joXX.getString(joXX.keys().next().toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return count;
	}

	/**
	 * 一票多件多SKU判断是否有货可配
	 * 
	 */
	public boolean isDisMorehave() {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String str = jo.getString("noData");
			if (str.equals("0")) {
				return false;
			}
			return true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 一票多件多SKU获取从配置界面过来的初始数据也就是正常的初始数据
	 */
	public String[] getFirstManyMoreInfo(ArrayList<DisMoreItemBean> listBean) {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONObject joData = jo.getJSONObject("data");

			strs = new String[17];
			strs[15] = "";
			if (!joData.isNull("complete")) {
				strs[15] = joData.getString("complete");
			}
			strs[0] = joData.getString("ws_code");
			strs[1] = joData.getString("opm_time");
			strs[2] = joData.getString("count");
			strs[3] = joData.getString("pickupedOrders");
			strs[4] = joData.getString("orderCount");
			strs[5] = joData.getString("op_code");
			strs[6] = joData.getString("page");
			JSONObject joDataData = joData.getJSONArray("data")
					.getJSONObject(0);
			strs[7] = joDataData.getString("product_id");
			strs[8] = joDataData.getString("opm_quantity");
			strs[9] = joDataData.getString("orders_code");
			strs[10] = joDataData.getString("customer_id");
			strs[11] = joDataData.getString("sku");
			strs[12] = joDataData.getString("barcode");
			strs[13] = joDataData.getString("spic");
			strs[14] = joDataData.getString("product_title");
			strs[16] = joDataData.getString("opm_id");

			JSONArray jddArray = joDataData.getJSONArray("sort_number");
			for (int i = 0; i < jddArray.length(); i++) {
				DisMoreItemBean bean = new DisMoreItemBean();
				bean.setLocation(jddArray.getJSONObject(i).getString(
						"sort_number"));
				bean.setCount(jddArray.getJSONObject(i).getString("qty"));
				bean.setOrders_code(jddArray.getJSONObject(i).getString(
						"orders_code"));
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return strs;
	}

	/**
	 * 一票多件多SKU点击已配货、确认配货、跳过获取的数据
	 */
	public String[] getDisManyMoreInfo(ArrayList<DisMoreItemBean> listBean) {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			// 没有下一条数据了
			String noData = jo.getString("noData");
			if (noData.equals("1")) {
				strs = new String[1];
				// 如果data是空字符
				if (jo.getString("data").equals("")) {
					strs[0] = "";
					return strs;
				}
				// 如果data是对象
				JSONObject joN = jo.getJSONObject("data");
				if (!joN.isNull("op_code")) {
					strs[0] = joN.getString("op_code");
				}
				return strs;
			}
			// 正常
			JSONObject joData = jo.getJSONObject("data");

			strs = new String[17];
			strs[15] = "";
			if (!joData.isNull("complete")) {
				strs[15] = joData.getString("complete");
			}

			strs[0] = joData.getString("ws_code");
			strs[1] = joData.getString("opm_time");
			strs[2] = joData.getString("count");
			strs[3] = joData.getString("pickupedOrders");
			strs[4] = joData.getString("orderCount");
			strs[5] = joData.getString("op_code");
			strs[6] = joData.getString("page");
			JSONObject joDataData = joData.getJSONArray("data")
					.getJSONObject(0);
			strs[7] = joDataData.getString("product_id");
			strs[8] = joDataData.getString("opm_quantity");
			strs[9] = joDataData.getString("orders_code");
			strs[10] = joDataData.getString("customer_id");
			strs[11] = joDataData.getString("sku");
			strs[12] = joDataData.getString("barcode");
			strs[13] = joDataData.getString("spic");
			strs[14] = joDataData.getString("product_title");
			strs[16] = joDataData.getString("opm_id");
			JSONArray jddArray = joDataData.getJSONArray("sort_number");
			for (int i = 0; i < jddArray.length(); i++) {
				DisMoreItemBean bean = new DisMoreItemBean();
				bean.setLocation(jddArray.getJSONObject(i).getString(
						"sort_number"));
				bean.setCount(jddArray.getJSONObject(i).getString("qty"));
				bean.setOrders_code(jddArray.getJSONObject(i).getString(
						"orders_code"));
				listBean.add(bean);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return strs;
	}

	/**
	 * 获取一票多件多SKU添加异常的数据
	 */
	public String getDisMoreException(ArrayList<DisMoreItemBean> listBean) {
		String str = "";
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			str = jo.getString("msg");
			JSONObject joData = jo.getJSONObject("data");
			Iterator<?> it = joData.keys();
			while (it.hasNext()) {
				DisMoreItemBean bean = new DisMoreItemBean();
				String location = it.next().toString();
				bean.setLocation(location);
				bean.setCount(joData.getString(location));
				listBean.add(bean);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 一票多件提交status=0时获得最后数据
	 */
	public String[] getCommitResult(ArrayList<String> listStr) {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String strData = jo.getString("data");
			if (strData.equals("")) {
				return strs;
			}
			JSONObject joData = jo.getJSONObject("data");
			JSONObject joX = joData.getJSONObject("unfinishedOrders");
			Iterator<?> it = joX.keys();
			while (it.hasNext()) {
				listStr.add(joX.getString(it.next().toString()));
			}
			strs = new String[6];
			strs[0] = joData.getString("op_code");
			strs[1] = joData.getString("order_type");
			strs[2] = joData.getString("end_time");
			strs[3] = joData.getString("sortBy");
			strs[4] = joData.getString("pickupedOrders");
			strs[5] = joData.getString("orderCount");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 一票多件多SKU返回结果前奏判断，如果complete为0则需要回传数据回去，如果complee为1则说明分配口都放满了
	 */
	public String[] firstDisManyMoreData(ArrayList<String> listStr) {
		String[] strs = null;
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			String str = jo.getString("data");
			if (str.equals("")) {
				return strs;
			}
			JSONObject joData = jo.getJSONObject("data");
			if (joData.isNull("complete")) {
				return strs;
			}
			String strComplete = joData.getString("complete");
			if (strComplete.equals("0")) {
				JSONObject joX = joData.getJSONObject("unfinishedOrders");
				Iterator<?> it = joX.keys();
				while (it.hasNext()) {
					listStr.add(joX.getString(it.next().toString()));
				}
				strs = new String[6];
				strs[0] = joData.getString("op_code");
				strs[1] = joData.getString("order_type");
				strs[2] = joData.getString("end_time");
				strs[3] = joData.getString("sortBy");
				strs[4] = joData.getString("pickupedOrders");
				strs[5] = joData.getString("orderCount");
				return strs;
			}
			if (strComplete.equals("1")) {
				return new String[0];
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return strs;

	}

	/**
	 * 获取货架分区
	 */
	public void getDisArea(ArrayList<DisBean> listBean) {
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray jarray = jo.getJSONArray("data");
			for (int i = 0; i < jarray.length(); i++) {
				DisBean bean = new DisBean();
				bean.setAbo_name(jarray.getJSONObject(i).getString("abo_name"));
				bean.setAbo_id(jarray.getJSONObject(i).getString("abo_id"));
				listBean.add(bean);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
