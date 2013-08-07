package com.BaseClass;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.wise.zaitu1.AVTActivity;

public class CarLocationOverlay extends ItemizedOverlay<OverlayItem> {
	ArrayList<OverlayItem> mapOverLays = new ArrayList<OverlayItem>();
	Drawable marker;
	Context mContext;
	String car_id;
	GeoPoint getPoint;
	Boolean isSearch;
	Boolean IsSatellite;
	AVTActivity mAvtActivity;
	int mSize;

	public CarLocationOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}
	public CarLocationOverlay(Drawable Marker, Context context,String car_id, GeoPoint getPoint,boolean ISSEARCH,boolean IsSatellite,AVTActivity AVT,int size) {
		super(Marker);
		this.marker = Marker;
		this.mContext = context;
		this.car_id = car_id;
		this.getPoint = getPoint;
		this.isSearch = ISSEARCH;
		this.IsSatellite = IsSatellite;
		this.mAvtActivity = AVT;
		this.mSize = size;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverLays.get(i);
	}

	@Override
	public int size() {
		return mapOverLays.size();
	}
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
		Point point = new Point();
		mapView.getProjection().toPixels(getPoint, point);
		if (IsSatellite) {
			Paint paint1 = new Paint();
			paint1.setARGB(255, 255, 255, 255);
			paint1.setTextSize(mSize);
			paint1.setAntiAlias(true);
			canvas.drawText(car_id, point.x + 15, point.y -8, paint1);
		}else {
			Paint paint = new Paint();
			//paint.setTextSkewX(-0.5f);
			paint.setARGB(255, 0, 0, 0);
			paint.setTextSize(mSize);
			paint.setAntiAlias(true);
			canvas.drawText(car_id, point.x + 15, point.y -8, paint);
		}
		
		if(isSearch){
			Paint innerPaint = new Paint();  
	        Paint borderPaint = new Paint();  
	        innerPaint.setARGB(75, 167, 222, 224); // darkgray  
	        borderPaint.setARGB(75, 4, 110, 114); // white  
	        borderPaint.setAntiAlias(true); // �����  
	        borderPaint.setStyle(Style.STROKE); //��ߣ���Style.Fill���  
	        borderPaint.setStrokeWidth(2);  
	        
	        Projection projection = mapView.getProjection();
			Point p1 = new Point();
			projection.toPixels(getPoint, p1);
			
			canvas.drawCircle(p1.x, p1.y, 30, innerPaint);
			canvas.drawCircle(p1.x, p1.y, 30, borderPaint);
		}
	}
	// ���ʱ����
	protected boolean onTap(int index) {
		mAvtActivity.Interactive(car_id);
		return true;
	}
	// ��ӱ��
	public void addOverLay(OverlayItem overLay) {
		mapOverLays.add(overLay);
		this.populate();
	}
}
