package com.maxkrass.stundenplan.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Period;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Weekday;
import com.maxkrass.stundenplan.views.CheckBoxWidget;
import com.orm.SugarRecord;

public class CreateLessonActivity extends BaseActivity {

	int SELECT_SUBJECT_REQUEST_CODE = 1067;
	int color;

	private View subjectColor;
	private TextView subjectName;
	private Subject selectedSubject;
	private TextView lessonRoom;
	private Spinner lessonWeekday;
	private Spinner lessonPeriod;
	private CheckBoxWidget doublePeriod;
	private View mRevealView;
	private View mRevealBackgroundView;
	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_lesson);
		findViewById(R.id.cancel_lesson).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mRevealView = findViewById(R.id.reveal);
		mRevealBackgroundView = findViewById(R.id.revealBackground);
		mToolbar = (Toolbar) findViewById(R.id.create_lesson_toolbar);
		subjectColor = findViewById(R.id.select_subject_color);
		subjectName = (TextView) findViewById(R.id.select_subject);
		doublePeriod = (CheckBoxWidget) findViewById(R.id.double_period);
		lessonRoom = (TextView) findViewById(R.id.lesson_room);
		lessonPeriod = (Spinner) findViewById(R.id.lesson_period);
		lessonWeekday = (Spinner) findViewById(R.id.lesson_weekday);
		color = Color.LIGHT_GREEN.getColor(this);

		if (savedInstanceState != null) {
			long subjectID = savedInstanceState.getLong("selectedSubjectID", 0);
			if (subjectID != 0) {
				selectedSubject = SugarRecord.findById(Subject.class, subjectID);
				subjectName.setText(selectedSubject.getName());
				subjectColor.setVisibility(View.VISIBLE);
				int newColor = Color.values()[selectedSubject.getColorIndex()].getColor(this);
				((GradientDrawable) subjectColor.getBackground()).setColor(newColor);
				mRevealView.setBackgroundColor(newColor);
				color = newColor;
			}
			lessonPeriod.setSelection(savedInstanceState.getInt("selectedPeriod"));
			lessonWeekday.setSelection(savedInstanceState.getInt("selectedWeekday"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (selectedSubject != null) outState.putLong("selectedSubjectID", selectedSubject.getId());
		outState.putInt("selectedWeekday", lessonWeekday.getSelectedItemPosition());
		outState.putInt("selectedPeriod", lessonPeriod.getSelectedItemPosition());
	}

	public void selectSubject(View view) {
		startActivityForResult(new Intent(CreateLessonActivity.this, ManageSubjectsActivity.class).putExtra("select", true), SELECT_SUBJECT_REQUEST_CODE);
	}

	public void saveLesson(View view) {
		if (selectedSubject != null) {

			Lesson lessonToSave = new Lesson();

			lessonToSave.setSubject(selectedSubject);
			lessonToSave.setLocation(lessonRoom.getText().toString());
			lessonToSave.setPeriod(SugarRecord.findById(Period.class, lessonPeriod.getSelectedItemPosition() + 1));
			lessonToSave.setWeekday(Weekday.values()[lessonWeekday.getSelectedItemPosition()]);

			lessonToSave.save();

			if (doublePeriod.isChecked() && lessonToSave.getPeriod().getId() < 10) {
				lessonToSave.setId(lessonToSave.getId() + 1);
				lessonToSave.setPeriod(SugarRecord.findById(Period.class, lessonToSave.getPeriod().getId() + 1));

				lessonToSave.save();
			}

			setResult(RESULT_OK, new Intent().putExtra("lessonID", lessonToSave.getId()));

			finish();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_SUBJECT_REQUEST_CODE) {

			if (resultCode == RESULT_OK) {

				int subjectID = data.getIntExtra("subjectID", 0);

				if (subjectID < SugarRecord.count(Subject.class) && subjectID >= 0) {
					selectedSubject = SugarRecord.listAll(Subject.class, "name").get(subjectID);
					subjectName.setText(selectedSubject.getName());
					subjectColor.setVisibility(View.VISIBLE);
					int newColor = Color.values()[selectedSubject.getColorIndex()].getColor(CreateLessonActivity.this);
					animateAppAndStatusBar(color, newColor);
					((GradientDrawable) subjectColor.getBackground()).setColor(newColor);
					color = newColor;

				}

			}

		}
	}

	private void animateAppAndStatusBar(int fromColor, final int toColor) {
		Animator animator = ViewAnimationUtils.createCircularReveal(
				mRevealView,
				mToolbar.getWidth() / 2,
				mToolbar.getHeight() / 2, 0,
				mToolbar.getWidth() / 2);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mRevealView.setBackgroundColor(toColor);
			}
		});

		mRevealBackgroundView.setBackgroundColor(fromColor);
		animator.setStartDelay(200);
		animator.setDuration(125);
		animator.start();
		mRevealView.setVisibility(View.VISIBLE);
	}

	public void onClick(View view) {
		if (view instanceof CheckBoxWidget)
			((CheckBoxWidget) view).toggle();
	}
}
