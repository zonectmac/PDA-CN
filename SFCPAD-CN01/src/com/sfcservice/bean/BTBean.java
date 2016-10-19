package com.sfcservice.bean;


public class BTBean {
	private String holdCount;
	private String sku;
	private String count;
	private String pic;
	private String productId;
	private String putawayLotNumber;
	private String status = "";

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPutawayLotNumber() {
		return putawayLotNumber;
	}

	public void setPutawayLotNumber(String putawayLotNumber) {
		this.putawayLotNumber = putawayLotNumber;
	}

	public String getHoldCount() {
		return holdCount;
	}

	public void setHoldCount(String holdCount) {
		this.holdCount = holdCount;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}
}
