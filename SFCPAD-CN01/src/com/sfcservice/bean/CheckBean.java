package com.sfcservice.bean;

public class CheckBean {
	private String productSku;
	private String containerCode;
	private String status;
	private String pic;
	private String usable;
	private String start;
	private String pda;

	public String getPda() {
		return pda;
	}

	public void setPda(String pda) {
		this.pda = pda;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getProductSku() {
		if (productSku == null) {
			return "";
		}
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getContainerCode() {
		if (containerCode == null) {
			return "";
		}
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public String getPic() {
		if (pic == null) {
			return "";
		}
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getStatus() {
		if (status == null) {
			return "";
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsable() {
		if (usable == null) {
			return "";
		}
		return usable;
	}

	public void setUsable(String usable) {
		this.usable = usable;
	}
}
