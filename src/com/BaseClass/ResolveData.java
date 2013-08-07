package com.BaseClass;

import java.util.ArrayList;
import java.util.List;
import com.wise.util.CarInfo;
import com.wise.util.TotalData;

public class ResolveData {
	/**
	 * 解析返回统计数据
	 * @param Str
	 * @return
	 */
	public static TotalData ResolveTotal(String Str){
		String[] ResolveInfo = Str.split("; ");
		TotalData totalData = new TotalData();
		totalData.setAccOn(ResolveInfo[0].substring(54));
		totalData.setAccOff(ResolveInfo[1].substring(7));
		totalData.setToatal(ResolveInfo[8].substring(6));
		return totalData;
	}
	/**
	 * 解析XML文档
	 * @param str
	 webservice返回的XML
	 */
	public static List<CarInfo> parseXML(String str,List<CarInfo> carinfos) throws Exception{
		carinfos.clear();
		String[] strGPSInfo = str.split("GPSInfo=anyType");
		for (int i = 1; i < strGPSInfo.length; i++) {
			CarInfo carInfo = new CarInfo();
			/***************** 解析数据 ********************/
			String[] strCars = strGPSInfo[i].split("; ");

			String gpstime = strCars[0].substring(9);
			if (gpstime.equals("anyType{}")) {
				carInfo.setGps_time("");
			} else {
				carInfo.setGps_time(gpstime);
			}

			String lon = strCars[2].substring(4);
			if (lon.equals("anyType{}")) {
				carInfo.setLon("");
			} else {
				carInfo.setLon(lon);
			}

			String lat = strCars[3].substring(4);
			if (lat.equals("anyType{}")) {
				carInfo.setLat("");
			} else {
				carInfo.setLat(lat);
			}

			String speed = strCars[4].substring(6);
			if (speed.equals("anyType{}")) {
				carInfo.setSpeed("");
			} else {
				carInfo.setSpeed(speed);
			}

			String mileage = strCars[5].substring(8);
			if (mileage.equals("anyType{}")) {
				carInfo.setMileage("");
			} else {
				carInfo.setMileage(mileage);
			}

			String direct = strCars[6].substring(7);
			if (direct.equals("anyType{}")) {
				carInfo.setDirect("");
			} else {
				carInfo.setDirect(direct);
			}

			String status = strCars[7].substring(10);
			if (status.equals("anyType{}")) {
				carInfo.setCar_status("");
			} else {
				carInfo.setCar_status(status);
			}

			carInfo.setCar_id(strCars[8].substring(9));

			String regnum = strCars[9].substring(13);
			if (regnum.equals("anyType{}")) {
				carInfo.setRegNum("");
			} else {
				carInfo.setRegNum(regnum);
			}
			
			String gpsFlag = strCars[11].substring(8);
			if(gpsFlag.equals("anyType{}")){
				carInfo.setGPSFlag("0");
			}else{
				carInfo.setGPSFlag(gpsFlag);
			}
			
			String Temp = strCars[13].substring(12);
			if (Temp.equals("anyType{}")) {
				carInfo.setTemp("");
			} else {
				carInfo.setTemp(Temp);
			}
			
			String Fuel = strCars[12].substring(8);
			if (Fuel.equals("anyType{}")) {
				carInfo.setFuel("");
			} else {
				carInfo.setFuel(Fuel);
			}	
			
			String BoardTime = strCars[1].substring(10);
			if (BoardTime.equals("anyType{}")) {
				carInfo.setBoardTime("");
			} else {
				carInfo.setBoardTime(BoardTime.replace("T", " "));
			}
			String StaticTime = strCars[17].substring(11);
			if (BoardTime.equals("anyType{}")) {
				carInfo.setStaticTime("");
			} else {
				carInfo.setStaticTime(StaticTime);
			}
			carinfos.add(carInfo);
		}
		return carinfos;
	}
	


	/**
	 * 解析轨迹回访记录
	 * @param str
	 */
	public static List<CarInfo> locusParseXML(String str) {
		List<CarInfo> carPath = new ArrayList<CarInfo>();
		String[] strGPSInfo = str.split("GPSInfo=anyType");
		for (int i = 1; i < strGPSInfo.length; i++) {
			CarInfo carInfo = new CarInfo();
			/***************** 解析数据 ********************/
			String[] strCars = strGPSInfo[i].split("; ");

			String gpstime = strCars[0].substring(9);
			if (gpstime.equals("anyType{}")) {
				carInfo.setGps_time("");
			} else {
				carInfo.setGps_time(gpstime);
			}

			String lon = strCars[2].substring(4);
			if (lon.equals("anyType{}")) {
				carInfo.setLon("");
			} else {
				carInfo.setLon(lon);
			}

			String lat = strCars[3].substring(4);
			if (lat.equals("anyType{}")) {
				carInfo.setLat("");
			} else {
				carInfo.setLat(lat);
			}

			String speed = strCars[4].substring(6);
			if (speed.equals("anyType{}")) {
				carInfo.setSpeed("");
			} else {
				carInfo.setSpeed(speed);
			}

			String mileage = strCars[5].substring(8);
			if (mileage.equals("anyType{}")) {
				carInfo.setMileage("");
			} else {
				carInfo.setMileage(mileage);
			}

			String direct = strCars[6].substring(7);
			if (direct.equals("anyType{}")) {
				carInfo.setDirect("");
			} else {
				carInfo.setDirect(direct);
			}

			String status = strCars[7].substring(10);
			if (status.equals("anyType{}")) {
				carInfo.setCar_status("");
			} else {
				carInfo.setCar_status(status);
			}

			carInfo.setCar_id(strCars[8].substring(9));

			String regnum = strCars[9].substring(13);
			if (regnum.equals("anyType{}")) {
				carInfo.setRegNum("");
			} else {
				carInfo.setRegNum(regnum);
			}
			
			String gpsFlag = strCars[11].substring(8);
			if(gpsFlag.equals("anyType{}")){
				carInfo.setGPSFlag("0");
			}else{
				carInfo.setGPSFlag(gpsFlag);
			}
			carPath.add(carInfo);
			System.out.println(carInfo.toString());
		}
		return carPath;
	}
}


//GetGPSCountsResponse{GetGPSCountsResult=anyType{AccOn=56; 
//AccOff=9; 
//Sensor1=1; 
//Sensor2=0; 
//Sos=0; 
//BadGps=8; 
//InActive=26; 
//Alert=7; 
//Total=65; }; }