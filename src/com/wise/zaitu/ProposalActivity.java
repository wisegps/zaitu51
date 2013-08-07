package com.wise.zaitu;

import com.BaseClass.Config;
import com.BaseClass.WebService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProposalActivity extends Activity{
	
	private final int NETWRONG = 1 ; //网络连接超时
	private final int BACKOK = 2;    //意见反馈成功
	private final int timeout = 30000;
	
	String str;
	ProgressDialog SearchpProgressDialog = null;  //progress
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.proposal);
		final EditText et_proposal = (EditText)findViewById(R.id.et_proposal);
		Button bt_proposal = (Button)findViewById(R.id.bt_proposal);
		bt_proposal.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				str = et_proposal.getText().toString().trim();
				SearchpProgressDialog = ProgressDialog.show(ProposalActivity.this,getString(R.string.Note),getString(R.string.bt_proposal_load),true);
				new Thread(new BackThread()).start();
			}
		});
		Button bt_proposal_cancle = (Button)findViewById(R.id.bt_proposal_cancle);
		bt_proposal_cancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NETWRONG:
				if(SearchpProgressDialog != null){
					SearchpProgressDialog.dismiss();
				}
				Toast.makeText(ProposalActivity.this, R.string.AllCarInfoActivity_login_net_outtime, Toast.LENGTH_SHORT).show();
				break;

			case BACKOK:
				if(SearchpProgressDialog != null){
					SearchpProgressDialog.dismiss();
				}
				Toast.makeText(ProposalActivity.this, R.string.proposal_up_ok, Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	
	class BackThread extends Thread{
		@Override
		public void run() {
			super.run();
			SharedPreferences preferences = getSharedPreferences("wise",Context.MODE_PRIVATE);
			String url = preferences.getString("url", "");
			int userid = preferences.getInt("userid", -1);
			System.out.println(url);
			System.out.println(userid);
			try {
				String result = WebService.SoapAddFeedback(url, Config.nameSpace, Config.methodAddFeedback, userid, str, timeout);
				System.out.println(result);
				if(result.indexOf("-1")>0){
					Message msg = new Message();
					msg.what = NETWRONG;
					handler.sendMessage(msg); // 向Handler发送超时消息,更新UI
				}else{
					Message msg = new Message();
					msg.what = BACKOK;
					handler.sendMessage(msg); // 向Handler发送超时消息,更新UI
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = NETWRONG;
				handler.sendMessage(msg); // 向Handler发送超时消息,更新UI
			}
		}
	}
}
