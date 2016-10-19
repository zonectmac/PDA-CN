package com.sfcservice.bean;

public class CommitPickBean {
	private String orders_code;// 订单号
	private String opm_quantity;// 要取的数量与实际取得数量的差值
	private String opm_status;// 订单状态
	private String opm_id;// 订单ID

	public CommitPickBean() {
		// TODO Auto-generated constructor stub
	}

	public CommitPickBean(String orders_code, String opm_quantity,
			String opm_status, String opm_id) {
		this.orders_code = orders_code;
		this.opm_quantity = opm_quantity;
		this.opm_status = opm_status;
		this.opm_id = opm_id;
	}

	public String getOpm_id() {
		return opm_id;
	}

	public void setOpm_id(String opm_id) {
		this.opm_id = opm_id;
	}

	public String getOrders_code() {
		return orders_code;
	}

	public void setOrders_code(String orders_code) {
		this.orders_code = orders_code;
	}

	public String getOpm_quantity() {
		return opm_quantity;
	}

	public void setOpm_quantity(String opm_quantity) {
		this.opm_quantity = opm_quantity;
	}

	public String getOpm_status() {
		return opm_status;
	}

	public void setOpm_status(String opm_status) {
		this.opm_status = opm_status;
	}

}
