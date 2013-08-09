package com.wise.zaitu;

import com.BaseClass.Config;
import com.BaseClass.NetThread;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	static final int WAIT = 1;
	static final int GetService = 2;
	
	boolean is_wait = false;
	boolean is_getService = false;
	String url = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Thread(new WaitThread()).start();
		new Thread(new NetThread.GetSerListThread(handler, GetService,Config.strAppID)).start();
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(Config.MyPackage, 0);
			TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
			versionNumber.setText("Version " + pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WAIT:
				is_wait = true;
				TrunActivity();
				break;

			case GetService:
				if(msg.obj == null){
					Toast.makeText(getApplicationContext(), "获取地址异常", Toast.LENGTH_SHORT).show();
				}else{
					is_getService = true;
					Log.d(TAG, msg.obj.toString());
					jsonXml(msg.obj.toString());
					TrunActivity();
				}				
				break;
			}
		}
	};
	class WaitThread extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = WAIT;
			handler.sendMessage(message);
		}
	}
	private void jsonXml(String result){
		try {
			String[] array = result.split("; ");
			url = array[1].substring(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void TrunActivity(){
		if(is_wait && is_getService){
			Intent intent = new Intent(SplashActivity.this,LoginAvtivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
			SplashActivity.this.finish();
		}		
	}
}