package com.maxkrass.stundenplan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.ActivityMainBinding;
import com.maxkrass.stundenplan.databinding.LessonCardBinding;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Period;
import com.maxkrass.stundenplan.receiver.BootReceiver;
import com.maxkrass.stundenplan.receiver.NotificationReceiver;
import com.maxkrass.stundenplan.services.NotificationService;
import com.maxkrass.stundenplan.tools.Tools;
import com.maxkrass.stundenplan.views.ScalableScrollView;
import com.orm.SugarRecord;
import com.orm.query.Select;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

//import android.util.Log;

public class MainActivity extends BaseActivity {

	private double mScalingFactor = 1;
	private double mLastScalingFactor = 1;
	private boolean showRoomOnSingleLesson = true;

	private int fourDp;

	private List<Period> periods = SugarRecord.listAll(Period.class);
	private List<Lesson> mondayLessons;
	private List<Lesson> tuesdayLessons;
	private List<Lesson> wednesdayLessons;
	private List<Lesson> thursdayLessons;
	private List<Lesson> fridayLessons;
	private Map<Long, Long[]> originalMeasures;

	private RelativeLayout columnMonday;
	private RelativeLayout columnTuesday;
	private RelativeLayout columnWednesday;
	private RelativeLayout columnThursday;
	private RelativeLayout columnFriday;

	private Calendar firstPeriodStartTime;

