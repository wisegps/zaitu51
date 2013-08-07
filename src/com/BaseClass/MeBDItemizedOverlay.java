package com.BaseClass;

import java.util.ArrayList;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class MeBDItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	private GeoPoint p;
	private float Accuracy;
	
	public MeBDItemizedOverlay(Drawable defaDrawable,Context context,GeoPoint p,float Accuracy){
		this(defaDrawable);
		this.p = p;
		this.Accuracy = Accuracy;
	}

	public MeBDItemizedOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
		super.draw(canvas, mapView, shadow);  
    	Paint innerPaint = new Paint();  
        Paint borderPaint = new Paint();  
        innerPaint.setARGB(75, 167, 222, 224); // darkgray  
        borderPaint.setARGB(75, 4, 110, 114); // white  
        borderPaint.setAntiAlias(true); // 抗锯齿  
        borderPaint.setStyle(Style.STROKE); //描边，和Style.Fill相对  
        borderPaint.setStrokeWidth(2);  
        
        Projection projection = mapView.getProjection();
		Point p1 = new Point();
		projection.toPixels(p, p1);
		
		canvas.drawCircle(p1.x, p1.y, projection.metersToEquatorPixels(Accuracy), innerPaint);
		canvas.drawCircle(p1.x, p1.y, projection.metersToEquatorPixels(Accuracy), borderPaint);
        return true;  
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}
	// 添加标记
	public void addOverLay(OverlayItem overLay) {
		mapOverlays.add(overLay);
		this.populate();
	}
}