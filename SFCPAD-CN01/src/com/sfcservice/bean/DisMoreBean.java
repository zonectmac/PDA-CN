package com.sfcservice.bean;

import java.util.ArrayList;

public class DisMoreBean {
	private String product_id;
	private String opm_quantity;
	private String orders_code;
	private String count;
	private String customer_id;
	private String sku;
	private String barcode;
	private String pic;
	private String spic;
	private String product_title;
	private ArrayList<DisMoreItemBean> list;
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getOpm_quantity() {
		return opm_quantity;
	}
	public void setOpm_quantity(String opm_quantity) {
		this.opm_quantity = opm_quantity;
	}
	public String getOrders_code() {
		return orders_code;
	}
	public void setOrders_code(String orders_code) {
		this.orders_code = orders_code;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getSpic() {
		return spic;
	}
	public void setSpic(String spic) {
		this.spic = spic;
	}
	public String getProduct_title() {
		return product_title;
	}
	public void setProduct_title(String product_title) {
		this.product_title = product_title;
	}
	public ArrayList<DisMoreItemBean> getList() {
		return list;
	}
	public void setList(ArrayList<DisMoreItemBean> list) {
		this.list = list;
	}

}
