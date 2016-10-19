package com.sfcservice.pda.home;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sfcservice.bean.DisBean;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class SFCDisArea extends Activity implements OnClickListener,
		OnItemClickListener {
	private ListView listView;
	private Button btnAll, btnBack;
	private ArrayList<DisBean> listBean;
	private MyAdapter adapter;
	int[] imgSelect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sfc_dis_area_dialog);
		init();
	}

	public void init() {
		listView = (ListView) findViewById(R.id.listView);
		btnAll = (Button) findViewById(R.id.btn_all);
		btnBack = (Button) findViewById(R.id.btn_back);
		listBean = MyConfig.getMyConfig().getListDisAll();
		imgSelect = getIntent().getIntArrayExtra("S");
		if (imgSelect == null) {
			imgSelect = new int[listBean.size()];
			for (int i = 0; i < imgSelect.length; i++) {
				imgSelect[i] = 1;
			}
		}
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		btnAll.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (listBean == null) {
				return 0;
			}
			return listBean.size();
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
			View v = LayoutInflater.from(SFCDisArea.this).inflate(
					R.layout.sfc_dis_area_dialog_item, null);
			TextView tv = (TextView) v.findViewById(R.id.tvArea);
			ImageView img = (ImageView) v.findViewById(R.id.imgOption);
			if (imgSelect[position] == 1) {
				img.setImageResource(R.drawable.checked);
			} else {
				img.setImageResource(R.drawable.unchecked);
			}
			tv.setText(listBean.get(position).getAbo_name());
			return v;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			exitDeal();
			break;
		case R.id.btn_all:
			if (btnAll.getText().toString().equals("全选")) {
				for (int i = 0; i < imgSelect.length; i++) {
					imgSelect[i] = 1;
				}
				btnAll.setText("反选");
				adapter.notifyDataSetChanged();
				break;
			}
			if (btnAll.getText().toString().equals("反选")) {
				for (int i = 0; i < imgSelect.length; i++) {
					imgSelect[i] = 0;
				}
				btnAll.setText("全选");
				adapter.notifyDataSetChanged();
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitDeal();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (imgSelect[position] == 0) {
			imgSelect[position] = 1;
			adapter.notifyDataSetChanged();
			return;
		}
		if (imgSelect[position] == 1) {
			imgSelect[position] = 0;
			adapter.notifyDataSetChanged();
			return;
		}
	}
	public void exitDeal(){
		boolean bool = false;
		ArrayList<DisBean> listS = new ArrayList<DisBean>();
		for (int i = 0; i < imgSelect.length; i++) {
			if (imgSelect[i] == 1) {
				listS.add(listBean.get(i));
				bool = true;
			}
		}
		if (!bool) {
			MyTool.toastShow(this, "请至少选择一个配货区域");
			return;
		}
		MyConfig.getMyConfig().setListDisRemain(listS);
		MyConfig.getMyConfig().setInts(imgSelect);
		MyTool.toastShow(this, "保存成功");
		finish();
	}
}
