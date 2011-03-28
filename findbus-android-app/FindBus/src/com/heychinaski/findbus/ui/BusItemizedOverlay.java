package com.heychinaski.findbus.ui;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;

import com.google.android.maps.*;

import java.util.*;

/**
 * @author pestrella
 */
public class BusItemizedOverlay extends ItemizedOverlay<OverlayItem> {
  private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
  private Context context;

  public BusItemizedOverlay(Drawable defaultMarker) {
    super(boundCenterBottom(defaultMarker));
  }

  public BusItemizedOverlay(Drawable defaultMarker, Context context) {
    this(defaultMarker);
    this.context = context;
  }

  public void addOverlay(OverlayItem overlay) {
    overlays.add(overlay);
    populate();
  }

  @Override
  protected OverlayItem createItem(int i) {
    return overlays.get(i);
  }

  @Override
  public int size() {
    return overlays.size();
  }

  @Override
  public boolean onTap(int index) {
    if (context == null)
      return false;

    OverlayItem item = overlays.get(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    dialog.setTitle(item.getTitle());
    dialog.setMessage(item.getSnippet());
    dialog.show();
    return true;
  }
}