package com.BaseClass;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocusOverlay extends ItemizedOverlay<OverlayItem>{
	ArrayList<OverlayItem> mapOverLays = new ArrayList<OverlayItem>();
	public LocusOverlay(Drawable defaultMarker) {
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
