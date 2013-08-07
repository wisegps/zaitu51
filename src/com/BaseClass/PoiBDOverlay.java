package com.BaseClass;

import java.util.ArrayList;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class PoiBDOverlay extends ItemizedOverlay<OverlayItem> {
	ArrayList<OverlayItem> poiLayItems = new ArrayList<OverlayItem>();
	Context mContext;
	String icon;
	GeoPoint getPoint;
	boolean IsSatellite;
	int mSize;

	public PoiBDOverlay(Drawable Marker, Context context,String icon, GeoPoint getPoint,boolean IsSatellite,int size) {
		this(Marker);
		this.mContext = context;
		this.icon = icon;
		this.getPoint = getPoint;
		this.IsSatellite = IsSatellite;
		this.mSize = size;
	}
	
	public PoiBDOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point point = new Point();
		mapView.getProjection().toPixels(getPoint, point);
		if(IsSatellite){
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			paint.setTextSize(mSize);
			paint.setAntiAlias(true);
			canvas.drawText(icon, point.x + 15 , point.y - 8, paint);
		}else{
			Paint paint = new Paint();
			paint.setARGB(255, 0, 0, 0);
			paint.setTextSize(mSize);
			paint.setAntiAlias(true);
			canvas.drawText(icon, point.x + 15 , point.y - 8, paint);
		}
	}

	@Override
	protected OverlayItem createItem(int i) {
		return poiLayItems.get(i);
	}

	@Override
	public int size() {
		return poiLayItems.size();
	}

	// 点击时触发
	protected boolean onTap(final int index) {
		return true;
	}

	// 添加标记
	public void addOverLay(OverlayItem overLay) {
		poiLayItems.add(overLay);
		this.populate();
	}
}
