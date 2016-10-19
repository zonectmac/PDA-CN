package com.sfcservice.bean;

public class UnderShelveBean {
	private String singleTosingle;// 一票单件
	private String singleTomore;// 一票多件
	private String warehouse_id;// 仓库ID

	public String getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(String warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public UnderShelveBean() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "UnderShelveBean [singleTosingle=" + singleTosingle
				+ ", singleTomore=" + singleTomore + "]";
	}

	public UnderShelveBean(String singleTosingle, String singleTomore) {
		this.singleTosingle = singleTosingle;
		this.singleTomore = singleTomore;
	}

	public String getSingleTosingle() {
		return singleTosingle;
	}

	public void setSingleTosingle(String singleTosingle) {
		this.singleTosingle = singleTosingle;
	}

	public String getSingleTomore() {
		return singleTomore;
	}

	public void setSingleTomore(String singleTomore) {
		this.singleTomore = singleTomore;
	}

}
