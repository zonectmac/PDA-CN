package com.sfcservice.pda.offline;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.sfcservice.bean.UnderShelveBean;

public abstract class UnderAdapter extends BaseAdapter {
	private List<UnderShelveBean> list = null;
	private Context context;

	public UnderAdapter(Context context, List<UnderShelveBean> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public UnderShelveBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
