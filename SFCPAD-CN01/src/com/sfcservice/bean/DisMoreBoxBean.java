package com.sfcservice.bean;

public class DisMoreBoxBean {
//	���ܺ�1'=>array(
//	'ws_code' => '���ܺ�1'
//	'data' => array(
//		array(
//			'barcode' => '�ͻ���Ʒ��',
//			'opm_quantity' => '�˲�Ʒ�ڴ˷�����µ�����',
//			'sort_number' => '�����',
//			'pic' => '��ƷͼƬ'
//		)
//		array(
//			'barcode' => '�ͻ���Ʒ��',
//			'opm_quantity' => '�˲�Ʒ�ڴ˷�����µ�����',
//			'sort_number' => '�����',
//			'pic' => '��ƷͼƬ'
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
