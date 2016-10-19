package com.sfcservice.bean;

import java.io.Serializable;

public class DisBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String abo_id;
	private String abo_name;
	public String getAbo_id() {
		return abo_id;
	}
	public void setAbo_id(String abo_id) {
		this.abo_id = abo_id;
	}
	public String getAbo_name() {
		return abo_name;
	}
	public void setAbo_name(String abo_name) {
		this.abo_name = abo_name;
	}
}
