package com.maxkrass.stundenplan.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ZoomableRelativeLayout extends RelativeLayout {
	float mScaleFactor = 1;

	public ZoomableRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ZoomableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ZoomableRelativeLayout(Context context, AttributeSet attrs,
	                              int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	protected void dispatchDraw(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(1, mScaleFactor, 0, 0);
		super.dispatchDraw(canvas);
		canvas.restore();
	}

	public void scale(float scaleFactor) {
		mScaleFactor = scaleFactor;
		this.invalidate();
	}

	public void restore() {
		mScaleFactor = 1;
		this.invalidate();
	}

}