package com.wise.zaitu;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import com.BaseClass.Config;
import com.BaseClass.NetThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginAvtivity extends Activity{
	
	private final String TAG = "LoginAvtivity"; 
	private final int Login = 3; //登陆成功
	
	/* 控件定义 */
	EditText et_name; // 用户名
	EditText et_pwd; // 密码
	CheckBox cb_isSavePwd; // 是否保存密码
	Button bt_login; // 登陆
	Button bt_exit; // 离开
	TextView tv_update;
	/*全局变量*/
	ProgressDialog Dialog = null;    //progress
	String strGroupCode = null;      //用户组
	int userid;                      //用户编号,发送指令需要
	String LoginName ;               //用户名
	String LoginPws ;                //密码
	String url;                      //
	boolean LoginNote = true;               //是否保存密码
	boolean Map; //选择地图
	double Verson; 	  //版本号，用户判断更新
	String VersonUrl; //下载路径
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		init();
		getSp();
		getDM();
		try {
		    Class.forName("com.google.android.maps.MapActivity");
		} catch (Exception e) {
			e.printStackTrace();
			ExitDialog();
		    return;
		}
	}
	//t1234, 888888
	/**
	 * 提示没有谷歌服务
	 */
	private void ExitDialog(){
		AlertDialog.Builder bulder = new AlertDialog.Builder(LoginAvtivity.this);
		bulder.setTitle(R.string.Note);// 设置标题
		bulder.setMessage(R.string.Google);
		bulder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Login:
				if(Dialog != null){
					Dialog.dismiss();
				}
				try {
					System.out.println("结果：" + msg.obj);
					SoapObject result = (SoapObject) msg.obj;
					if(result == null){
						Toast.makeText(LoginAvtivity.this, R.string.AllCarInfoActivity_login_id_wrong, Toast.LENGTH_LONG).show();
					}else{
						SoapPrimitive UserId = (SoapPrimitive)result.getProperty(0);
						SoapPrimitive GroupCode = (SoapPrimitive) result.getProperty(1);
						userid = Integer.valueOf(UserId.toString());
						strGroupCode = GroupCode.toString();
						Turn();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(LoginAvtivity.this, R.string.AllCarInfoActivity_login_net_outtime, Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};
	
	OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_login:
				//登陆事件
				LoginName = et_name.getText().toString();
				LoginPws = et_pwd.getText().toString();
				if (LoginName.equals("")|| LoginPws.equals("")) {
					Toast.makeText(LoginAvtivity.this, R.string.Login_null, Toast.LENGTH_LONG).show();
				}else{
					Dialog = ProgressDialog.show(LoginAvtivity.this,getString(R.string.AllCarInfoActivity_login_pd_title),
							getString(R.string.AllCarInfoActivity_login_pd_context),true);
					new Thread(new NetThread.LoginThread(url,Config.methodLogin, LoginName, LoginPws, handler, Login)).start();
				}
				break;
			case R.id.bt_cancle:
				finish();
				break;
			}
		}
	};
	/**
	 * 初始化数据
	 */
	private void getSp(){		
		//读取sharedPreferences配置信息
		SharedPreferences preferences = getSharedPreferences("wise", Context.MODE_PRIVATE);
		String LoginName = preferences.getString("LoginName", "");
		String LoginPws = preferences.getString("LoginPws", "");
		LoginNote = preferences.getBoolean("LoginNote", true);
		Map = preferences.getBoolean("Map", true);
		Verson = Double.valueOf(preferences.getString("Verson", "0"));
		VersonUrl = preferences.getString("VersonUrl", "");
		et_name.setText(LoginName);
		et_pwd.setText(LoginPws);
		cb_isSavePwd.setChecked(LoginNote);
		
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		System.out.println(url);
	}
	/**
	 * 初始化控件
	 */
	private void init() {
		et_name = (EditText) findViewById(R.id.UserName_ET);
		et_pwd = (EditText) findViewById(R.id.PassWord_ET);
		cb_isSavePwd = (CheckBox) findViewById(R.id.NotePsw);
		cb_isSavePwd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				LoginNote = cb.isChecked();
			}
		});
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(OCL);
		bt_exit = (Button) findViewById(R.id.bt_cancle);
		bt_exit.setOnClickListener(OCL);
		tv_update = (TextView)findViewById(R.id.tv_update);
	}
	
	
	/**
	 * 跳转页面
	 */
	private void Turn(){
		/*保存用户账号*/
		SharedPreferences preferences = getSharedPreferences("wise", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("LoginName", LoginName);
		editor.putBoolean("LoginNote", LoginNote);
		if(LoginNote){
			editor.putString("LoginPws", LoginPws);
		}else{
			editor.putString("LoginPws", "");
		}
		editor.commit();
		//跳转页面
		Intent intent = new Intent();
		if(Map){
			intent.setClass(LoginAvtivity.this, AVTActivity.class);
		}else{
			intent.setClass(LoginAvtivity.this, AVTBDActivity.class);
		}
		intent.putExtra("strGroupCode", strGroupCode);
		intent.putExtra("LoginName", LoginName);
		intent.putExtra("userid", userid);
		intent.putExtra("url", url);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 根据分辨率，设置字体大小
	 */
	private void getDM(){
		 DisplayMetrics dm = new DisplayMetrics();  
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
	     int with = dm.widthPixels;
	     SharedPreferences preferences = getSharedPreferences("wise", Context.MODE_PRIVATE);
		 Editor editor = preferences.edit();
		 if(with >= 480){
			 editor.putInt("Text_size", 20);
			 editor.commit();
			 return;
	     }else if(with >240){
	    	 editor.putInt("Text_size", 15);
			 editor.commit();
	    	 return;
	     }else{
	    	 editor.putInt("Text_size", 8);
			 editor.commit();
	    	 return;
	     }
	}
	private void isUpdate(){
		if(isSdCardExist()){
			try {
				Log.d(TAG, "Verson:" + Verson + ",VersonUrl:" + VersonUrl);
				//得到系统的版本
				if(Verson>Double.valueOf(GetVersion(getApplicationContext(), Config.MyPackage))){
					tv_update.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//String url = Config.URL + Config.UpdateUrl;
			//new Thread(new NetThread.GetDataThread(handler, url, Update)).start();
		}else{
			Toast.makeText(LoginAvtivity.this, R.string.SD_NOTFIND, Toast.LENGTH_LONG).show();
		}
	}
	private boolean isSdCardExist(){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	public String GetVersion(Context context,String packString) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packString, 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
