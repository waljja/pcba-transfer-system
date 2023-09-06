package com.ht.vo;

public class LocationInfoVo {
	
	private String SendLocation;
	private String RecLocation;
	
	
	
	public String getSendLocation() {
		return SendLocation;
	}
	public void setSendLocation(String sendLocation) {
		SendLocation = sendLocation;
	}
	public String getRecLocation() {
		return RecLocation;
	}
	public void setRecLocation(String recLocation) {
		RecLocation = recLocation;
	}
	
	
	@Override
	public String toString() {
		return "LocationInfoVo [SendLocation=" + SendLocation
				+ ", RecLocation=" + RecLocation + "]";
	}
	
	

}
