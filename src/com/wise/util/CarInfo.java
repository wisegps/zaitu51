package com.wise.util;

import java.io.Serializable;

public class CarInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String MDTStatus;
	private String ObjectID;
	private String GpsTime;
	private String Lon;
	private String Lat;
	private String Speed;
	private String Mileage;
	private String Direct;
	private String RegNum;
	private String GPSFlag;
	private String Fuel;
	private String Temp;
	private String BoardTime;
	private String StaticTime;
	
	public String getGPSFlag() {
		return GPSFlag;
	}
	public void setGPSFlag(String gPSFlag) {
		GPSFlag = gPSFlag;
	}
	public String getRegNum() {
		return RegNum;
	}
	public void setRegNum(String regNum) {
		RegNum = regNum;
	}
	public String getCar_status() {
		return MDTStatus;
	}
	public void setCar_status(String car_status) {
		this.MDTStatus = car_status;
	}
	public String getCar_id() {
		return ObjectID;
	}
	public void setCar_id(String car_id) {
		this.ObjectID = car_id;
	}
	public String getGps_time() {
		return GpsTime;
	}
	public void setGps_time(String gpsTime) {
		GpsTime = gpsTime;
	}
	public String getLon() {
		return Lon;
	}
	public void setLon(String lon) {
		this.Lon = lon;
	}
	public String getLat() {
		return Lat;
	}
	public void setLat(String lat) {
		this.Lat = lat;
	}
	public String getSpeed() {
		return Speed;
	}
	public void setSpeed(String speed) {
		this.Speed = speed;
	}
	public String getMileage() {
		return Mileage;
	}
	public void setMileage(String mileage) {
		this.Mileage = mileage;
	}
	public String getDirect() {
		return Direct;
	}
	public void setDirect(String direct) {
		this.Direct = direct;
	}
	public String getFuel() {
		return Fuel;
	}
	public void setFuel(String fuel) {
		Fuel = fuel;
	}
	public String getTemp() {
		return Temp;
	}
	public void setTemp(String temp) {
		Temp = temp;
	}
	public String getMDTStatus() {
		return MDTStatus;
	}
	public void setMDTStatus(String mDTStatus) {
		MDTStatus = mDTStatus;
	}
	public String getObjectID() {
		return ObjectID;
	}
	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}
	public String getGpsTime() {
		return GpsTime;
	}
	public void setGpsTime(String gpsTime) {
		GpsTime = gpsTime;
	}
	public String getBoardTime() {
		return BoardTime;
	}
	public void setBoardTime(String boardTime) {
		BoardTime = boardTime;
	}
	public String getStaticTime() {
		return StaticTime;
	}
	public void setStaticTime(String staticTime) {
		StaticTime = staticTime;
	}
	@Override
	public String toString() {
		return "CarInfo [MDTStatus=" + MDTStatus + ", ObjectID=" + ObjectID
				+ ", GpsTime=" + GpsTime + ", Lon=" + Lon + ", Lat=" + Lat
				+ ", Speed=" + Speed + ", Mileage=" + Mileage + ", Direct="
				+ Direct + ", RegNum=" + RegNum + ", GPSFlag=" + GPSFlag
				+ ", Fuel=" + Fuel + ", Temp=" + Temp + ", BoardTime="
				+ BoardTime + ", StaticTime=" + StaticTime + "]";
	}			
}