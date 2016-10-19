package com.sfcservice.bean;

public class OfflinePickDetail2 {
	private String shelve_loc_num;// ��λ���
	private String pro_sku;// �ͻ���Ʒ��
	private String pro_qyt;// ��Ʒ����
	private String lack_qyt;// ȱ������
	private String pro_name;// ��Ʒ����
	private String pro_pic;// ͼƬ��ַ
	private String pro_state;// �Ƿ��������0δ�䣬1����,2�ٻ�
	private String user_login_id;// ˭���
	private String orders_code;// ������
	private String opm_id;// ������ID
	private String opm_sortcode;// ���������
	private String op_code;// �¼ܵ�
	private String product_id;// ��ƷID
	private String _id;// ����ID

	public OfflinePickDetail2(String shelve_loc_num, String pro_sku,
			String pro_qyt, String lack_qyt, String pro_name, String pro_pic,
			String pro_state, String user_login_id, String orders_code,
			String opm_id, String opm_sortcode, String op_code,
			String product_id, String _id) {
		this.shelve_loc_num = shelve_loc_num;
		this.pro_sku = pro_sku;
		this.pro_qyt = pro_qyt;
		this.lack_qyt = lack_qyt;
		this.pro_name = pro_name;
		this.pro_pic = pro_pic;
		this.pro_state = pro_state;
		this.user_login_id = user_login_id;
		this.orders_code = orders_code;
		this.opm_id = opm_id;
		this.opm_sortcode = opm_sortcode;
		this.op_code = op_code;
		this.product_id = product_id;
		this._id = _id;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getOp_code() {
		return op_code;
	}

	public void setOp_code(String op_code) {
		this.op_code = op_code;
	}

	public String getOpm_id() {
		return opm_id;
	}

	public void setOpm_id(String opm_id) {
		this.opm_id = opm_id;
	}

	public String getLack_qyt() {
		return lack_qyt;
	}

	public void setLack_qyt(String lack_qyt) {
		this.lack_qyt = lack_qyt;
	}

	public String getOrders_code() {
		return orders_code;
	}

	public void setOrders_code(String orders_code) {
		this.orders_code = orders_code;
	}

	public OfflinePickDetail2() {
		// TODO Auto-generated constructor stub
	}

	public String getShelve_loc_num() {
		return shelve_loc_num;
	}

	public void setShelve_loc_num(String shelve_loc_num) {
		this.shelve_loc_num = shelve_loc_num;
	}

	public String getPro_sku() {
		return pro_sku;
	}

	public void setPro_sku(String pro_sku) {
		this.pro_sku = pro_sku;
	}

	public String getPro_qyt() {
		return pro_qyt;
	}

	public void setPro_qyt(String pro_qyt) {
		this.pro_qyt = pro_qyt;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
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

	@Override
	public String toString() {
		return "OfflinePickDetail2 [shelve_loc_num=" + shelve_loc_num
				+ ", pro_sku=" + pro_sku + ", pro_qyt=" + pro_qyt
				+ ", lack_qyt=" + lack_qyt + ", pro_name=" + pro_name
				+ ", pro_pic=" + pro_pic + ", pro_state=" + pro_state
				+ ", user_login_id=" + user_login_id + ", orders_code="
				+ orders_code + ", opm_id=" + opm_id + ", opm_sortcode="
				+ opm_sortcode + ", op_code=" + op_code + ", product_id="
				+ product_id + ", _id=" + _id + "]";
	}

}
