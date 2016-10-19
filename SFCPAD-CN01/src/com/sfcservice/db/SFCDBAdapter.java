package com.sfcservice.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SFCDBAdapter extends SQLiteOpenHelper {

	public SFCDBAdapter(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 这里的外键只是让数据不容易删除,并没有扩展数据表;因为下一次登录的时候
		String sql_user = "create table user(user_login_id text primary key not null, user_tokken text not null,user_key text not null,user_last_update text not null)";
		String sql_cut_sheet_back = "create table cut_sheet_back(_id integer primary key autoincrement,user_login_id text not null,back_num text not null,shelf_num text not null,sku text not null,foreign key(user_login_id) references user(user_login_id));";
		String sql_binding_shelves = "create table binding_shelves(_id integer primary key autoincrement,user_login_id text not null,sku text not null,shelf_num_new text not null,count text not null,count_confirm text not null,foreign key(user_login_id) references user(user_login_id))";
		// 产品上架有三个状态未上传、已上传、上传失败分别对应status 1 2 3,cause只有在status=3的时候才有
		String sql_new_product = "create table new_product(_id integer primary key autoincrement,user_login_id text not null,box_num text not null,shelf_num text not null,storage_date text not null,upload_date text not null,status text not null,cause text,foreign key(user_login_id) references user(user_login_id))";
		// String sql_order_pickup =
		// "CREATE TABLE order_pickup (op_id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,op_code vARCHAR(32)  UNIQUE NULL,user_login_id VARCHAR(32)  NOT NULL,op_order_cnt INTEGER DEFAULT 0 NULL,op_product_cnt INTEGER DEFAULT 0 NULL,op_status INTEGER DEFAULT 0 NULL,op_orders_type INTEGER DEFAULT 0 NULL,op_note VARCHAR(64)  NULL,op_create_date DATE DEFAULT CURRENT_TIMESTAMP NULL,op_last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL)";
		// String sql_order_pickup_map =
		// "CREATE TABLE order_pickup_map (opm_id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,op_code VARCHAR(32)  NOT NULL,orders_code VARCHAR(32)  NOT NULL,opm_sort VARCHAR(8)  NULL,product_sku VARCHAR(16)  NULL,opm_note VARCHAR(64)  NULL,ws_code VARCHAR(32)  NOT NULL,opm_status INTEGER DEFAULT 0 NOT NULL,opm_create_date DATE DEFAULT CURRENT_TIMESTAMP NULL,opm_last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,foreign key(user_login_id) references user(user_login_id))";
		// String sql_stock_transfer_merge =
		// "create table stock_transfer_merge()";
		// String sql_container_shelves_binding =
		// "create table container_shelves_binding()";
		String sql_offline_pickdetail = "create table offline_pickdetail(_id integer primary key autoincrement,user_login_id text not null,op_code text not null,opm_id text not null,shelve_loc_num text not null,pro_sku text not null,orders_code text not null,product_id text not null,pro_qyt text not null,lack_qyt text not null,pro_name text not null,pro_pic text not null,opm_sortcode text not null,pro_state text not null,foreign key(user_login_id) references user(user_login_id))";
		// String sql_stock_transfer_detail =
		// "create table stock_transfer_detail(_id integer primary key autoincrement,user_login_id text not null,op_code text not null,opm_id text not null,shelve_loc_num text not null,pro_sku text not null,orders_code text not null,product_id text not null,pro_qyt text not null,lack_qyt text not null,transfer_container text not null,pro_pic text not null,opm_sortcode text not null,pro_state text not null,foreign key(user_login_id) references user(user_login_id))";
		db.execSQL(sql_user);
		db.execSQL(sql_cut_sheet_back);
		db.execSQL(sql_binding_shelves);
		db.execSQL(sql_new_product);
		db.execSQL(sql_offline_pickdetail);
		// db.execSQL(sql_stock_transfer_detail);
		// db.execSQL(sql_order_pickup);
		// db.execSQL(sql_order_pickup_map);
		// db.execSQL(sql_stock_transfer_merge);
		// db.execSQL(sql_container_shelves_binding);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("version", newVersion + "----" + oldVersion);
		// 数据库不升级
		if (oldVersion >= newVersion) {
			return;
		}
		List<String> sqlUpdateList = new ArrayList<String>();
		for (int i = oldVersion; i < newVersion; i++) {
			if (i == 1) {
				updateTo2(sqlUpdateList, db);
			}

			if (sqlUpdateList.size() == 0) {
				continue;
			}

			db.beginTransaction();
			try {
				for (String string : sqlUpdateList) {
					db.execSQL(string);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("sfcdb", "check..." + e.getMessage());

			} finally {
				db.endTransaction();
			}
		}

	}

	private void updateTo2(List<String> sqlUpdateList, SQLiteDatabase db) {
		StringBuilder sbSql = new StringBuilder();
		String sql_offline_pickdetail = "create table offline_pickdetail(_id integer primary key autoincrement,user_login_id text not null,op_code text not null,opm_id text not null,shelve_loc_num text not null,pro_sku text not null,orders_code text not null,product_id text not null,pro_qyt text not null,lack_qyt text not null,pro_name text not null,pro_pic text not null,opm_sortcode text not null,pro_state text not null,foreign key(user_login_id) references user(user_login_id))";
		sbSql.append(sql_offline_pickdetail);

		sqlUpdateList.add(sbSql.toString());
	}
}
