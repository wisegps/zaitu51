package com.BaseClass;

import java.util.List;
import com.wise.util.CarInfo;
import com.wise.zaitu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CarAdapter extends BaseAdapter {
	public Context context = null;
	private LayoutInflater myInflater = null;
	private static List<CarInfo> items;
	private Bitmap car_alert, car_bad, car_off, car_on,car_out;
	private int selectItem = 0;
	String str;
	String alarm[];
	
	public void setSelectItem(int selectItem){
		this.selectItem = selectItem;
	}

	public CarAdapter(Context context, List<CarInfo> it) {
		this.context = context;
		items = it;
		str = context.getString(R.string.accon);
		alarm = context.getResources().getStringArray(R.array.alarm);
		car_alert = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_alert);
		car_bad = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_bad);
		car_off = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_off);
		car_on = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_on);
		car_out = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_out);
	}
	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			myInflater = LayoutInflater.from(context);
			convertView = myInflater.inflate(R.layout.list_row, null);
			holder.caro = (ImageView) convertView.findViewById(R.id.CarInfo);
			holder.carid = (TextView) convertView.findViewById(R.id.car_id);
			holder.carinfo = (TextView) convertView.findViewById(R.id.car_info);
			holder.carstatu = (TextView)convertView.findViewById(R.id.car_statu);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		String status = items.get(position).getCar_status().toString();
		int gpsFlag = Integer.valueOf(items.get(position).getGPSFlag());
		String BoardTime = items.get(position).getBoardTime();
		if(AllStaticClass.GetTimeDiff(BoardTime) > 10){
			holder.caro.setImageBitmap(car_out);
		}else if(gpsFlag%2 != 0) {
			holder.caro.setImageBitmap(car_bad);
		} else if(SearchAlarm(status)){
			holder.caro.setImageBitmap(car_alert);
		}else if(SearchAccon(status)){
			holder.caro.setImageBitmap(car_on);
		}else{
			holder.caro.setImageBitmap(car_off);
		}
		holder.carid.setText(items.get(position).getRegNum().toString());
		holder.carinfo.setText(
				changeTime(items.get(position).getGps_time()) + "   "
				+ items.get(position).getSpeed() + " km/h");
		holder.carstatu.setText(items.get(position).getCar_status());
		
		if(position == selectItem){
			convertView.setBackgroundColor(Color.rgb(204, 204, 204));
		}else{
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}
	
	private String changeTime(String str){
		return str.replace("T", " ");
	}

	public class ViewHolder {
		ImageView caro;
		TextView carid, carinfo ,carstatu;
	}
	
	private boolean SearchAlarm(String status){
		for(int i = 0 ; i < alarm.length ; i++){
			if(status.indexOf(alarm[i])>=0){
				return true;
			}
		}
		return false;
	}
	private boolean SearchAccon(String status){
		if(status.indexOf(str)>=0){
			return true;
		}
		return false;
	}
}
