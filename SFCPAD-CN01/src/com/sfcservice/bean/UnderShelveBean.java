package com.sfcservice.bean;

public class UnderShelveBean {
	private String singleTosingle;// һƱ����
	private String singleTomore;// һƱ���
	private String warehouse_id;// �ֿ�ID

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
