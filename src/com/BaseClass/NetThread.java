package com.BaseClass;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetThread {
	static int timeout = 20000;
	
	public static class GetSerListThread extends Thread{
		Handler tHandler;
		int tWhere;
		String strAppID;
		/**
		 * 获取访问地址
		 * @param handler Handler
		 * @param Where
		 */
		public GetSerListThread(Handler handler,int Where,String strAppID){
			tHandler = handler;
			tWhere = Where;
			this.strAppID = strAppID;
		}
		@Override
		public void run() {
			super.run();
			String str = "";
			try {
				str = WebService.SoapGetServer(Config.url, Config.nameSpace, Config.methodGetSerList,strAppID, timeout);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = str;
				tHandler.sendMessage(msg);
			}
		}
	}
	
	public static class LoginThread extends Thread {
		String url;
		String tMethodName;
		String tLoginName;
		String tLoginPws;
		Handler tHandler;
		int tWhere;
		public LoginThread(String url,String methodname,String LoginName,String LoginPws,Handler handler,int where){
			this.url = url;
			tMethodName = methodname;
			tLoginName = LoginName;
			tLoginPws = LoginPws;
			tHandler = handler;
			tWhere = where;
		}
		public void run() {
			SoapObject result = null;
			try {
				result = WebService.SoapResponse(url, Config.nameSpace, tMethodName,tLoginName,tLoginPws,timeout);
				System.out.println("result: " + result);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = result;
				tHandler.sendMessage(msg);
			}
		}
	}
	
	public static class TotalThread extends Thread{
		String url;
		String tStrGroupCode;
		Handler tHandler;
		int tWhere;
		/**
		 * 统计信息线程
		 * @param strGroupCode
		 * @param handler
		 */
		public TotalThread(String url,String strGroupCode,Handler handler,int where){
			this.url = url;
			tStrGroupCode = strGroupCode;
			tHandler = handler;
			tWhere = where;
		}
		@Override
		public void run() {
			super.run();
			try {
				String str = WebService.SoapTotal(url,Config.nameSpace, Config.methodTotal,tStrGroupCode, timeout);
				Message message = new Message();
				message.what = tWhere;
				message.obj = str;
				tHandler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 查询所有数据
	 * @author honesty
	 */
	public static class SearchThread extends Thread {
		String url;
		String tStrGroupCode;
		Handler tHandler;
		int tWhere;
		/**
		 * 查询所有车辆信息
		 * @param strGroupCode
		 * @param handler
		 */
		public SearchThread(String url,String strGroupCode ,Handler handler,int where){
			this.url = url;
			tStrGroupCode = strGroupCode;
			tHandler = handler;
			tWhere = where;
		}
		public void run() {
			String strAllCarInfo = null;
			try {
				strAllCarInfo = WebService.SoapRespStr(url,Config.nameSpace, Config.methodNameSearch,tStrGroupCode, timeout);
			}catch (Exception ee) {
				ee.printStackTrace();
			}finally{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = strAllCarInfo;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	}
	
	/**
	 * 开启线程验证账号密码时候匹配
	 * @author honesty
	 *
	 */
	public static class LoginThread1 extends Thread {
		String url;
		String tMethodName;
		String tLoginName;
		String tLoginPws;
		Handler tHandler;
		public LoginThread1(String url,String methodname,String LoginName,String LoginPws,Handler handler){
			this.url = url;
			tMethodName = methodname;
			tLoginName = LoginName;
			tLoginPws = LoginPws;
			tHandler = handler;
		}
		public void run() {
			//调用webservice
			try {
				SoapObject result = WebService.SoapResponse(url, Config.nameSpace, tMethodName,tLoginName,tLoginPws,timeout);
				if(result == null){
					Message msg = new Message();
					msg.what = Config.ID_USER;
					tHandler.sendMessage(msg); // 向Handler发送账户密码不匹配消息,更新UI
				}else{
					SoapPrimitive UserId = (SoapPrimitive)result.getProperty(0);
					Message msg = new Message();
					msg.what = Config.CHANGE_PWD_YZ_OK;
					msg.arg1 = Integer.valueOf(UserId.toString());
					tHandler.sendMessage(msg); // 向Handler发送验证成功消息,更新UI
				}
			}catch (Exception ee) {
				ee.printStackTrace();
				Message msg = new Message();
				msg.what = Config.TIME_OUT;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
			
		}
	}
	
	/**
	 * 开始修改密码
	 * @author honesty
	 *
	 */
	public static class ChangePwdThread extends Thread {
		String url;
		int tUserid;
		String tNewPwd;
		Handler tHandler;
		int tWhere;
		public ChangePwdThread(String url,int userid,String newpwd,Handler handler,int where){
			this.url = url;
			tUserid = userid;
			tNewPwd = newpwd;
			tHandler = handler;
			tWhere = where;
		}
		public void run() {
			String result = null;
			try {
				result = WebService.SoapChangePwd(url, Config.nameSpace, Config.methodNameUpdatePwd,tUserid,tNewPwd,timeout);
			}catch (Exception ee) {
				ee.printStackTrace();
			}finally{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = result;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	}
	
	/**
	 * 单次点名
	 * 
	 * @author honesty
	 * 
	 */
	public static class DCDMThread extends Thread {
		String url;
		String tCar_Id;
		int tUserId;
		Handler tHandler;
		public DCDMThread(String url,String Car_Id,int userid,Handler handler){
			this.url = url;
			tCar_Id = Car_Id;
			tUserId = userid;
			tHandler = handler;
		}
		public void run() {
			try {
				SoapObject result = WebService.SoapRespStrDCDM(url,Config.nameSpace, Config.methodNameCmd, tCar_Id, "Q1","123", tUserId, timeout);
				String sendFlag = ((SoapPrimitive) result.getProperty(1)).toString();
				if (sendFlag.equals("1")) {
					Message message = new Message();
					message.what = Config.CMDOK;
					tHandler.sendMessage(message);
				} else {
					Message message = new Message();
					message.what = Config.CMDFALSE;
					tHandler.sendMessage(message);
				}
			}catch (Exception ee) {
				ee.printStackTrace();
				Message msg = new Message();
				msg.what = Config.TIME_OUT;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	};
	
	/**
	 * 轨迹查询
	 * 
	 * @author honesty
	 * 
	 */
	public static class locusThread implements Runnable {
		String url;
		String tCar_id;
		String tStartTime;
		String tStopTime;
		Handler tHandler;
		int tWhere;
		public locusThread(String url,String Car_id,String startTime,String stopTime,Handler hander,int where){
			this.url = url;
			tCar_id = Car_id;
			tStartTime = startTime;
			tStopTime = stopTime;
			tHandler = hander;
			tWhere = where;
		}
		public void run() {
			String result = null;
			try {
				result = WebService.locusSoapResponse(url,Config.nameSpace, "GetTracks", tCar_id, tStartTime,tStopTime, timeout);
			}catch (Exception ee) {
				ee.printStackTrace();
			}finally{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = result;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	}
	public static class verThread implements Runnable {
		String url;
		String tName;
		String tPwd;
		String tMethodName;
		int tWhith;
		Handler tHandler;
		/**
		 * 验证用户线程（断油断点，来油来电）
		 * @param name  账号
		 * @param pwd   密码
		 * @param whith 断 或者 来
		 * @param methodname 方法名称
		 * @param handler
		 */
		public verThread(String url,String name, String pwd, int whith,String methodname,Handler handler) {
			this.url = url;
			tName = name;
			tPwd = pwd;
			tWhith = whith;
			tMethodName = methodname;
			tHandler = handler;
		}
		public void run() {
			try {
				SoapObject result = WebService.SoapResponse(url,Config.nameSpace, tMethodName, tName, tPwd, timeout);
				if (result == null) {
					Message message = new Message();
					message.what = Config.USERID;
					tHandler.sendMessage(message);
				} else {
					if (tWhith == Config.VEROK) {
						//断油断点
						Message message = new Message();
						message.what = Config.VEROK;
						tHandler.sendMessage(message);
					} else {
						//来由来电
						Message message = new Message();
						message.what = Config.VERCANCLE;
						tHandler.sendMessage(message);
					}
				}
			}catch (Exception ee) {
				ee.printStackTrace();
				Message msg = new Message();
				msg.what = Config.TIME_OUT;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	}
	public static class FixLocation implements Runnable{
		String url;
		double tLon;
		double tLat;
		int tWhere;
		int tGpsOrLocation;
		Handler tHandler;
		public FixLocation(String url,double Lon,double Lat,int where,Handler handler,int GpsOrLocation){
			this.url = url;
			tLon = Lon;
			tLat = Lat;
			tWhere = where;
			tHandler = handler;
			tGpsOrLocation = GpsOrLocation;
		}
		public void run() {
			try {
				Object result = WebService.SoapFixValue(url, Config.nameSpace, "FixValue", tLon, tLat, timeout);
				String[] FixValue = result.toString().split(",");
				String FixLon = FixValue[0].substring(FixValue[0].indexOf("=")+1, FixValue[0].length());
				String FixLat = FixValue[1].substring(0, FixValue[1].indexOf(";"));
				double lon = tLon + Double.valueOf(FixLon);
				double lat = tLat + Double.valueOf(FixLat);
				Message msg = new Message();
				msg.arg1 = tGpsOrLocation;
				msg.what = tWhere;
				msg.obj = lon+","+lat;
				tHandler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = tWhere;
				msg.arg1 = tGpsOrLocation;
				msg.obj = tLon+","+tLat;
				tHandler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	}
	
	public static class GetPoi implements Runnable{
		String url;
		int tUserid;
		Handler tHandler;
		int tWhere;
		/**
		 * 获取热点
		 * @param userid
		 * @param handler
		 * @param where
		 */
		public GetPoi(String url,int userid,Handler handler,int where){
			this.url = url;
			this.tUserid = userid;
			tHandler = handler;
			tWhere = where;
		}
		public void run() {
			String result = null;
			try {
				result = WebService.SoapGetPoi(url, Config.nameSpace, Config.methodGetPoi, tUserid, 30000);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = result;
				tHandler.sendMessage(msg);
			}
		}
	}
	
	public static class GetLocation implements Runnable{
		String tLat;
		String tLon;
		Handler tHandler;
		int tWhere;

		Context mContext;
		/**
		 * 获取地址
		 * @param Lat
		 * @param Lon
		 * @param handler
		 */
		public GetLocation(String Lat, String Lon,Handler handler,int where,Context context) {
			tLat = Lat;
			tLon = Lon;
			tHandler = handler;
			tWhere = where;
			mContext = context;
		}

		public void run() {
			String Address = AllStaticClass.geocodeAddr(mContext,tLat, tLon);
			if(Address == null || Address.equals("")){
				System.out.println("没找到地址");
			}else{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = Address;
				tHandler.sendMessage(msg);
			}
		}
	}
	
	public static class postDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> params;
		public postDataThread(Handler handler,String url,List<NameValuePair> params,int what){
			this.handler = handler;
			this.url = url;
			this.what = what;
			this.params = params;
		}
		@Override
		public void run() {
			super.run();
			HttpPost httpPost = new HttpPost(url);
			try {
				 httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				 HttpClient client = new DefaultHttpClient();
				 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
				 HttpResponse httpResponse = client.execute(httpPost);
				 if(httpResponse.getStatusLine().getStatusCode() == 200){
					 String strResult = EntityUtils.toString(httpResponse.getEntity());
					 System.out.println(strResult);
				 }else{
					 Log.d("NetThread", "状态" +httpResponse.getStatusLine().getStatusCode());
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}