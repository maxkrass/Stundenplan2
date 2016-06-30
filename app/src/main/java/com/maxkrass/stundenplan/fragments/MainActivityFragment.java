package com.maxkrass.stundenplan.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

	RelativeLayout columnMonday;
	RelativeLayout columnTuesday;
	RelativeLayout columnWednesday;
	RelativeLayout columnThursday;
	RelativeLayout columnFriday;

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		columnMonday = (RelativeLayout) v.findViewById(R.id.column_monday);
		columnTuesday = (RelativeLayout) v.findViewById(R.id.column_tuesday);
		columnWednesday = (RelativeLayout) v.findViewById(R.id.column_wednesday);
		columnThursday = (RelativeLayout) v.findViewById(R.id.column_thursday);
		columnFriday = (RelativeLayout) v.findViewById(R.id.column_friday);

		loadAllLessons();

		return v;
	}

	public void loadAllLessons() {

		List<Lesson> lessons = SugarRecord.listAll(Lesson.class);

		for (Lesson l : lessons) addLesson(l);

	}

	public void addLesson(final Lesson l) {
		CardView lesson = (CardView) getActivity().getLayoutInflater().inflate(R.layout.lesson_card, null);

		lesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO create ViewLessonActivity
				//getActivity().startActivity(new Intent(getActivity(), ViewLessonActivity.class));
			}
		});

		lesson.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				LessonDialogFragment lessonDialogFragment = new LessonDialogFragment();
				Bundle bundle = new Bundle();
				bundle.putLong("lessonID", l.getId());
				lessonDialogFragment.setArguments(bundle);
				lessonDialogFragment.show(getFragmentManager(), "lessonDialog");
				return true;
			}
		});

		((TextView) lesson.findViewById(R.id.subject_abbr_label)).setText(l.getSubject().getAbbreviation());
		((TextView) lesson.findViewById(R.id.room_label)).setText(l.getLocation());
		lesson.setCardBackgroundColor(Color.values()[l.getSubject().getColorIndex()].getColor(getActivity()));

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Tools.getPixels(l.hasSucceedingLesson() ? 184 : 90, getActivity()));

		int fourDp = (int) Tools.getPixels(2, getActivity());

		layoutParams.setMargins(fourDp, (int) Tools.getPixels(l.getPeriod().getPeriodIndex() * 94, getActivity()), fourDp, 0);

		lesson.setLayoutParams(layoutParams);

		switch (l.getWeekday()) {

			case MONDAY:
				columnMonday.addView(lesson);
				break;
			case TUESDAY:
				columnTuesday.addView(lesson);
				break;
			case WEDNESDAY:
				columnWednesday.addView(lesson);
				break;
			case THURSDAY:
				columnThursday.addView(lesson);
				break;
			case FRIDAY:
				columnFriday.addView(lesson);
				break;

		}
	}
}
