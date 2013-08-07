package com.BaseClass;

import java.util.ArrayList;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class LocusBDOverlay extends ItemizedOverlay<OverlayItem>{
	ArrayList<OverlayItem> mapOverLays = new ArrayList<OverlayItem>();
	public LocusBDOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}
	@Override
	protected OverlayItem createItem(int i) {
		return mapOverLays.get(i);
	}	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
	}
	@Override
	public int size() {
		return mapOverLays.size();
	}
	// Ìí¼Ó±ê¼Ç
	public void addOverLay(OverlayItem overLay) {
		mapOverLays.add(overLay);
		this.populate();
	}
}