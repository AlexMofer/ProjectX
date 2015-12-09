package com.am.widget.supergridview;

import android.graphics.Bitmap;
import android.view.View;

public class DragController {
	
	/**
	 * 初始化拖拽View
	 */
	public Bitmap setDragView(View tagView) {
		tagView.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(tagView.getDrawingCache());
		tagView.destroyDrawingCache();
		return bitmap;
	}

}
