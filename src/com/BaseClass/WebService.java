package com.BaseClass;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebService {
	/**
	 * 验证用户方法
	 * @param url
	 * @param nameSpace
	 * @param methodName
	 * @param name
	 * @param pwd
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static SoapObject SoapResponse(String url , String nameSpace ,String methodName ,String name ,String pwd , int timeout) throws Exception{
		System.out.println(url);
		System.out.println(nameSpace);
		System.out.println(methodName);
		System.out.println(name);
		System.out.println(pwd);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("p_strUserName", name);
    	request.addProperty("p_strPassword", pwd);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,timeout);
    	ht.call(nameSpace + methodName, envelope);
		SoapObject result = (SoapObject)envelope.getResponse();
		System.out.println(envelope.bodyIn.toString());
		return result;
	}
	//修改密码
	public static String SoapChangePwd(String url , String nameSpace ,String methodName ,int userid ,String pwd , int timeout) throws Exception{
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("p_intUserID", userid);
    	request.addProperty("p_strPassword", pwd);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,timeout);
    	ht.call(nameSpace + methodName, envelope);
		String result = envelope.bodyIn.toString();
		return result;
	}
	public static String SoapRespStr(String url , String nameSpace ,String methodName , String strGroupCode, int timeout) throws Exception{
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("p_strGroupCode", strGroupCode);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,30000);
    	ht.call(nameSpace + methodName, envelope);
		return envelope.bodyIn.toString();
	}
	public static String SoapRespGpsStr(String url , String nameSpace ,String methodName , String car_id, int timeout) throws Exception{
		System.out.println(methodName);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("p_strObjectID", car_id);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,30000);
    	ht.call(nameSpace + methodName, envelope);
		return envelope.bodyIn.toString();
	}
	
	public static String locusSoapResponse(String url , String nameSpace ,String methodName ,String car_id ,String StartTime ,String StopTime , int timeout) throws Exception{
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("p_strGroupCode", car_id);
    	request.addProperty("p_dtStartTime", StartTime);
    	request.addProperty("p_dtEndTime", StopTime);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,timeout);
    	ht.call(nameSpace + methodName, envelope);
		SoapObject result = (SoapObject)envelope.getResponse();
		return result.toString(); 
    }
	/*单次点名*/
	public static SoapObject SoapRespStrDCDM(String url , String nameSpace ,String methodName , String strObjectID ,String cmd_id, String phoneNum,int userid, int timeout) throws Exception{
		System.out.println(strObjectID);
		System.out.println(userid);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("p_strObjectID", strObjectID);
    	request.addProperty("p_strProtocol", "HQ");
    	request.addProperty("p_strCmdType", cmd_id);
    	if(cmd_id.equals("C5")){
    		request.addProperty("p_strParams", phoneNum);
    	}else{
    		request.addProperty("p_strParams", "");
    	}
    	request.addProperty("p_strRemark", "");
    	request.addProperty("p_intUserID", userid);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,30000);
    	ht.call(nameSpace + methodName, envelope);
    	System.out.println(envelope.bodyIn.toString());
		return (SoapObject) envelope.getResponse();
	}
	/**
	 * 读取地址
	 * @param url
	 * @param nameSpace
	 * @param methodName
	 * @param strAppID
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String SoapGetServer(String url , String nameSpace ,String methodName ,String strAppID, int timeout) throws Exception{
		SoapObject request = new SoapObject(nameSpace, methodName);
    	request.addProperty("strAppID", strAppID);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,timeout);
    	ht.call(nameSpace + methodName, envelope);
    	String result = envelope.bodyIn.toString();
		return result;
	}	
	//反馈意见
	public static String SoapAddFeedback(String url , String nameSpace ,String methodName , int user_id , String feedBack, int timeout) throws Exception{
		System.out.println(url);
		System.out.println(nameSpace);
		System.out.println(methodName);
		System.out.println(user_id);
		System.out.println(feedBack);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	request.addProperty("p_intUserID", user_id);
    	request.addProperty("p_strFeedback", feedBack);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,timeout);
    	ht.call(nameSpace + methodName, envelope);
    	String result = envelope.bodyIn.toString();
    	System.out.println(result);
		return result;
	}
	public static String SoapGetPoi(String url , String nameSpace ,String methodName , int user_id ,int timeout ) throws Exception{
		System.out.println(url);
		System.out.println(nameSpace);
		System.out.println(methodName);
		System.out.println(user_id);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	request.addProperty("intUserID", user_id);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,timeout);
    	ht.call(nameSpace + methodName, envelope);
    	String result = envelope.bodyIn.toString();
		return result;
	}
	/**
	 * 返回统计数据
	 * @param url
	 * @param nameSpace
	 * @param methodName
	 * @param strGroupCode
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String SoapTotal(String url , String nameSpace ,String methodName , String strGroupCode, int timeout) throws Exception{
		System.out.println(strGroupCode);
		System.out.println(methodName);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("strGrouopCode", strGroupCode);
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,30000);
    	ht.call(nameSpace + methodName, envelope);
    	System.out.println(envelope.bodyIn);
		return envelope.bodyIn.toString();
	}
	
	public static Object SoapFixValue(String url , String nameSpace ,String methodName , double Lon, double Lat, int timeout) throws Exception{
		System.out.println(Lon);
		System.out.println(Lat);
		//初始化SOAP对象
    	SoapObject request = new SoapObject(nameSpace, methodName);
    	//调用参数
    	request.addProperty("lon", String.valueOf(Lon));
    	request.addProperty("lat", String.valueOf(Lat));
    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.setOutputSoapObject(request);
    	envelope.dotNet = true;
    	HttpTransportSE ht = new HttpTransportSE(url,30000);
    	ht.call(nameSpace + methodName, envelope);
    	System.out.println(envelope.bodyIn);
		return envelope.bodyIn;
	}
}
