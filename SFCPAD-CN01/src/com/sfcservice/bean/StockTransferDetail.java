package com.sfcservice.bean;

import java.io.Serializable;

public class StockTransferDetail implements Serializable {
	private String shelve_loc_num;// ��λ��
	private String pro_qyt;// ����
	private String pro_sku;// �ͻ���Ʒ��
	private String pro_pic;// ͼƬ��ַ
	private String pro_state;// ״̬��0δ�ã�1���ˣ�2�ٻ�
	private String user_login_id;// ˭����
	private String opm_sortcode;// ���������
	private String product_id;// ��ƷID
	private String opm_id;// ������ID
	private String op_code;// �¼ܵ�����
	private String orders_code;// ������
	private String transfer_container;// ���

	public StockTransferDetail() {
		// TODO Auto-generated constructor stub
	}

	public StockTransferDetail(String shelve_loc_num, String pro_qyt,
			String pro_sku, String pro_pic, String pro_state,
			String user_login_id, String opm_sortcode, String product_id,
			String opm_id, String op_code, String orders_code,
			String transfer_container) {
		this.shelve_loc_num = shelve_loc_num;
		this.pro_qyt = pro_qyt;
		this.pro_sku = pro_sku;
		this.pro_pic = pro_pic;
		this.pro_state = pro_state;
		this.user_login_id = user_login_id;
		this.opm_sortcode = opm_sortcode;
		this.product_id = product_id;
		this.opm_id = opm_id;
		this.op_code = op_code;
		this.orders_code = orders_code;
		this.transfer_container = transfer_container;
	}

	public String getTransfer_container() {
		return transfer_container;
	}

	public void setTransfer_container(String transfer_container) {
		this.transfer_container = transfer_container;
	}

	public String getShelve_loc_num() {
		return shelve_loc_num;
	}

	public void setShelve_loc_num(String shelve_loc_num) {
		this.shelve_loc_num = shelve_loc_num;
	}

	public String getPro_qyt() {
		return pro_qyt;
	}

	public void setPro_qyt(String pro_qyt) {
		this.pro_qyt = pro_qyt;
	}

	public String getPro_sku() {
		return pro_sku;
	}

	public void setPro_sku(String pro_sku) {
		this.pro_sku = pro_sku;
	}

	public String getPro_pic() {
		return pro_pic;
	}

	public void setPro_pic(String pro_pic) {
		this.pro_pic = pro_pic;
	}

	public String getPro_state() {
		return pro_state;
	}

	public void setPro_state(String pro_state) {
		this.pro_state = pro_state;
	}

	public String getUser_login_id() {
		return user_login_id;
	}

	public void setUser_login_id(String user_login_id) {
		this.user_login_id = user_login_id;
	}

	public String getOpm_sortcode() {
		return opm_sortcode;
	}

	public void setOpm_sortcode(String opm_sortcode) {
		this.opm_sortcode = opm_sortcode;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getOpm_id() {
		return opm_id;
	}

	public void setOpm_id(String opm_id) {
		this.opm_id = opm_id;
	}

	public String getOp_code() {
		return op_code;
	}

	public void setOp_code(String op_code) {
		this.op_code = op_code;
	}

	public String getOrders_code() {
		return orders_code;
	}

	public void setOrders_code(String orders_code) {
		this.orders_code = orders_code;
	}

	@Override
	public String toString() {
		return "StockTransferDetail [shelve_loc_num=" + shelve_loc_num
				+ ", pro_qyt=" + pro_qyt + ", pro_sku=" + pro_sku
				+ ", pro_pic=" + pro_pic + ", pro_state=" + pro_state
				+ ", user_login_id=" + user_login_id + ", opm_sortcode="
				+ opm_sortcode + ", product_id=" + product_id + ", opm_id="
				+ opm_id + ", op_code=" + op_code + ", orders_code="
				+ orders_code + ", transfer_container=" + transfer_container
				+ "]";
	}

}
