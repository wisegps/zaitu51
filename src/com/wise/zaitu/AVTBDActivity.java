package com.wise.zaitu;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import com.BaseClass.AllStaticClass;
import com.BaseClass.CarAdapter;
import com.BaseClass.CarBDLocationOverlay;
import com.BaseClass.Config;
import com.BaseClass.LocusBDOverlay;
import com.BaseClass.MeBDItemizedOverlay;
import com.BaseClass.MyBDOverlay;
import com.BaseClass.NetThread;
import com.BaseClass.PoiBDOverlay;
import com.BaseClass.ResolveData;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;
import com.wise.util.ArrayAdapter;
import com.wise.util.CarInfo;
import com.wise.util.TotalData;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class AVTBDActivity extends MapActivity implements OnGestureListener{
	private final int GetCarList = 1; //获取车辆数据
	private final int UPDATEMAIN = 2; //刷新车辆数据
	private final int GetMeLocation = 3; //个人位置
	private final int GetLocus  = 4; //获取轨迹回访数据
	private final int LocausOver  = 5; //轨迹回放完毕
	private final int LocausNow  = 6; //每隔一段时间画一次图像
	private final int LocausThreadDad  = 7; //轨迹回放完毕
	private final int UpdatePwd  = 8; //修改用户密码
	private final int checkPwd  = 9; //验证用户密码
	private final int GetPoi  = 10; //获取热点
	private final int GetTotal  = 11; //获取统计数据
	private final int GetLocation  = 12; //获取地址
	//控件
	ViewFlipper flipper;
	GestureDetector gestureScanner;
	ListView lv_cars;
	AutoCompleteTextView ListAutoComplete,MapAutoComplete;
	ImageView iv_ListClear,iv_MapClear;
	ImageView iv_Me,iv_Map;
	LinearLayout layout_bar;
	ProgressBar bar;
	EditText et_start,et_stop;
	ImageView bd_iv_play,bd_iv_pause,bd_iv_stop;
	SeekBar bd_sb_speed;
	ProgressDialog Dialog;
	TextView tv_statistic,bd_tv_map_change,tv_address,tv_SearchStatistic;
	Button bt_ZoomDown,bt_ZoomUp;
	//全局变量
	public List<CarInfo> carinfos = new ArrayList<CarInfo>(); // 所有数据集合list
	List<String> carNums;                         //绑定到自动感应控件
	private List<CarInfo> carPath; // 轨迹回放list
	CarAdapter carAdapter;
	
	String strGroupCode = null; // 用户组
	Integer userid = null; // 用户ID
	String LoginName; // 账号
	int ShortTime; // 定时刷新时间
	int Text_size; // 字体大小
	int Car_item = 0;  //车所在的item
	String Car_id; // 当前监控车id
	int PROGRESS = 0; // 进度条开始位置
	GeoPoint LastPoint;  //上一个点实时监控用到
	GeoPoint Point; // 默认定位的点
	GeoPoint p;     //当前gps位置
	GeoPoint pLocation;     //当前基站位置位置
	BMapManager mBMapMan = null;
	MapView mMapView;
	MapController mMapController;
	private List<Overlay> mapOverLays; // 图层列表
	View popView;     // 气泡窗口
	String startTime;
	String stopTime;
	String url;
	/**
	 * 轨迹逐步回放的间隔时间
	 */
	int SENDTIME = 1000;
	/**
	 * 第一次加载页面，需要启动定时刷新，
	 */
	boolean IsFristOnCreate = true;
	/**
	 * 是否监控，ture实时监控，并显示路线图。false取消实时监控
	 */
	private boolean ISSEARCH = false;
	/**
	 * false 地图 ，true 卫星
	 */
	boolean IsSatellite = false;
	/**
	 * 是否在轨迹回访中（跟踪），是，停止刷新数据，否刷新数据
	 */
	boolean IsLock = false;
	/**
	 * 刷新所有数据，退出时关闭
	 */
	boolean IsUpdateMain = true;
	/**
	 * 控制轨迹回访线程
	 */
	boolean ISSTARTBAR = false;
	/**
	 * 程序是否在后台
	 */
	boolean isPause = false;

	
	//定位管理
	LocationManager locationManager;
	private LocationListener gpsListener=null;
	private LocationListener networkListner=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //手势控件
        gestureScanner = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		LayoutInflater mLayoutInflater = LayoutInflater.from(AVTBDActivity.this);
		
		//搜索
		View searchView = mLayoutInflater.inflate(R.layout.search, null);
		flipper.addView(searchView);
		tv_SearchStatistic = (TextView)searchView.findViewById(R.id.tv_SearchStatistic);
		iv_ListClear = (ImageView)searchView.findViewById(R.id.iv_ListClear);
		iv_ListClear.setOnClickListener(OCL);
		iv_Map = (ImageView)searchView.findViewById(R.id.iv_Map);
		iv_Map.setOnClickListener(OCL);
		lv_cars = (ListView)searchView.findViewById(R.id.lv_car);
		lv_cars.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				AVTBDActivity.this.gestureScanner.onTouchEvent(event);
				return false;
			}
		});
		lv_cars.setOnItemClickListener(OICL);
		ListAutoComplete = (AutoCompleteTextView) searchView.findViewById(R.id.et_ListSearch);
		ListAutoComplete.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//autocomolete有内容显示清空按钮
				if(s.length() > 0){
					iv_ListClear.setVisibility(View.VISIBLE);
				}else{
					iv_ListClear.setVisibility(View.GONE);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
		});
		//地图
		View mapView = mLayoutInflater.inflate(R.layout.map_bd, null);
		flipper.addView(mapView);
		layout_bar = (LinearLayout) mapView.findViewById(R.id.bd_Layout_bar);
		bar = (ProgressBar) mapView.findViewById(R.id.bd_show_bar);
		//跳转到列表页面
		ImageView iv_Search = (ImageView)mapView.findViewById(R.id.bd_iv_Search);
		iv_Search.setOnClickListener(OCL);
		iv_MapClear = (ImageView)mapView.findViewById(R.id.bd_iv_MapClear);
		iv_MapClear.setOnClickListener(OCL);
		iv_Me = (ImageView)mapView.findViewById(R.id.bd_iv_Me);
		iv_Me.setOnClickListener(OCL);
		bt_ZoomDown = (Button)mapView.findViewById(R.id.bd_bt_ZoomDown);
		bt_ZoomDown.setOnClickListener(OCL);
		bt_ZoomUp = (Button)mapView.findViewById(R.id.bd_bt_ZoomUp);
		bt_ZoomUp.setOnClickListener(OCL);
		MapAutoComplete = (AutoCompleteTextView) mapView.findViewById(R.id.bd_et_MapSearch);
		MapAutoComplete.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() > 0){
					iv_MapClear.setVisibility(View.VISIBLE);
				}else{
					iv_MapClear.setVisibility(View.GONE);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
		});
		tv_statistic = (TextView)mapView.findViewById(R.id.bd_tv_statistic);
		bd_tv_map_change = (TextView)mapView.findViewById(R.id.bd_tv_map_change);
		tv_address = (TextView)mapView.findViewById(R.id.bd_tv_address);
		bd_tv_map_change.setOnClickListener(OCL);
		bd_iv_play = (ImageView)mapView.findViewById(R.id.bd_iv_play);
		bd_iv_play.setOnClickListener(OCL);
		bd_iv_pause = (ImageView)mapView.findViewById(R.id.bd_iv_pause);
		bd_iv_pause.setOnClickListener(OCL);
		bd_iv_stop = (ImageView)mapView.findViewById(R.id.bd_iv_stop);
		bd_iv_stop.setOnClickListener(OCL);
		bd_sb_speed = (SeekBar)mapView.findViewById(R.id.bd_sb_speed);
		bd_sb_speed.setOnSeekBarChangeListener(OSBCL);
		bd_sb_speed.setProgress(50);
		
		//默认显示地图页面
		flipper.setDisplayedChild(1);
		
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("934B1BF7DD618B5E356CA715D473D1BD864048AC", null);
		super.initMapActivity(mBMapMan);
		mMapView = (MapView) findViewById(R.id.bd_MapView);
		mMapView.setBuiltInZoomControls(false); // 设置启用内置的缩放控件
		//设置在缩放动画过程中也显示overlay,默认为不绘制
        mMapView.setDrawOverlayWhenZooming(true);
		mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		mMapView.setSatellite(IsSatellite); //地图
		mapOverLays = mMapView.getOverlays();
		
		getDate();
		Dialog = ProgressDialog.show(AVTBDActivity.this,getString(R.string.AllCarInfoActivity_serach_pd_title),getString(R.string.AllCarInfoActivity_serach_pd_context), true);
		
		new Thread(new NetThread.SearchThread(url,strGroupCode, handler,GetCarList)).start();
		//new Thread(new NetThread.GetPoi(url,userid, handler, GetPoi)).start();
		new Thread(new NetThread.TotalThread(url,strGroupCode, handler,GetTotal)).start();
		new Thread(new WhileThread()).start();
	}
    
    /**
	 * 初始化数据
	 */
	private void getDate() {
		Intent intent = getIntent();
		strGroupCode = intent.getStringExtra("strGroupCode");
		LoginName = intent.getStringExtra("LoginName");
		userid = intent.getIntExtra("userid", 0);
		url = intent.getStringExtra("url");
		SharedPreferences preferences = getSharedPreferences("wise",Context.MODE_PRIVATE);
		ShortTime = (preferences.getInt("ShortTime", 20)) * 1000;
		Text_size = preferences.getInt("Text_size", 22);
		// 初始化点击标注显示方式
		popView = super.getLayoutInflater().inflate(R.layout.pop, null);
		mMapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,0,0,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GetCarList:
				try {
					//获取车辆信息
					carinfos = ResolveData.parseXML((String)msg.obj,carinfos);
					if(IsFristOnCreate){
						// 成功跳入，并显示信息
						if (Dialog != null) {
							Dialog.dismiss();
						}
						System.out.println("第一次创建界面");
						IsFristOnCreate = false;
						//启动定时刷新线程
						new Thread(new UpdateMain()).start();
						//绑定数据
						carAdapter = new CarAdapter(AVTBDActivity.this, carinfos);
						lv_cars.setAdapter(carAdapter);
						//把车辆号码取出来
						carNums = new ArrayList<String>(); 
						for(int i = 0 ; i < carinfos.size(); i++){
							carNums.add(carinfos.get(i).getRegNum());
						}
						bindData();
						//当前车选中状态,列表选中
						carAdapter.setSelectItem(Car_item);
						carAdapter.notifyDataSetInvalidated();
						//记下当前车的id
						Car_id = carinfos.get(Car_item).getCar_id();
						//全部车辆图层
						ShowAllCar();
						//定位到第一个
						Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(0).getLat()), AllStaticClass.StringToInt(carinfos.get(0).getLon()));
						mMapController.animateTo(Point);
						//获取选中车辆位置
						new Thread(new NetThread.GetLocation(carinfos.get(0).getLat(), carinfos.get(0).getLon(), handler,GetLocation,AVTBDActivity.this)).start();
						getLocation();
					}else{
						System.out.println("IsLock:"+IsLock);
						if(!IsLock){
							//如果在轨迹回访不刷新数据
							if (popView != null) {
								popView.setVisibility(View.GONE);
							}
							//跟踪用到
							LastPoint = Point;
							System.out.println("刷新所有数据");
							//刷新列表数据
							carAdapter.notifyDataSetChanged();
							//删除汽车标记
							for(int i = 0 ; i < mapOverLays.size() ; i++){
								if(mapOverLays.get(i).getClass().toString().indexOf("CarLocationOverlay")>0){
									mapOverLays.remove(i);
									i--;
								}
							}
							//当前车位置
							Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(Car_item).getLat()), AllStaticClass.StringToInt(carinfos.get(Car_item).getLon()));
							if(ISSEARCH){
								//处于跟踪状态
								mapOverLays.add(new MyBDOverlay(LastPoint, Point));
								//添加所有图层
								ShowAllCar();
								mMapController.animateTo(Point);
							}else{
								//添加所有图层
								ShowAllCar();
								mMapController.animateTo(mMapView.getMapCenter());
							}
							new Thread(new NetThread.TotalThread(url,strGroupCode, handler,GetTotal)).start();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (Dialog != null) {
						Dialog.dismiss();
					}
					if(IsFristOnCreate){
						Toast.makeText(AVTBDActivity.this, R.string.AllCarInfoActivity_search_null, Toast.LENGTH_LONG).show();
					}
				}
				break;
			//定时刷新所有车辆信息
			case UPDATEMAIN:
				new Thread(new NetThread.SearchThread(url,strGroupCode, handler,GetCarList)).start();
				break;
			//获取自己当前位置
			case GetMeLocation:
				if(!IsLock){
					if(msg.arg1 == 1){
						//gps定位
		            	String FixValue = (String) msg.obj;
		            	String[] value = FixValue.split(",");
		            	p = new GeoPoint(AllStaticClass.StringToInt(value[1]), AllStaticClass.StringToInt(value[0])); // 得到经纬度
		            	ShowMeLocation(p);
					}
	            }
				break;
			//获取轨迹回放数据
			case GetLocus:
				if (Dialog != null) {
					Dialog.dismiss();
				}
				try {
					String theLocus = (String) msg.obj;
					//解析轨迹回访返回的数据carPath
					carPath = ResolveData.locusParseXML(theLocus);
					locus();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(AVTBDActivity.this, R.string.monitor_locus_null,Toast.LENGTH_LONG).show();
				}
				break;
			case LocausOver:
				//回访完毕后的动作
				bd_iv_play.setEnabled(true);
				bd_iv_pause.setEnabled(true);
				PROGRESS = 0;
				break;
			//轨迹回访步骤
			case LocausNow:
				LocusNow(PROGRESS);
				break;
			case LocausThreadDad:
				bd_iv_play.setEnabled(true);
				bd_iv_pause.setEnabled(true);
				//删除轨迹移动图标
				for(int j = 0 ; j < mapOverLays.size() ; j++){
					if(mapOverLays.get(j).getClass().toString().indexOf("LocusOverlay")>0){
						mapOverLays.remove(j);
						j--;
					}
				}
				break;
			//验证密码返回
			case UpdatePwd:
				try {
					SoapObject result = (SoapObject) msg.obj;
					SoapPrimitive UserId = (SoapPrimitive)result.getProperty(0);
					userid = Integer.valueOf(UserId.toString());
					// 验证完账号密码后修改密码线程，不需要取消等待框
					new Thread(new NetThread.ChangePwdThread(url,userid, newpwd, handler,checkPwd)).start();
				} catch (Exception e) {
					if (Dialog != null) {
						Dialog.dismiss();
					}
					Toast.makeText(AVTBDActivity.this,R.string.AllCarInfoActivity_login_id_wrong,Toast.LENGTH_SHORT).show();
				}
				break;
			//修改密码返回
			case checkPwd:
				if (Dialog != null) {
					Dialog.dismiss();
				}
				try {
					System.out.println(msg.obj.toString());
					String result = msg.obj.toString();
					if(result.indexOf("0")> 0){
						Toast.makeText(AVTBDActivity.this, R.string.change_pwd_true,Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(AVTBDActivity.this, R.string.change_pwd_false,Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(AVTBDActivity.this, R.string.change_pwd_false,Toast.LENGTH_SHORT).show();
				}
				break;
			case GetPoi:
				try {
					String result = (String) msg.obj;
					System.out.println(result);
					String[] poi_result = result.split("POI=anyType");
					for(int i = 1 ; i < poi_result.length ; i++){
						String[] poi_data = poi_result[i].split(";");
						
						Drawable drawablePoi = AllStaticClass.FindPoiIcon(AVTBDActivity.this, poi_data[3].substring(11));
						drawablePoi.setBounds(0, 0, drawablePoi.getIntrinsicWidth(), drawablePoi.getIntrinsicHeight());
						GeoPoint Point = new GeoPoint(AllStaticClass.StringToInt(poi_data[8].substring(5)), AllStaticClass.StringToInt(poi_data[7].substring(5))); // 得到经纬度
						PoiBDOverlay itemOverLayPoi = new PoiBDOverlay(drawablePoi, AVTBDActivity.this, poi_data[2].substring(11), Point,IsSatellite,Text_size);
						OverlayItem overLayItemPoi = new OverlayItem(Point, "", ""); // 绑定点击事件
						itemOverLayPoi.addOverLay(overLayItemPoi);
						mapOverLays.add(itemOverLayPoi); // 图层添加到map显示
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case GetTotal:
				//得到统计信息
				String theResult = (String) msg.obj;
				try{
					TotalData totalData = ResolveData.ResolveTotal(theResult);
					String statistic = getString(R.string.total)+": "+totalData.getToatal()+"          "
					          +getString(R.string.accon)+": "+totalData.getAccOn()+"          "
					          +getString(R.string.accoff)+": "+totalData.getAccOff();
					tv_statistic.setText(statistic);
					tv_SearchStatistic.setText(statistic);
				}catch (Exception e) {
					e.printStackTrace();
					System.out.println("统计信息异常");
				}
				break;
			case GetLocation:
				String LocationString = (String) msg.obj;
				tv_address.setText(LocationString);
				break;
			}
		}
	};
	
	/**
	 * seekBar滑动事件
	 */
	OnSeekBarChangeListener OSBCL = new OnSeekBarChangeListener() {		
		public void onStopTrackingTouch(SeekBar seekBar) {}		
		public void onStartTrackingTouch(SeekBar seekBar) {}		
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
			SENDTIME = 1500 - seekBar.getProgress()*10;
		}
	};
	/**
	 * 把运行轨迹画到地图上
	 */
	private void locus() {
		if(carPath.size()==0){
			//没有数据
			Toast.makeText(AVTBDActivity.this, R.string.monitor_locus_null,Toast.LENGTH_LONG).show();
		}else{
			IsLock = true;
			//删除车辆标记
			for(int i = 0 ; i < mapOverLays.size() ; i++){
				if(mapOverLays.get(i).getClass().toString().indexOf("CarLocationOverlay")>0){
					mapOverLays.remove(i);
					i--;
				}
			}
			//循环画出轨迹线
			for (int i = 0; i < carPath.size() - 1; i++) {
				GeoPoint startPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i).getLat()), AllStaticClass.StringToInt(carPath.get(i).getLon()));
				GeoPoint stopPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i + 1).getLat()),AllStaticClass.StringToInt(carPath.get(i + 1).getLon()));
				mapOverLays.add(new MyBDOverlay(startPoint, stopPoint));
			}
			// 定位到开始的点
			mMapController.animateTo(new GeoPoint(AllStaticClass.StringToInt(carPath.get(0).getLat()), AllStaticClass.StringToInt(carPath.get(0).getLon()))); 
			//显示轨迹控制条
			layout_bar.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * 轨迹图标移动
	 * @param index
	 */
	private void LocusNow(int index){
		for(int j = 0 ; j < mapOverLays.size() ; j++){
			if(mapOverLays.get(j).getClass().toString().indexOf("LocusOverlay")>0){
				mapOverLays.remove(j);
				j--;
			}
		}
		System.gc();
		GeoPoint stopPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(index).getLat()), AllStaticClass.StringToInt(carPath.get(index).getLon()));
		String GPSFlag = carPath.get(index).getGPSFlag();
		String MSTStatus = carPath.get(index).getCar_status();
		String Direct = carPath.get(index).getDirect();
		Drawable drawable = AllStaticClass.DrawableBimpMap(AVTBDActivity.this, GPSFlag, MSTStatus, Direct,carPath.get(index).getBoardTime());
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		LocusBDOverlay itemOverLay = new LocusBDOverlay(drawable); // 实例化画图
		OverlayItem overLayItem = new OverlayItem(stopPoint, "123","123"); // 绑定点击事件
		itemOverLay.addOverLay(overLayItem);

		mapOverLays.add(itemOverLay); // 图层添加到map显示
		mMapController.animateTo(stopPoint); // 定位
		bar.setProgress(index);
		ShowLocalPop(index);
	}
	private void ShowLocalPop(int i){
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView.getLayoutParams();
		geoLP.point = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i).getLat()), AllStaticClass.StringToInt(carPath.get(i).getLon()));;
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		tv_car_id.setVisibility(View.GONE);
		TextView my_line = (TextView) popView.findViewById(R.id.my_line);
		my_line.setVisibility(View.GONE);
		TextView tv_car_MSTStatus = (TextView) popView.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView.findViewById(R.id.pop_car_Mileage);
		TextView tv_car_Speed = (TextView) popView.findViewById(R.id.pop_car_Speed);
		TextView tv_car_GpsTime = (TextView) popView.findViewById(R.id.pop_car_GpsTime);
		TextView pop_car_staticTime = (TextView) popView.findViewById(R.id.pop_car_staticTime);
		//TextView pop_car_Temp = (TextView) popView.findViewById(R.id.pop_car_Temp);
		
		TextView bt_menu_car = (TextView)popView.findViewById(R.id.bt_menu_car);
		bt_menu_car.setVisibility(View.GONE);
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(OCL);
		
		TextView bt_monitor_locus = (TextView)popView.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.setVisibility(View.GONE);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		bt_monitor_locus.setOnClickListener(OCL);
		
		String Speed = carPath.get(i).getSpeed();
		String RegNum = carPath.get(i).getRegNum();
		String MSTStatus = carPath.get(i).getCar_status();
		String mileage = carPath.get(i).getMileage();
		String gps_time = carPath.get(i).getGps_time().replaceAll("T", " ");
		
		tv_car_id.setText(RegNum);
		tv_car_MSTStatus.setText(MSTStatus + " ");
		tv_car_Mileage.setText(mileage + " km");
		tv_car_Speed.setText(Speed + " km/h");
		tv_car_GpsTime.setText(gps_time);
		pop_car_staticTime.setText(carPath.get(i).getStaticTime());
		//pop_car_Temp.setText(carPath.get(i).getTemp());

		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
	}
	/**
	 * 当前位置
	 */
	private void ShowMeLocation(GeoPoint gp){
		try{
			//当前位置
			for(int i = 0 ; i < mapOverLays.size() ; i++){
				if(mapOverLays.get(i).getClass().toString().indexOf("MeItemizedOverlay")>0){
					mapOverLays.remove(i);
					break;
				}
			}
			Drawable drawable = getResources().getDrawable(R.drawable.car);
	        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	        MeBDItemizedOverlay carItemizedOverlay = new MeBDItemizedOverlay(drawable, AVTBDActivity.this, gp,Accuracy);
	        OverlayItem overlayItem = new OverlayItem(gp, "", "");
	        carItemizedOverlay.addOverLay(overlayItem);
	        mapOverLays.add(carItemizedOverlay);
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("个人定位异常");
		}
	}
	/**
	 * 定时刷新主线程
	 * @author honesty
	 *
	 */
	class UpdateMain implements Runnable {
		public void run() {
			while (IsUpdateMain) {
				int updateTime;
				//判断配置文件是否自动刷新
				SharedPreferences preferences = getSharedPreferences("wise", Context.MODE_PRIVATE);
				boolean isRef = preferences.getBoolean("isRef", true);
				if(isRef){
					updateTime = (preferences.getInt("ShortTime", 30)) * 1000;
				}else{
					updateTime = 180000;
				}
				System.out.println("定时刷新时间：" + updateTime);
				try {
					Thread.sleep(updateTime);
					//判断是否程序运行在后台
					if(!isPause){
						Message message = new Message();
						message.what = UPDATEMAIN;
						handler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.e("see", "循环线程死亡");
		}
	}
	
	/**
	 * 显示全部车辆
	 */
	private void ShowAllCar(){
		HideLocaus();
		for(int i = 0 ; i < carinfos.size() ; i++){
			String Lat = carinfos.get(i).getLat();
			String Lon = carinfos.get(i).getLon();
			String Speed = carinfos.get(i).getSpeed();
			String Direct = carinfos.get(i).getDirect();
			String RegNum = carinfos.get(i).getRegNum();
			String GPSFlag = carinfos.get(i).getGPSFlag();
			String MSTStatus = carinfos.get(i).getCar_status();
			String Mileage = carinfos.get(i).getMileage();
			String gps_time = carinfos.get(i).getGps_time().replaceAll("T", " ");
			String Fuel  = carinfos.get(i).getFuel();
			String Temp  = carinfos.get(i).getTemp();
			
			String snippet = RegNum + ",," + gps_time + ",," + MSTStatus + ",," + Speed + "km/h" + ",," + Mileage + "km,," + Fuel + ",," + Temp + ",," + carinfos.get(i).getStaticTime();
			
			GeoPoint Point = new GeoPoint(AllStaticClass.StringToInt(Lat), AllStaticClass.StringToInt(Lon)); // 得到经纬度
			Drawable drawable = AllStaticClass.DrawableBimpMap(AVTBDActivity.this, GPSFlag, MSTStatus, Direct,carinfos.get(i).getBoardTime());
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			drawable.setBounds(-w/2, -h/2, w/2, h/2);
			CarBDLocationOverlay itemOverLay;
			//判断当前车辆是否跟踪，要用特殊标记
			if(i == Car_item){
				itemOverLay = new CarBDLocationOverlay(drawable, AVTBDActivity.this, RegNum, Point, ISSEARCH,IsSatellite,AVTBDActivity.this,Text_size);
			}else{
				itemOverLay = new CarBDLocationOverlay(drawable, AVTBDActivity.this, RegNum, Point, false,IsSatellite,AVTBDActivity.this,Text_size);
			}
			OverlayItem overLayItem = new OverlayItem(Point, RegNum, snippet); // 绑定点击事件
			overLayItem.setMarker(drawable);
			itemOverLay.addOverLay(overLayItem);
			mapOverLays.add(itemOverLay); // 图层添加到map显示
		}
	}	
	/**
	 * 绑定自动感应控件
	 */
	private void bindData(){
    	//绑定数据到AutoCompleteTextView
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,carNums); 
    	//列表搜索控件
    	ListAutoComplete.setAdapter(adapter);
    	ListAutoComplete.setThreshold(0);
    	ListAutoComplete.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String Regnum = ListAutoComplete.getText().toString();
				for(int i = 0 ; i < carinfos.size() ; i++){
					if(carinfos.get(i).getRegNum().equals(Regnum)){
						lv_cars.setSelection(i); //定位到对应行
						//选中对应车辆
						carAdapter.setSelectItem(i);
						carAdapter.notifyDataSetInvalidated();
						break;
					}
				}
			}
		});
    	//地图搜索控件
    	MapAutoComplete.setAdapter(adapter);
    	MapAutoComplete.setThreshold(0);
    	MapAutoComplete.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String Regnum = MapAutoComplete.getText().toString();
				for(int i = 0 ; i < carinfos.size() ; i++){
					if(carinfos.get(i).getRegNum().equals(Regnum)){
						ChooseCar(i,1);
						lv_cars.setSelection(i);
						break;
					}
				}
			}
		});
    }
    
    
    private OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bd_bt_ZoomDown:
				mMapController.zoomOut();
				break;
			case R.id.bd_bt_ZoomUp:
				mMapController.zoomIn();
				break;
			//跳转到地图页
			case R.id.iv_Map:
				flipper.setDisplayedChild(1);
				break;
			//跳转到列表页
			case R.id.bd_iv_Search:
				flipper.setDisplayedChild(0);
				break;
			case R.id.bd_tv_map_change:
				//如果在轨迹或回访不能对图标处理
				if(!IsLock){
					ChangeMap();
				}
				//卫星和地图切换
				if(IsSatellite){
					//地图
					IsSatellite = false;
					mMapView.setSatellite(false);
					bd_tv_map_change.setText(R.string.Satellite);
				}else{
					//卫星
					IsSatellite = true;
					mMapView.setSatellite(IsSatellite);
					bd_tv_map_change.setText(R.string.Traffic);
				}
				break;
			//定位到当前位置
			case R.id.bd_iv_Me:
				//定位到当前位置
				try {
					if(p==null){
						mMapController.animateTo(pLocation);
					}else{
						mMapController.animateTo(p);
					}
				} catch (Exception e) {
					//没有定位信息
					Toast.makeText(AVTBDActivity.this, R.string.Location_wrong, Toast.LENGTH_LONG).show();
				}
				break;
			//清除列表
			case R.id.iv_ListClear:
				ListAutoComplete.setText("");
				break;
			//清除地图
			case R.id.bd_iv_MapClear:
				MapAutoComplete.setText("");
				break;
			case R.id.bt_menu_car:
				//跟踪
				HideLocaus();
				popView.setVisibility(View.GONE);
				if (ISSEARCH) {
					//取消跟踪
					ISSEARCH = false;
					System.gc();
					//删除轨迹线
					for(int i = 0 ; i < mapOverLays.size() ; i++){
						if(mapOverLays.get(i).getClass().toString().indexOf("MyOverlay")>0){
							mapOverLays.remove(i);
							i--;
						}
					}
				} else {
					//开始跟踪
					ISSEARCH = true;
					//删除汽车标记
					for(int i = 0 ; i < mapOverLays.size() ; i++){
						if(mapOverLays.get(i).getClass().toString().indexOf("CarLocationOverlay")>0){
							mapOverLays.remove(i);
							i--;
						}
					}
					ShowAllCar();
				}
				break;
			case R.id.bt_monitor_locus:
				//轨迹回放
				popView.setVisibility(View.GONE);
				//关闭跟踪
				ISSEARCH = false;
				LocausDialog();
				break;
			//开始播放轨迹
			case R.id.iv_play:
				//播放按钮设为不可用
				bd_iv_play.setEnabled(false);
				if(PROGRESS == 0){
					bar.setMax(carPath.size() - 1);
					ISSTARTBAR = true;
					new Thread(new startBarThread()).start();
				}else{
					//暂停后播放
					ISSTARTBAR = true;
					new Thread(new startBarThread()).start();
				}
				break;
			//暂停和继续播放
			case R.id.bd_iv_pause:
				if(PROGRESS > 0){
					//停止播放线程
					ISSTARTBAR = false;
				}
				break;
			//退出轨迹回放
			case R.id.bd_iv_stop:
				//停止回访线程
				ISSTARTBAR = false;
				//数据回复到开始状态
				PROGRESS = 0;
				bar.setProgress(0);
				LocusNow(PROGRESS);
				break;
			}
		}
	};
	/**
	 * 切换卫星图
	 */
	private void ChangeMap(){
		//删除汽车标记
		for(int i = 0 ; i < mapOverLays.size() ; i++){
			if(mapOverLays.get(i).getClass().toString().indexOf("CarLocationOverlay")>0){
				mapOverLays.remove(i);
				i--;
			}
		}
		ShowAllCar();
	}
	/**
	 * 轨迹回放
	 * @author honesty
	 *
	 */
	class startBarThread implements Runnable {
		public void run() {
			while (ISSTARTBAR) {
				try {
					if (PROGRESS >= carPath.size() - 1) {
						ISSTARTBAR = false;
						Message message = new Message();
						message.what = LocausOver;
						handler.sendMessage(message);
						System.out.println("回放完毕" +PROGRESS + "/" +carPath.size());
					} else {
						Thread.sleep(SENDTIME);
						if(ISSTARTBAR){
							PROGRESS++;
							Message message = new Message();
							message.what = LocausNow;
							handler.sendMessage(message);
							System.out.println("位置：" +PROGRESS + "/" +carPath.size());
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Message message = new Message();
			message.what = LocausThreadDad;
			handler.sendMessage(message);
		}
	}
	
	/**
	 * 隐藏轨迹回访
	 */
	private void HideLocaus(){
		//关闭线程
		ISSTARTBAR = false;
		//隐藏
		layout_bar.setVisibility(View.GONE);
		//
		bar.setProgress(0);
		PROGRESS = 0;
	}
	
	private void LocausDialog() {
		View viewtime = LayoutInflater.from(AVTBDActivity.this).inflate(R.layout.timedialog, null);
		ImageView iv_start = (ImageView) viewtime.findViewById(R.id.iv_start);
		iv_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(1);
			}
		});
		ImageView iv_stop = (ImageView) viewtime.findViewById(R.id.iv_stop);
		iv_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(2);
			}
		});
		et_start = (EditText) viewtime.findViewById(R.id.et_start);
		et_start.setInputType(InputType.TYPE_NULL);
		et_stop = (EditText) viewtime.findViewById(R.id.et_stop);
		et_stop.setInputType(InputType.TYPE_NULL);
		AlertDialog.Builder timeBuilder = new AlertDialog.Builder(AVTBDActivity.this);
		timeBuilder.setView(viewtime);
		timeBuilder.setTitle(R.string.car_dialog_title);
		timeBuilder.setPositiveButton(R.string.car_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (et_start.getText().toString().equals("")|| et_stop.getText().toString().equals("")) {
							Toast.makeText(AVTBDActivity.this,R.string.car_dialog_et_null,Toast.LENGTH_SHORT).show();
						} else if (!AllStaticClass.LimitTime(et_start.getText().toString(),et_stop.getText().toString())) {
							Toast.makeText(AVTBDActivity.this,R.string.car_dialog_time_limit,Toast.LENGTH_SHORT).show();
						} else {
							// 查询轨迹
							Dialog = ProgressDialog.show(AVTBDActivity.this,getString(R.string.AllCarInfoActivity_serach_pd_title),getString(R.string.monitor_locus_load),true);
							new Thread(new NetThread.locusThread(url,Car_id, startTime, stopTime, handler,GetLocus)).start();
						}
					}
				});
		timeBuilder.setNegativeButton(android.R.string.cancel, null);
		timeBuilder.show();
	}
	
	private void timeDialog(final int i) {
		/******************************************/
		View view = LayoutInflater.from(AVTBDActivity.this).inflate(R.layout.time, null);
		final DatePicker dp_start = (DatePicker) view.findViewById(R.id.dp_start);
		final TimePicker tp_start = (TimePicker) view.findViewById(R.id.tp_start);
		tp_start.setIs24HourView(true);
		if(i==1){
			tp_start.setCurrentHour(0);
			tp_start.setCurrentMinute(0);
		}else{
			tp_start.setCurrentHour(23);
			tp_start.setCurrentMinute(59);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(AVTBDActivity.this);
		builder.setView(view);
		builder.setTitle(R.string.time_choose);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 轨迹回放
						if (i == 1) {// 起始时间
							startTime = AllStaticClass.intToString(dp_start.getYear()) + "-"
									+ AllStaticClass.intToString(dp_start.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start.getCurrentMinute())
									+ ":" + "00";
							et_start.setText(startTime);
						} else {
							stopTime = AllStaticClass.intToString(dp_start.getYear()) + "-"
									+ AllStaticClass.intToString(dp_start.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start.getCurrentMinute())
									+ ":" + "00";
							et_stop.setText(stopTime);
						}
					}

				});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();

		/**********************************************/
	}
	
	
	private OnItemClickListener OICL = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			ChooseCar(arg2,0);
		}
	};
	//TODO 点击地图图标
	/**
	 * 点击地图标记同步
	 */
	public void Interactive(String car_id){
		//车辆处于list哪一项
		int arg = 0;
		for(int i = 0 ; i < carinfos.size() ; i++){
			if(carinfos.get(i).getRegNum().equals(car_id)){
				arg = i;
				//轨迹回访返回用到
				item = arg;
				break;
			}
		}
		//定位到当前坐标
		Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(arg).getLat()), AllStaticClass.StringToInt(carinfos.get(arg).getLon()));
		
		//获取位置
		new Thread(new NetThread.GetLocation(carinfos.get(arg).getLat(), carinfos.get(arg).getLon(), handler,GetLocation,AVTBDActivity.this)).start();
		
		//选中车辆
		carAdapter.setSelectItem(arg);
		carAdapter.notifyDataSetInvalidated();
		lv_cars.setSelection(arg);
		//记下当前车
		Car_id = carinfos.get(arg).getCar_id();
		Car_item = arg;
		
		if(ISSEARCH){
			//如果在监控则停止监控
			ISSEARCH = false;
			//删除汽车标记
			for(int i = 0 ; i < mapOverLays.size() ; i++){
				if(mapOverLays.get(i).getClass().toString().indexOf("CarLocationOverlay")>0){
					mapOverLays.remove(i);
					i--;
				}
			}
			ShowAllCar();
		}
		ShowPop(arg);
	}
	int item;
	/**
	 * 切换车辆
	 * @param arg
	 * @param where 0，列表 1，地图
	 */
	private void ChooseCar(int arg,int where){
		item = arg;
		if(where==0){
			//跳转到地图
			flipper.setDisplayedChild(1);
		}
		//定位到当前坐标
		Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(arg).getLat()), AllStaticClass.StringToInt(carinfos.get(arg).getLon()));
		mMapController.animateTo(Point);
		//获取位置
		new Thread(new NetThread.GetLocation(carinfos.get(arg).getLat(), carinfos.get(arg).getLon(), handler,GetLocation,AVTBDActivity.this)).start();
		
		//选中车辆
		carAdapter.setSelectItem(arg);
		carAdapter.notifyDataSetInvalidated();
		//记下当前车
		Car_id = carinfos.get(arg).getCar_id();
		Car_item = arg;
		
		if(ISSEARCH){
			//如果在监控则停止监控
			ISSEARCH = false;
			//删除汽车标记
			for(int i = 0 ; i < mapOverLays.size() ; i++){
				if(mapOverLays.get(i).getClass().toString().indexOf("CarLocationOverlay")>0){
					mapOverLays.remove(i);
					i--;
				}
			}
			ShowAllCar();
		}
		if(IsLock){
			//停止轨迹回访
			IsLock = false;
			for(int i = 0 ; i < mapOverLays.size() ; i++){
				if(mapOverLays.get(i).getClass().toString().indexOf("MyOverlay")>0){
					mapOverLays.remove(i);
					i--;
				}
			}
			//删除轨迹回访跑的图标
			for(int j = 0 ; j < mapOverLays.size() ; j++){
				if(mapOverLays.get(j).getClass().toString().indexOf("LocusOverlay")>0){
					mapOverLays.remove(j);
					j--;
				}
			}
			ShowAllCar();
		}
		mMapController.animateTo(Point);
		ShowPop(arg);
	}
	
	private void ShowPop(int i){
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView.getLayoutParams();
		geoLP.point = Point;
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		tv_car_id.setVisibility(View.VISIBLE);
		TextView my_line = (TextView) popView.findViewById(R.id.my_line);
		my_line.setVisibility(View.VISIBLE);
		TextView tv_car_MSTStatus = (TextView) popView.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView.findViewById(R.id.pop_car_Mileage);
		TextView tv_car_Speed = (TextView) popView.findViewById(R.id.pop_car_Speed);
		TextView tv_car_GpsTime = (TextView) popView.findViewById(R.id.pop_car_GpsTime);
		TextView pop_car_staticTime = (TextView) popView.findViewById(R.id.pop_car_staticTime);
		//TextView pop_car_Temp = (TextView) popView.findViewById(R.id.pop_car_Temp);
		
		TextView bt_menu_car = (TextView)popView.findViewById(R.id.bt_menu_car);
		bt_menu_car.setVisibility(View.VISIBLE);
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(OCL);
		TextView bt_monitor_locus = (TextView)popView.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.setVisibility(View.VISIBLE);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		bt_monitor_locus.setOnClickListener(OCL);
		
		String Speed = carinfos.get(i).getSpeed();
		String RegNum = carinfos.get(i).getRegNum();
		String MSTStatus = carinfos.get(i).getCar_status();
		String mileage = carinfos.get(i).getMileage();
		String gps_time = carinfos.get(i).getGps_time().replaceAll("T", " ");
		
		tv_car_id.setText(RegNum);
		tv_car_MSTStatus.setText(MSTStatus + " ");
		tv_car_Mileage.setText(mileage + " km");
		tv_car_Speed.setText(Speed + " km/h");
		tv_car_GpsTime.setText(gps_time);
		pop_car_staticTime.setText(carinfos.get(i).getStaticTime());
		//pop_car_Temp.setText(carinfos.get(i).getTemp());

		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, R.string.allcar_config);
		menu.add(0, 2, 0, R.string.bt_updatePwd);
		menu.add(0, 3, 0, R.string.Proposal);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(AVTBDActivity.this, ConfigActivity.class);
			startActivity(intent);
			break;
		case 2:
			ChangePwd();
			break;
		case 3:
			Intent intent1 = new Intent(AVTBDActivity.this,ProposalActivity.class);
			startActivity(intent1);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	};
	String LoginPws;
	String newpwd;
	
	/**
	 * 修改密码对话框
	 */
	private void ChangePwd(){
		View view = LayoutInflater.from(AVTBDActivity.this).inflate(R.layout.changepwd, null);
		final EditText et_oldpwd = (EditText)view.findViewById(R.id.oldPassWord_ET);
		final EditText et_newpwd = (EditText)view.findViewById(R.id.newPassWord_ET);
		final EditText et_newpwdtwo = (EditText)view.findViewById(R.id.newPassWordTwo_ET);
		AlertDialog.Builder bulder = new AlertDialog.Builder(AVTBDActivity.this);
		bulder.setView(view);
		bulder.setTitle(R.string.changePwd_title);// 设置标题
		bulder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				LoginPws = et_oldpwd.getText().toString().trim();
				newpwd = et_newpwd.getText().toString().trim();
				String newpwdtoo = et_newpwdtwo.getText().toString().trim();
				if(LoginPws.equals("")||newpwd.equals("")||newpwdtoo.equals("")){
					Toast.makeText(AVTBDActivity.this, R.string.change_pwd_null, Toast.LENGTH_SHORT).show();
					return;
				}else{
					if(newpwd.equals(newpwdtoo)){
						Dialog = ProgressDialog.show(AVTBDActivity.this,getString(R.string.AllCarInfoActivity_login_pd_title),getString(R.string.change_pwd_now),true);
						new Thread(new NetThread.LoginThread(url,Config.methodLogin, LoginName, LoginPws, handler,UpdatePwd)).start();
					}else{
						Toast.makeText(AVTBDActivity.this, R.string.change_pwd_TwoNewPwd_false, Toast.LENGTH_SHORT).show();
						return;
					}
				} 
			}
		});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}
	
	/**
	 * 开启个人定位
	 */
	private void getLocation(){
        // 获取位置管理服务
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        
        networkListner=new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, networkListner);
		gpsListener=new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, gpsListener);
		System.out.println("--当前位置---");
	}
	private  class MyLocationListner implements LocationListener{
		public void onLocationChanged(Location location) {
			Log.v("GPSTEST", "Got New Location of provider:"+location.getProvider());
			if(currentLocation!=null){
				if(isBetterLocation(location, currentLocation)){
					Log.v("GPSTEST", "It's a better location");
					currentLocation=location;
					showLocation(location);
				}else{
					Log.v("GPSTEST", "Not very good!");
				}
			}else{
				System.out.println("It's first location");
				Log.v("GPSTEST", "It's first location");
				currentLocation=location;
				showLocation(location);
			}
			//移除基于LocationManager.NETWORK_PROVIDER的监听器
			if(LocationManager.NETWORK_PROVIDER.equals(location.getProvider())){
				locationManager.removeUpdates(this);
			}
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
	}
	float Accuracy = 0;
	Location currentLocation;
	private void showLocation(Location location){
		//纬度
		Log.v("GPSTEST","Latitude:"+location.getLatitude());
		//经度
		Log.v("GPSTEST","Longitude:"+location.getLongitude());
		//精确度
		Log.v("GPSTEST","Accuracy:"+location.getAccuracy());
		double latitude = location.getLatitude();
        double longitude= location.getLongitude();
        Accuracy = location.getAccuracy();
        new Thread(new NetThread.FixLocation(url,longitude, latitude, GetMeLocation, handler,1)).start();
     }
	private static final int CHECK_INTERVAL = 1000 * 30;
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
 
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
		boolean isNewer = timeDelta > 0;
 
		// If it's been more than two minutes since the current location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		}
 
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
 
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());
 
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}
 
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//wl.release();
		ISSEARCH = false; // 停止线程
		IsUpdateMain = false;
		ISSTARTBAR = false;
		ISWHILE = false;   //循环定位当前位置
		System.gc();
	}
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("onPause");
		isPause = true;
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
		isPause = false;
		if (mBMapMan != null) {
			mBMapMan.start();
		}
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			System.out.println("IsLock:" + IsLock);
			if(IsLock){
				//停止轨迹回访
				IsLock = false;
				for(int i = 0 ; i < mapOverLays.size() ; i++){
					if(mapOverLays.get(i).getClass().toString().indexOf("MyOverlay")>0){
						mapOverLays.remove(i);
						i--;
					}
				}
				//删除轨迹回访跑的图标
				for(int j = 0 ; j < mapOverLays.size() ; j++){
					if(mapOverLays.get(j).getClass().toString().indexOf("LocusOverlay")>0){
						mapOverLays.remove(j);
						j--;
					}
				}
				ShowAllCar();
				mMapController.animateTo(Point);
				ShowPop(item);
				return false;
			}else{
				AlertDialog.Builder bulder = new AlertDialog.Builder(AVTBDActivity.this);
				bulder.setTitle(R.string.Note);// 设置标题
				bulder.setMessage(R.string.exit_content);
				bulder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				bulder.setNegativeButton(android.R.string.cancel, null);
				bulder.show();
			}
			break;
		}
    	return super.onKeyDown(keyCode, event);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public boolean onDown(MotionEvent e) {
		return false;
	}
	public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,float velocityY) {
		try {
			if (me1.getX() - me2.getX() > 120 && Math.abs(velocityX) > 0) {
				// 设置View进入屏幕时使用的动画
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.left_in));
				// 设置View出屏幕时使用的动画
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.left_out));
				// 显示下一个View
				this.flipper.showNext();
				return true;
			} else if (me1.getX() - me2.getX() < -120 && Math.abs(velocityX) > 0) {
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.right_out));
				// 显示上一个View
				this.flipper.showPrevious();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	public void onLongPress(MotionEvent e) {
		
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}
	public void onShowPress(MotionEvent e) {
		
	}
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	boolean ISWHILE = true;
	/**
	 * 循环上传数据
	 * @author honesty
	 */
	class WhileThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (ISWHILE) {
				try {
					System.out.println("每隔60s上次一次");
					if(!isPause && p != null){
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("DeviceID", String.valueOf(userid)));
						params.add(new BasicNameValuePair("Lon", String.valueOf(p.getLongitudeE6())));
						params.add(new BasicNameValuePair("Lat", String.valueOf(p.getLatitudeE6())));
						params.add(new BasicNameValuePair("Speed", "0"));
						new Thread(new NetThread.postDataThread(handler, url, params, 999)).start();
					}
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}