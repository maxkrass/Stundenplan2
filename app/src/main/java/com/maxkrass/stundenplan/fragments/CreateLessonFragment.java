package com.maxkrass.stundenplan.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.contracts.CreateLessonContract;
import com.maxkrass.stundenplan.customViews.CheckBoxWidget;
import com.maxkrass.stundenplan.databinding.FragmentCreateLessonBinding;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Weekday;

/**
 * Max made this for Stundenplan2 on 22.07.2016.
 */
public class CreateLessonFragment extends Fragment implements CreateLessonContract.View, View.OnClickListener {

	private View                           subjectColor;
	private TextView                       subjectName;
	private Subject                        selectedSubject;
	private TextView                       lessonRoom;
	private Spinner                        lessonWeekday;
	private Spinner                        lessonPeriod;
	private CheckBoxWidget                 doublePeriod;
	private CreateLessonContract.Presenter mPresenter;
	private FragmentCreateLessonBinding    binding;
	private OnChooseSubjectListener        mOnChooseListener;

	public void onSubjectChosen(Subject subject) {
		selectedSubject = subject;
		subjectName.setText(subject.getName());
		subjectColor.setVisibility(View.VISIBLE);
		((GradientDrawable) subjectColor.getBackground()).setColor(Color.parseColor(subject.getColor()));
	}

	public void onNoSubjectChosen() {
		subjectName.setText(R.string.select_subject);
		subjectColor.setVisibility(View.GONE);
	}

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnChooseListener = (OnChooseSubjectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentCreateLessonBinding.inflate(inflater, container, false);

		subjectColor = binding.selectSubjectColor;
		subjectName = binding.selectSubject;
		lessonRoom = binding.lessonRoom;
		lessonWeekday = binding.lessonWeekday;
		lessonPeriod = binding.lessonPeriod;
		doublePeriod = binding.doublePeriod;
		binding.selectSubjectContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnChooseListener.onRequestChooseSubject();
			}
		});

		if (savedInstanceState != null) {
			Subject subject = (Subject) savedInstanceState.getSerializable("selectedSubject");
			if (subject != null) {
				selectedSubject = subject;
				subjectName.setText(selectedSubject.getName());
				subjectColor.setVisibility(View.VISIBLE);
				int newColor = selectedSubject.getColorInt();
				((GradientDrawable) subjectColor.getBackground()).setColor(newColor);
			}
			lessonPeriod.setSelection(savedInstanceState.getInt("selectedPeriod"));
			lessonWeekday.setSelection(savedInstanceState.getInt("selectedWeekday"));
		}

		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();
		mPresenter.start();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (selectedSubject != null) outState.putSerializable("selectedSubject", selectedSubject);
		outState.putInt("selectedWeekday", lessonWeekday.getSelectedItemPosition());
		outState.putInt("selectedPeriod", lessonPeriod.getSelectedItemPosition());
	}

	@Override
	public void showLesson(Lesson lesson, Boolean doublePeriod) {
		binding.setLesson(lesson);
		selectedSubject = lesson.getSubject();
		subjectName.setText(lesson.getSubject().getName());
		subjectColor.setVisibility(View.VISIBLE);
		((GradientDrawable) subjectColor.getBackground()).setColor(Color.parseColor(lesson.getSubject().getColor()));
		lessonWeekday.setSelection(Weekday.valueOf(lesson.getWeekday().toUpperCase()).ordinal());
		lessonPeriod.setSelection(lesson.getPeriod());
		lessonRoom.setText(lesson.getLocation());
		this.doublePeriod.setChecked(doublePeriod);
	}

	@Override
	public void showError(String error) {
		Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
	}

	@Override
	public void exitCreateDialog() {
		if (getActivity() != null)
			getActivity().finish();
	}

	@Override
	public void setPresenter(CreateLessonContract.Presenter presenter) {
		mPresenter = presenter;
	}

	public interface OnChooseSubjectListener {
		void onRequestChooseSubject();
	}

	@Override
	public void onClick(View v) {
		mPresenter.validateLesson(
				selectedSubject,
				lessonPeriod.getSelectedItemPosition(),
				lessonRoom.getText().toString(),
				Weekday.values()[lessonWeekday.getSelectedItemPosition()],
				doublePeriod.isChecked());
	}
}
