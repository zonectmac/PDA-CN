package com.sfcservice.bean;

public class DisMoreBoxBean {
//	货架号1'=>array(
//	'ws_code' => '货架号1'
//	'data' => array(
//		array(
//			'barcode' => '客户产品号',
//			'opm_quantity' => '此产品在此分配口下的数量',
//			'sort_number' => '分配口',
//			'pic' => '产品图片'
//		)
//		array(
//			'barcode' => '客户产品号',
//			'opm_quantity' => '此产品在此分配口下的数量',
//			'sort_number' => '分配口',
//			'pic' => '产品图片'
//		)
//		.....
//	)
//)
	private String barcode;
	private String opm_quantity;
	private String sort_number;
	private String pic;
	
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getOpm_quantity() {
		return opm_quantity;
	}
	public void setOpm_quantity(String opm_quantity) {
		this.opm_quantity = opm_quantity;
	}
	public String getSort_number() {
		return sort_number;
	}
	public void setSort_number(String sort_number) {
		this.sort_number = sort_number;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
}
