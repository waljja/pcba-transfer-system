package com.ht.vo;

public class SendRecDataVo {
	
	private String Wo;
	private String WoQty;
	private String Pn;
	private String UID;
	private String Location;
	private String sendLocation;
	private String RecLocation;
	private String Factory;
	private String Batch;
	private String Qty;
	private String workcenter;
	private String User;
	private String REF_DOC;
	private String REF_DOC_YR;
	private String Plant;
	private String CreateTime;
	
	
	
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getPlant() {
		return Plant;
	}
	public void setPlant(String plant) {
		Plant = plant;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getREF_DOC() {
		return REF_DOC;
	}
	public void setREF_DOC(String rEF_DOC) {
		REF_DOC = rEF_DOC;
	}
	public String getREF_DOC_YR() {
		return REF_DOC_YR;
	}
	public void setREF_DOC_YR(String rEF_DOC_YR) {
		REF_DOC_YR = rEF_DOC_YR;
	}
	public String getSendLocation() {
		return sendLocation;
	}
	public void setSendLocation(String sendLocation) {
		this.sendLocation = sendLocation;
	}
	public String getWorkcenter() {
		return workcenter;
	}
	public void setWorkcenter(String workcenter) {
		this.workcenter = workcenter;
	}
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public String getWoQty() {
		return WoQty;
	}
	public void setWoQty(String woQty) {
		WoQty = woQty;
	}
	public String getFactory() {
		return Factory;
	}
	public void setFactory(String factory) {
		Factory = factory;
	}
	public String getWo() {
		return Wo;
	}
	public void setWo(String wo) {
		Wo = wo;
	}
	public String getPn() {
		return Pn;
	}
	public void setPn(String pn) {
		Pn = pn;
	}
	public String getRecLocation() {
		return RecLocation;
	}
	public void setRecLocation(String recLocation) {
		RecLocation = recLocation;
	}
	public String getBatch() {
		return Batch;
	}
	public void setBatch(String batch) {
		Batch = batch;
	}
	public String getQty() {
		return Qty;
	}
	public void setQty(String qty) {
		Qty = qty;
	}
	@Override
	public String toString() {
		return "SendRecDataVo [Wo=" + Wo + ", WoQty=" + WoQty + ", Pn=" + Pn
				+ ", UID=" + UID + ", Location=" + Location + ", sendLocation="
				+ sendLocation + ", RecLocation=" + RecLocation + ", Factory="
				+ Factory + ", Batch=" + Batch + ", Qty=" + Qty
				+ ", workcenter=" + workcenter + ", User=" + User
				+ ", REF_DOC=" + REF_DOC + ", REF_DOC_YR=" + REF_DOC_YR
				+ ", Plant=" + Plant + ", CreateTime=" + CreateTime + "]";
	}
	
	 

}
