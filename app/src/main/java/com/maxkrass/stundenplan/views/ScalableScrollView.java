package com.maxkrass.stundenplan.views;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Max made this for Stundenplan on 26.05.2016.
 */

public class ScalableScrollView extends NestedScrollView {

	private ScaleGestureDetector scaleGestureDetector;

	public ScalableScrollView(Context context) {
		super(context);
	}

	public ScalableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScalableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return scaleGestureDetector.onTouchEvent(ev);
	}

	public void setScaleDetector(ScaleGestureDetector scaleDetector) {
		scaleGestureDetector = scaleDetector;
	}

}