	//private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(MainActivity.this, NotificationService.class));
		final ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
		fourDp = (int) Tools.getPixels(2, MainActivity.this);
		if (SugarRecord.count(Period.class) < 1) {
			new Period(8, 10, 8, 55).save();
			new Period(9, 0, 9, 45).save();
			new Period(9, 50, 10, 35).save();

			new Period(11, 5, 11, 50).save();
			new Period(11, 55, 12, 40).save();
			new Period(12, 45, 13, 30).save();

			new Period(14, 15, 15, 0).save();
			new Period(15, 0, 15, 45).save();

			new Period(15, 55, 16, 40).save();
			new Period(16, 40, 17, 25).save();
		}
		firstPeriodStartTime = Calendar.getInstance();
		firstPeriodStartTime.set(Calendar.HOUR_OF_DAY, periods.get(0).getStartHour());
		firstPeriodStartTime.set(Calendar.MINUTE, periods.get(0).getStartMinute());
		Toolbar toolbar = activityMainBinding.mainToolbar;
		((ViewGroup) toolbar.getParent()).setPadding(0, Tools.getStatusBarHeight(MainActivity.this), 0, 0);
		setActionBar(toolbar);
		columnMonday = activityMainBinding.columnMonday;
		columnTuesday = activityMainBinding.columnTuesday;
		columnWednesday = activityMainBinding.columnWednesday;
		columnThursday = activityMainBinding.columnThursday;
		columnFriday = activityMainBinding.columnFriday;
		ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new OnPinchListener());
		final ScalableScrollView scalableScrollView = activityMainBinding.scrollviewLesson;
		scalableScrollView.setScaleDetector(scaleGestureDetector);
		scalableScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (scalableScrollView.getScrollY() == 0) activityMainBinding.addLesson.show();
			}
		});
		loadAllLessons();
		mondayLessons = Select.from(Lesson.class).where("weekday = 'MONDAY'").list();
		tuesdayLessons = Select.from(Lesson.class).where("weekday = 'TUESDAY'").list();
		wednesdayLessons = Select.from(Lesson.class).where("weekday = 'WEDNESDAY'").list();
		thursdayLessons = Select.from(Lesson.class).where("weekday = 'THURSDAY'").list();
		fridayLessons = Select.from(Lesson.class).where("weekday = 'FRIDAY'").list();
		Collections.sort(mondayLessons);
		Collections.sort(tuesdayLessons);
		Collections.sort(wednesdayLessons);
		Collections.sort(thursdayLessons);
		Collections.sort(fridayLessons);
		switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				columnMonday.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.divider_black));
				break;
			case Calendar.TUESDAY:
				columnTuesday.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.divider_black));
				break;
			case Calendar.WEDNESDAY:
				columnWednesday.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.divider_black));
				break;
			case Calendar.THURSDAY:
				columnThursday.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.divider_black));
				break;
			case Calendar.FRIDAY:
				columnFriday.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.divider_black));
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_NEW_LESSON) {

			if (resultCode == RESULT_OK) {

				addLesson(SugarRecord.findById(Lesson.class, data.getLongExtra("lessonID", 0)));

			}

		}
	}

	public void addLesson(View v) {
		startActivityForResult(new Intent(MainActivity.this, CreateLessonActivity.class), REQUEST_CODE_NEW_LESSON);
	}

	private void resizeColumn(RelativeLayout column, List<Lesson> lessons) {
		for (int i = 0; i < column.getChildCount(); i++) {
			CardView lesson = (CardView) column.getChildAt(i);

			if (showRoomOnSingleLesson) {
				lesson.findViewById(R.id.room_label).setVisibility(View.VISIBLE);
			} else if (!lessons.get(i).hasSucceedingLesson()) {
				lesson.findViewById(R.id.room_label).setVisibility(View.GONE);
			}

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lesson.getLayoutParams();

			long lessonId = lessons.get(i).getId();

			Long[] measures = originalMeasures.get(lessonId);

			layoutParams.topMargin = (int) Tools.getPixels((int) (measures[0] * mScalingFactor), this);

			layoutParams.height = (int) Tools.getPixels((int) (measures[1] * mScalingFactor), this);
		}

		column.requestLayout();
	}

	private void resizeLessons() {

		//long start = System.nanoTime();

		resizeColumn(columnMonday, mondayLessons);

		resizeColumn(columnTuesday, tuesdayLessons);

		resizeColumn(columnWednesday, wednesdayLessons);

		resizeColumn(columnThursday, thursdayLessons);

		resizeColumn(columnFriday, fridayLessons);

		//Log.d(TAG, "resizeLessons: Took " + (System.nanoTime() - start) / 1000 + "µs with Scale Factor: " + mScalingFactor);
	}

	private void loadAllLessons() {

		List<Lesson> lessons = SugarRecord.listAll(Lesson.class);

		Collections.sort(lessons);

		//long start = System.currentTimeMillis();

		originalMeasures = new TreeMap<>();

		for (Lesson l : lessons) if (!l.isSucceedingLesson()) addLesson(l);

		//Log.d(TAG, "loadAllLessons: Took " + (System.currentTimeMillis() - start) + "ms with Scale Factor: " + mScalingFactor);

	}

	private void addLesson(final Lesson l) {
		LessonCardBinding lessonCardBinding = LessonCardBinding.inflate(getLayoutInflater());

		CardView lesson = (CardView) lessonCardBinding.getRoot();

		lessonCardBinding.setLesson(l);
		int colorIndex = l.getSubject().getColorIndex();

		if (colorIndex == 12 || colorIndex == 20) {
			lessonCardBinding.subjectAbbrLabel.setTextColor(0xDE000000);
			lessonCardBinding.roomLabel.setTextColor(0x8A000000);
		}

		lessonCardBinding.getLesson().setShowRoomLabel(showRoomOnSingleLesson);

		Calendar periodEndTime = Calendar.getInstance();
		periodEndTime.set(Calendar.HOUR_OF_DAY, periods.get(l.getPeriod().getPeriodIndex() + (l.hasSucceedingLesson() ? 1 : 0)).getEndHour());
		periodEndTime.set(Calendar.MINUTE, periods.get(l.getPeriod().getPeriodIndex() + (l.hasSucceedingLesson() ? 1 : 0)).getEndMinute());

		Calendar periodStartTime = Calendar.getInstance();
		periodStartTime.set(Calendar.HOUR_OF_DAY, periods.get(l.getPeriod().getPeriodIndex()).getStartHour());
		periodStartTime.set(Calendar.MINUTE, periods.get(l.getPeriod().getPeriodIndex()).getStartMinute());

		long startDifference = l.getPeriod().getPeriodIndex() == 0 ? 0 : TimeUnit.MILLISECONDS.toMinutes(periodStartTime.getTimeInMillis() - firstPeriodStartTime.getTimeInMillis());

		long periodDifference = TimeUnit.MILLISECONDS.toMinutes(periodEndTime.getTimeInMillis() - periodStartTime.getTimeInMillis());

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Tools.getPixels((int) (periodDifference * mScalingFactor), MainActivity.this));

		layoutParams.setMargins(fourDp, (int) Tools.getPixels((int) (startDifference * mScalingFactor), MainActivity.this), fourDp, 0);

		lesson.setLayoutParams(layoutParams);

		lesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle(l.getSubject().getName())
						.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
									case 1:
										break;
									case 2:
										break;
								}
							}
						})
						.show();

			}
		});

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

		originalMeasures.put(l.getId(), new Long[]{startDifference, periodDifference});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.manage_subjects:
				startActivity(new Intent(this, ManageSubjectsActivity.class).putExtra("select", false));
				break;
			case R.id.manage_teachers:
				startActivity(new Intent(this, ManageTeacherActivity.class));
				break;
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
		}

		return super.onOptionsItemSelected(item);
	}

	private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		float startingSpan;

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			mLastScalingFactor = mScalingFactor;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			startingSpan = detector.getCurrentSpanY();
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScalingFactor = mLastScalingFactor * detector.getCurrentSpanY() / startingSpan;
			if (mScalingFactor < 0.75) {
				mScalingFactor = 0.75;
			} else if (mScalingFactor < 1.0) {
				showRoomOnSingleLesson = false;
			} else if (mScalingFactor > 1.5) {
				mScalingFactor = 1.5;
			} else {
				showRoomOnSingleLesson = true;
			}

			resizeLessons();

			return true;
		}
	}

}
