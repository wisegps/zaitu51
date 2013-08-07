package com.BaseClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.zaitu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;

public class AllStaticClass {

	static int px;
	/**
	 * ������ʾ��Ϣ
	 * @param context
	 * ������
	 * @param rString
	 * R��Դ�ļ�
	 */
	public static void showTost(Context context , int rString){
		Toast.makeText(context, rString, Toast.LENGTH_SHORT).show();
	}
	public static Drawable FindPoiIcon(Context context, String icon){
		//postoffice
		String[] array_poi = {"bank","buildings","convenience_store","fast_food","fuel","hospital","house","mail_post","parking","school"};
		int[] array_Dra_poi = {R.drawable.bank,R.drawable.buildings,R.drawable.convenience_store,R.drawable.fast_food,R.drawable.fuel,R.drawable.hospital,R.drawable.house,R.drawable.mail_post,R.drawable.parking,R.drawable.school};
		for(int i = 0 ; i < array_poi.length ; i++){
			if(array_poi[i].equals(icon)){
				Drawable drawable = context.getResources().getDrawable(array_Dra_poi[i]);
				return drawable;
			}
		}
		Drawable drawable = context.getResources().getDrawable(R.drawable.other);
		return drawable;
	}

	
	
	// ��������Ƿ�����
	public static boolean checkNetwork(Context context) {
		try {
			ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			// mobile 3G Data Network
			State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			// wifi
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			// ���3G�����wifi���綼δ���ӣ��Ҳ��Ǵ�����������״̬ �����Network Setting���� ���û�������������
			if (mobile == State.CONNECTED || mobile == State.CONNECTING)
				return true;
			if (wifi == State.CONNECTED || wifi == State.CONNECTING)
				return true;
			return false;
		} catch (Exception e) {
			return true;
		}
		
	}
	public static BitmapDrawable DrawableBimpMap(Context context ,String GPSFlag , String Alarm,String Rotate,String BoardTime){
		Bitmap car_alert = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_alert);
		Bitmap car_bad = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_bad);
		Bitmap car_off = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_off);
		Bitmap car_on = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_on);
		Bitmap car_out = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_out);
		String str = context.getString(R.string.accon);
		String[] alarm = context.getResources().getStringArray(R.array.alarm);
		int gpsFlag = Integer.valueOf(GPSFlag);
		int rotate = Integer.valueOf(Rotate);
		if(GetTimeDiff(BoardTime) > 10){
			return DrawableBimp(rotate, car_out);
		}else if (gpsFlag%2 != 0) {
			return DrawableBimp(rotate, car_bad);
		}else if(SearchAlarm(Alarm, alarm)){
			return DrawableBimp(rotate, car_alert);
		}else if(SearchAccon(Alarm, str)){
			return DrawableBimp(rotate, car_on);
		}else{
			return DrawableBimp(rotate, car_off);
		}
	}
	public static BitmapDrawable DrawableBimp(int rotate , Bitmap bitmapOrg){
			//��ȡ���ͼƬ�Ŀ�͸�
			int width = bitmapOrg.getWidth(); 
			int height = bitmapOrg.getHeight(); 
			//����Ԥת���ɵ�ͼƬ�Ŀ�Ⱥ͸߶�
			int newWidth = width; 
			int newHeight = height; 
			//System.out.println("new:" + px + "old:" + width);
			//���������ʣ��³ߴ��ԭʼ�ߴ�
			float scaleWidth = ((float) newWidth) / width; 
			float scaleHeight = ((float) newHeight) / height; 
			// ��������ͼƬ�õ�matrix����
			Matrix matrix = new Matrix(); 
			// ����ͼƬ����
			matrix.postScale(scaleWidth, scaleHeight); 
			//��תͼƬ ����
			//matrix.postRotate(rotate); 
			matrix.postRotate(rotate, (float)width/2, (float)height/2);
			// �����µ�ͼƬ
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true); 
			//�����洴����Bitmapת����Drawable����ʹ�������ʹ����ImageView, ImageButton��
			BitmapDrawable bmd = new BitmapDrawable(resizedBitmap); 
			return bmd;
	}
	private static boolean SearchAlarm(String status , String[] alarm){
		for(int i = 0 ; i < alarm.length ; i++){
			if(status.indexOf(alarm[i])>=0){
				return true;
			}
		}
		return false;
	}
	private static boolean SearchAccon(String status ,String str){
		if(status.indexOf(str)>=0){
			return true;
		}
		return false;
	}
	/**
	 * ��֤ʱ���Ƿ��ڷ�Χ��
	 * @param startTime
	 * @param StopTime
	 * @return
	 */
	public static boolean LimitTime(String startTime, String StopTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date begin = sdf.parse(startTime);
			java.util.Date end = sdf.parse(StopTime);
			long between = (end.getTime() - begin.getTime())/(1000*60);
			if(between > (60*24)){
				return false;
			}else if (between < 0) {
				return false;
			}else{
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
     * �����뵱ǰʱ���ʱ�����ӣ�
     * @param �����뵱ǰ��ʱ���
     * @return ���ط���
     */
    public static long GetTimeDiff(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date begin = sdf.parse(time);
			java.util.Date end = sdf.parse(sdf.format(new Date()));
			long between = (end.getTime() - begin.getTime())/(1000*60);
			return between;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public static boolean openGPSSettings(Context context) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }


	/**
	 * ����ʱ���ʽ
	 * @param i
	 * @return
	 */
	public static String intToString(int i) {
		String str = null;
		if (i < 10) {
			str = "0" + i;
		} else {
			str = "" + i;
		}
		return str;
	}
	/**
	 * ��γ��ת��
	 * @param str
	 * @return
	 */
	public static int StringToInt(String str) {
		Double point_doub = Double.parseDouble(str);
		return (int) (point_doub * 1000000);
	}
	
	public static String geocodeAddr(Context context, String latitude, String longitude) { 
		String addr = ""; 
		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+ latitude +"," + longitude + "&sensor=true";
		System.out.println(url);
		URL myURL = null; 
		URLConnection httpsConn = null; 
		try { 
			myURL = new URL(url); 
		} catch (MalformedURLException e) { 
			e.printStackTrace(); 
			return null; 
		} 
		try { 
			httpsConn = (URLConnection) myURL.openConnection(); 
			if (httpsConn != null) { 
				System.out.println("��ʼ��ȡ����");
				InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8"); 
				BufferedReader br = new BufferedReader(insr,1024); 
				String data = ""; 
				String line = "";
				while((line = br.readLine())!= null){
					data += line ;
				}
				insr.close(); 
				return returnAddress(data);
			} 
		} catch (Exception e) { 
			e.printStackTrace(); 
			return null; 
		}
		return addr; 
	} 
	/**
	 * ���ص�ַ
	 * @param str
	 * @return
	 */
	public static String returnAddress(String str){
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			JSONObject temp = (JSONObject)jsonArray.get(0);
			String address = temp.getString("formatted_address");
			String returnAddress;
			if(address.indexOf("��������") > -1){
				returnAddress = address.substring(0, address.indexOf("��������"));
			}else{
				returnAddress = address;
			}
			return returnAddress;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
}