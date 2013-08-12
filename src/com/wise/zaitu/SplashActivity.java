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
	private static final int WAIT = 1;
	private static final int GET_SERVICE = 2;
	
	boolean is_wait = false;//等待3s
	boolean is_getService = false;//得到访问地址
	
	String url = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Init();
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
			case GET_SERVICE:
				try {
					if(Config.ODG){
						Log.d(TAG, msg.obj.toString());
					}
					url = jsonXml(msg.obj.toString());
					if("".equals(url)){
						Toast.makeText(getApplicationContext(), R.string.get_service_url_wrong, Toast.LENGTH_SHORT).show();
					}else{
						is_getService = true;
						TrunActivity();	
					}
				} catch (Exception e) {
					e.printStackTrace();
				}			
				break;
			}
		}
	};
	
	private String jsonXml(String result) throws Exception{
		String[] array = result.split("; ");
		return array[1].substring(10);
	}
	
	private void TrunActivity(){
		if(is_wait && is_getService){
			Intent intent = new Intent(SplashActivity.this,LoginAvtivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
			finish();
		}		
	}
	
	class WaitThread extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = WAIT;
			handler.sendMessage(message);
		}
	}
	
	private void Init(){
		TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
		new Thread(new WaitThread()).start();
		new Thread(new NetThread.GetSerListThread(handler, GET_SERVICE,Config.strAppID)).start();
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(Config.MyPackage, 0);
			versionNumber.setText("Version " + pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}