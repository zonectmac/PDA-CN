package com.sfcservice.bean;

import java.util.ArrayList;

public class DisMoreItemBeanException {
	private ArrayList<DisMoreItemBean> list;
	public ArrayList<DisMoreItemBean> getList() {
		return list;
	}
	public void setList(ArrayList<DisMoreItemBean> list) {
		this.list = list;
	}
	private String sku;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
}
