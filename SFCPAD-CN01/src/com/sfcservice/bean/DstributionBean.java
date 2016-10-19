package com.sfcservice.bean;

public class DstributionBean {
	private String pic;
	private String shelfNum;
	private String count;
	private String clientProNum;
	private String ProName;
	private String opmId;
	private String productId;
	private String orders_code;

	public String getOrders_code() {
		return orders_code;
	}

	public void setOrders_code(String orders_code) {
		this.orders_code = orders_code;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getOpmId() {
		return opmId;
	}

	public void setOpmId(String opmId) {
		this.opmId = opmId;
	}

	public String getShelfNum() {
		return shelfNum;
	}

	public void setShelfNum(String shelfNum) {
		this.shelfNum = shelfNum;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getClientProNum() {
		return clientProNum;
	}

	public void setClientProNum(String clientProNum) {
		this.clientProNum = clientProNum;
	}

	public String getProName() {
		return ProName;
	}

	public void setProName(String proName) {
		ProName = proName;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
}
