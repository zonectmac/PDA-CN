package com.sfcservice.bean;

import java.util.ArrayList;

public class DisMoreBoxBeanP {
	private ArrayList<DisMoreBoxBean> listBean=null;
	public ArrayList<DisMoreBoxBean> getListBean() {
		return listBean;
	}
	public void setListBean(ArrayList<DisMoreBoxBean> listBean) {
		this.listBean = listBean;
	}
	public String getShelfNum() {
		return ShelfNum;
	}
	public void setShelfNum(String shelfNum) {
		ShelfNum = shelfNum;
	}
	private String ShelfNum;
}
