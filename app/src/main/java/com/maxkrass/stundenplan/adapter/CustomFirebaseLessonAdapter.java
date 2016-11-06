package com.maxkrass.stundenplan.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.Query;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateLessonActivity;
import com.maxkrass.stundenplan.databinding.LessonCardBinding;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Period;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Weekday;
import com.maxkrass.stundenplan.tools.Tools;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Max made this for Stundenplan2 on 24.07.2016.
 */
public class CustomFirebaseLessonAdapter {

	private final RelativeLayout            mColumn;
	private final CustomFirebaseArray       mSnapshots;
	private final Activity                  mActivity;
	private final List<Period>              mPeriods;
	private final ArrayMap<Integer, Long[]> originalMeasurements;
	private final Calendar                  firstPeriodStartTime;
	private final int                       padding;
	private boolean mShowRoomOnSingleLesson = true;
	private double  mScalingFactor          = 1.0;

	public CustomFirebaseLessonAdapter(Activity activity, RelativeLayout column, final Query ref, List<Period> periods) {
		mActivity = activity;
		mColumn = column;
		padding = (int) Tools.getPixels(2, activity);
		mPeriods = periods;
		originalMeasurements = new ArrayMap<>();

		firstPeriodStartTime = Calendar.getInstance();
		firstPeriodStartTime.set(Calendar.HOUR_OF_DAY, mPeriods.get(0).getStartHour());
		firstPeriodStartTime.set(Calendar.MINUTE, mPeriods.get(0).getStartMinute());

		mSnapshots = new CustomFirebaseArray(ref);
		mSnapshots.setOnChangedListener(new CustomFirebaseArray.OnChangedListener() {
			@Override
			public void onChanged(SparseArray<Lesson> lessons, HashMap<String, Subject> subjects) {
				mColumn.removeAllViews();
				originalMeasurements.clear();
				for (int i = 0; i < lessons.size(); i++) {
					Lesson lesson = lessons.valueAt(i);
					if (!isSucceedingLesson(lesson))
						addLesson(lesson, subjects.get(lesson.getSubject()));
				}
			}
		});
	}

	private void addLesson(final Lesson l, final Subject s) {
		LessonCardBinding lessonCardBinding = LessonCardBinding.inflate(mActivity.getLayoutInflater());

		CardView lesson = (CardView) lessonCardBinding.getRoot();

		lessonCardBinding.setLesson(l);
		lessonCardBinding.setSubject(s);

		String color = s.getColor();

		if (color.equals("#FFFFFF") || color.equals("#FFEB3B")) {
			lessonCardBinding.subjectAbbrLabel.setTextColor(0xDE000000);
			lessonCardBinding.roomLabel.setTextColor(0x8A000000);
		}

		lessonCardBinding.getLesson().setShowRoomLabel(mShowRoomOnSingleLesson);

		Calendar periodEndTime = Calendar.getInstance();
		periodEndTime.set(Calendar.HOUR_OF_DAY, mPeriods.get(l.getPeriod() + (hasSucceedingLesson(l) ? 1 : 0)).getEndHour());
		periodEndTime.set(Calendar.MINUTE, mPeriods.get(l.getPeriod() + (hasSucceedingLesson(l) ? 1 : 0)).getEndMinute());

		Calendar periodStartTime = Calendar.getInstance();
		periodStartTime.set(Calendar.HOUR_OF_DAY, mPeriods.get(l.getPeriod()).getStartHour());
		periodStartTime.set(Calendar.MINUTE, mPeriods.get(l.getPeriod()).getStartMinute());

		long startDifference = l.getPeriod() == 0 ? 0 : TimeUnit.MILLISECONDS.toMinutes(periodStartTime.getTimeInMillis() - firstPeriodStartTime.getTimeInMillis());

		long periodDifference = TimeUnit.MILLISECONDS.toMinutes(periodEndTime.getTimeInMillis() - periodStartTime.getTimeInMillis());

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Tools.getPixels((int) (periodDifference * mScalingFactor), mActivity));

		layoutParams.setMargins(padding, (int) Tools.getPixels((int) (startDifference * mScalingFactor), mActivity), padding, 0);

		lesson.setLayoutParams(layoutParams);

		lesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mActivity)
						.setTitle(s.getName())
						.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
									case 0:
										Intent intent = new Intent(mActivity, CreateLessonActivity.class);
										intent.putExtra("weekday", Weekday.valueOf(l.getWeekday().toUpperCase()));
										intent.putExtra("period", Integer.valueOf(l.getPeriod()));
										intent.putExtra("doublePeriod", Boolean.valueOf(hasSucceedingLesson(l)));
										mActivity.startActivity(intent);
										break;
									case 1:
										break;
								}
							}
						})
						.show();

			}
		});

		mColumn.addView(lesson);

		originalMeasurements.put(l.getPeriod(), new Long[]{startDifference, periodDifference});
	}

	public void cleanup() {
		mSnapshots.cleanup();
	}

	private void resizeColumn() {
		int offset = 0;
		for (int i = 0; i < mSnapshots.getCount(); i++) {
			Lesson l = mSnapshots.getItemByIndex(i);
			if (l != null) {
				if (isSucceedingLesson(l)) {
					offset++;
				} else {
					CardView lesson = (CardView) mColumn.getChildAt(i - offset);

					if (mShowRoomOnSingleLesson) {
						lesson.findViewById(R.id.room_label).setVisibility(View.VISIBLE);
					} else if (!hasSucceedingLesson(l)) {
						lesson.findViewById(R.id.room_label).setVisibility(View.GONE);
					}

					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lesson.getLayoutParams();

					Long[] measures = originalMeasurements.get(l.getPeriod());

					layoutParams.topMargin = (int) Tools.getPixels((int) (measures[0] * mScalingFactor), mActivity);

					layoutParams.height = (int) Tools.getPixels((int) (measures[1] * mScalingFactor), mActivity);
				}
			}
		}

		mColumn.requestLayout();
	}

	private boolean isSucceedingLesson(Lesson lesson) {
		Lesson potentialLesson = mSnapshots.getItem(lesson.getPeriod() - 1);
		return potentialLesson != null && Objects.equals(lesson.getPeriod(), potentialLesson.getPeriod() + 1) && lesson.getSubject().equals(potentialLesson.getSubject());
	}

	private boolean hasSucceedingLesson(Lesson lesson) {
		Lesson potentialLesson = mSnapshots.getItem(lesson.getPeriod() + 1);
		return potentialLesson != null && Objects.equals(lesson.getPeriod() + 1, potentialLesson.getPeriod()) && Objects.equals(lesson.getSubject(), potentialLesson.getSubject());
	}

	public void setScalingFactor(double scalingFactor) {
		this.mScalingFactor = scalingFactor;
		resizeColumn();
	}

	public void setShowRoomOnSingleLesson(boolean showRoomOnSingleLesson) {
		this.mShowRoomOnSingleLesson = showRoomOnSingleLesson;
	}
}
