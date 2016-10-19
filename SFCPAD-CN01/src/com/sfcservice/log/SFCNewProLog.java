package com.sfcservice.log;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sfcservice.bean.NewProBean;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;

/**
 *
 */
public class SFCNewProLog extends Activity implements OnScrollListener,
		OnClickListener, OnItemClickListener {
	/** Called when the activity is first created. */
	private ListView listView;
	private Button btnBack, btnSelect;
	private ArrayList<NewProBean> list20;
	private MyAdaper adaper;
	private TextView tvNoLog, tvLogTitle;
	private TextView footTv;
	private PopupWindow p, pSelect;
	private int STATUS = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_log);
		init();
	}

	public void init() {
		footTv = new TextView(this);
		footTv.setTextColor(0xffff5500);
		footTv.setPadding(10, 10, 10, 10);
		footTv.setGravity(Gravity.CENTER);

		list20 = new ArrayList<NewProBean>();
		tvNoLog = (TextView) findViewById(R.id.tvNoLog);
		tvLogTitle = (TextView) findViewById(R.id.tvLogTitle);

		if (!MyConnection.getMyConnection().getNewProductInfo20(list20, 0)) {
			tvNoLog.setVisibility(View.VISIBLE);
		}
		listView = (ListView) findViewById(R.id.list);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnSelect = (Button) findViewById(R.id.btn_select);
		adaper = new MyAdaper();
		listView.addFooterView(footTv);
		listView.setAdapter(adaper);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(this);
		btnBack.setOnClickListener(this);
		btnSelect.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_select:
			SFCPSelect();
			break;
		case R.id.btn1:
			tvLogTitle.setText("***全部日志***");
			select(0);
			break;
		case R.id.btn2:
			tvLogTitle.setText("***未上传日志***");
			select(1);
			break;
		case R.id.btn3:
			tvLogTitle.setText("***已上传日志***");
			select(2);
			break;
		case R.id.btn4:
			tvLogTitle.setText("***上传失败日志***");
			select(3);
			break;
		default:
			break;
		}
	}

	private void select(int STATUS) {
		this.STATUS = STATUS;
		list20 = new ArrayList<NewProBean>();
		if (!MyConnection.getMyConnection().getNewProductInfo20(list20, STATUS)) {
			tvNoLog.setVisibility(View.VISIBLE);
		}
		adaper.notifyDataSetChanged();

		if (list20.size() != 0) {
			tvNoLog.setVisibility(View.INVISIBLE);
			listView.setSelection(0);
		} else {
			tvNoLog.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (position >= list20.size()) {
			return;
		}
		SFCPopWindow(position);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (firstVisibleItem + visibleItemCount == totalItemCount) {
			if (!MyConnection.getMyConnection().getNewProductInfo20(list20,
					STATUS)) {
				footTv.setText("数据加载完毕");
				return;
			}
			adaper.notifyDataSetChanged();
			footTv.setText("数据正在加载请稍候...");
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

		}
	}

	private class MyAdaper extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list20.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = View.inflate(SFCNewProLog.this,
						R.layout.sfc_log_item, null);
			}
			TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
			TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
			tv1.setText("货架号: " + list20.get(position).getShelfNum());
			tv2.setText("用户名: " + list20.get(position).getUser() + ", "
					+ "状态: " + list20.get(position).getStatus());
			return convertView;
		}

	}

	private TextView tv1 = null, tv2 = null, tv3 = null, tv4 = null,
			tv6 = null, tv7 = null, tvCause = null;
	private View v = null, vSelect = null;

	private void SFCPopWindow(int position) {
		if (p != null) {
			v.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.dialog_enter));
			tv1.setText(list20.get(position).getUser());
			tv2.setText(list20.get(position).getBoxNum());
			tv3.setText(list20.get(position).getStatus());
			tv4.setText(list20.get(position).getShelfNum());
			tv6.setText(list20.get(position).getStorageDate());
			tv7.setText(list20.get(position).getUploadDate());
			if (list20.get(position).getCause() != null) {
				tv3.setText("上传失败");
				tvCause.setVisibility(View.VISIBLE);
				tvCause.setText("原因： " + list20.get(position).getCause());
			} else {
				tvCause.setVisibility(View.GONE);
			}
			p.showAtLocation(findViewById(R.id.line), Gravity.CENTER, 0, 0);
			return;
		}

		v = LayoutInflater.from(this).inflate(R.layout.sfc_dialog_newpro, null);
		v.startAnimation(AnimationUtils
				.loadAnimation(this, R.anim.dialog_enter));

		tv1 = (TextView) v.findViewById(R.id.tv1);
		tv2 = (TextView) v.findViewById(R.id.tv2);
		tv3 = (TextView) v.findViewById(R.id.tv3);
		tv4 = (TextView) v.findViewById(R.id.tv4);
		tv6 = (TextView) v.findViewById(R.id.tv6);
		tv7 = (TextView) v.findViewById(R.id.tv7);
		tvCause = (TextView) v.findViewById(R.id.tvCause);

		tv1.setText(list20.get(position).getUser());
		tv2.setText(list20.get(position).getBoxNum());
		tv3.setText(list20.get(position).getStatus());
		tv4.setText(list20.get(position).getShelfNum());
		tv6.setText(list20.get(position).getStorageDate());
		tv7.setText(list20.get(position).getUploadDate());

		if (list20.get(position).getCause() != null) {
			tv3.setText("上传失败");
			tvCause.setVisibility(View.VISIBLE);
			tvCause.setText("原因： " + list20.get(position).getCause());
		}
		Button btnClose = (Button) v.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				p.dismiss();
			}
		});
		p = new PopupWindow(v, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		p.setFocusable(true);
		p.setBackgroundDrawable(new BitmapDrawable());
		p.setOutsideTouchable(true);
		p.showAtLocation(findViewById(R.id.line), Gravity.CENTER, 0, 0);
	}

	private void SFCPSelect() {
		if (pSelect != null) {
			vSelect.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.top_in));
			pSelect.showAsDropDown(btnSelect);
			return;
		}

		vSelect = LayoutInflater.from(this).inflate(
				R.layout.sfc_log_select_dialog, null);
		vSelect.startAnimation(AnimationUtils
				.loadAnimation(this, R.anim.top_in));

		Button btn1 = (Button) vSelect.findViewById(R.id.btn1);
		Button btn2 = (Button) vSelect.findViewById(R.id.btn2);
		Button btn3 = (Button) vSelect.findViewById(R.id.btn3);
		Button btn4 = (Button) vSelect.findViewById(R.id.btn4);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);

		pSelect = new PopupWindow(vSelect, 100, LayoutParams.WRAP_CONTENT);
		pSelect.setFocusable(true);
		pSelect.setBackgroundDrawable(new BitmapDrawable());
		pSelect.setOutsideTouchable(true);
		pSelect.showAsDropDown(btnSelect);

	}
}
