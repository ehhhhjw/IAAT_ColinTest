package model;

import java.io.Serializable;

public class FrontDevice implements Serializable{
	private String udid,os,deviceName;
	private int devStatus;
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public int getDevStatus() {
		return devStatus;
	}
	public void setDevStatus(int devStatus) {
		this.devStatus = devStatus;
	}
	
}
