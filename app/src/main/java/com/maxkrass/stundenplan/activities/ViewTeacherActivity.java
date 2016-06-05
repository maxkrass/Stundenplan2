package com.maxkrass.stundenplan.activities;

import android.app.SharedElementCallback;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.ActivityViewTeacherBinding;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

import java.util.List;

public class ViewTeacherActivity extends BaseActivity {
	ActivityViewTeacherBinding binding;

	private float targetTextSize;
	private ColorStateList targetTextColors;

	private SharedElementCallback elementCallback = new SharedElementCallback() {
		@Override
		public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
			TextView teacherName = binding.viewTeacherName;
			teacherName.setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
			teacherName.setTextColor(targetTextColors);
			//forceSharedElementLayout(binding.description);
		}

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
		Teacher teacher = SugarRecord.findById(Teacher.class, getIntent().getLongExtra("teacherID", 0));
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
			getWindow().getSharedElementEnterTransition().excludeTarget(getWindow().getDecorView(), true);
			//Transition transition = TransitionUtils.makeSharedElementEnterTransition();
			//getWindow().setSharedElementEnterTransition(transition);
			//getWindow().setEnterTransition(TransitionUtils.makeEnterTransition());
			//setEnterSharedElementCallback(new EnterSharedElementCallback(this));
			setActionBar(binding.viewTeacherToolbar);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			((View) binding.viewTeacherToolbar.getParent()).setPadding(0, Tools.getStatusBarHeight(this), 0, 0);
			//getWindow().setEnterTransition(new Slide(Gravity.BOTTOM).excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
			//getWindow().setExitTransition(new Slide(Gravity.BOTTOM).excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
			//TODO Add onPress events to phone and email
			//TODO Add "subjects with this teachers"
			//TODO Link those subjects to their view Activities

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finishAfterTransition();
		}
		return super.onOptionsItemSelected(item);
	}
}
