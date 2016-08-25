package com.maxkrass.stundenplan.activities;

import android.app.SharedElementCallback;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.ActivityViewTeacherBinding;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;

import java.util.List;

public class ViewTeacherActivity extends BaseActivity implements Transition.TransitionListener {
	private static final String TAG = "ViewTeacherActivity";
	private ActivityViewTeacherBinding binding;

	private float targetTextSize;
	private ColorStateList targetTextColors;

	private final SharedElementCallback elementCallback = new SharedElementCallback() {
		@Override
		public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
			TextView teacherName = binding.viewTeacherName;
			targetTextSize = teacherName.getTextSize();
			targetTextColors = teacherName.getTextColors();
			teacherName.setTextColor(getIntent().getIntExtra("color", Color.BLACK));
			float textSize = getIntent().getFloatExtra("fontSize", targetTextSize);
			teacherName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			Rect padding = getIntent().getParcelableExtra("padding");
			teacherName.setPadding(padding.left, padding.top, padding.right, padding.bottom);
		}

		@Override
		public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
			TextView teacherName = binding.viewTeacherName;
			teacherName.setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
			teacherName.setTextColor(targetTextColors);
			//forceSharedElementLayout(binding.description);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		postponeEnterTransition();

		final View decor = getWindow().getDecorView();
		decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				decor.getViewTreeObserver().removeOnPreDrawListener(this);
				startPostponedEnterTransition();
				return true;
			}
		});
		Teacher teacher = (Teacher) getIntent().getSerializableExtra("teacher");
		if (teacher == null) {
			finish();
		} else {
			binding = DataBindingUtil.setContentView(this, R.layout.activity_view_teacher);
			binding.setTeacher(teacher);
			binding.viewTeacherToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finishAfterTransition();
				}
			});
			setEnterSharedElementCallback(elementCallback);
			getWindow().getSharedElementEnterTransition().excludeTarget(getWindow().getDecorView(), true).addListener(this);
			setActionBar(binding.viewTeacherToolbar);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			((View) binding.viewTeacherToolbar.getParent()).setPadding(0, Tools.getStatusBarHeight(this), 0, 0);
			//TODO Add onPress events to phone and email
			//TODO Add "subjects with this teachers"
			//TODO Link those subjects to their view Activities

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				binding.editTeacher.setVisibility(View.GONE);
				finishAfterTransition();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTransitionStart(Transition transition) {
		Log.d(TAG, "onTransitionStart() called with: " + "transition = [" + transition + "]");
	}

	@Override
	public void onTransitionEnd(Transition transition) {
		binding.editTeacher.show();
		Log.d(TAG, "onTransitionEnd() called with: " + "transition = [" + transition + "]");
	}

	@Override
	public void onTransitionCancel(Transition transition) {
		Log.d(TAG, "onTransitionCancel() called with: " + "transition = [" + transition + "]");
	}

	@Override
	public void onTransitionPause(Transition transition) {
		Log.d(TAG, "onTransitionPause() called with: " + "transition = [" + transition + "]");
	}

	@Override
	public void onTransitionResume(Transition transition) {
		Log.d(TAG, "onTransitionResume() called with: " + "transition = [" + transition + "]");
	}
}
