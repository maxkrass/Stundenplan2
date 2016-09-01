package com.maxkrass.stundenplan.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateLessonActivity;
import com.maxkrass.stundenplan.adapter.CustomFirebaseLessonAdapter;
import com.maxkrass.stundenplan.customViews.ScalableScrollView;
import com.maxkrass.stundenplan.databinding.FragmentMainBinding;
import com.maxkrass.stundenplan.objects.Period;
import com.maxkrass.stundenplan.objects.Weekday;
import com.maxkrass.stundenplan.services.NotificationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

	private double mScalingFactor = 1;
	private double mLastScalingFactor = 1;
	private boolean showRoomOnSingleLesson = true;

	private DatabaseReference mPeriodRef;
	private DatabaseReference mLessonRef;

	private List<Period> periods;

	private CustomFirebaseLessonAdapter mondayAdapter;
	private CustomFirebaseLessonAdapter tuesdayAdapter;
	private CustomFirebaseLessonAdapter wednesdayAdapter;
	private CustomFirebaseLessonAdapter thursdayAdapter;
	private CustomFirebaseLessonAdapter fridayAdapter;
	
	private RelativeLayout columnMonday;
	private RelativeLayout columnTuesday;
	private RelativeLayout columnWednesday;
	private RelativeLayout columnThursday;
	private RelativeLayout columnFriday;

	private Calendar firstPeriodStartTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().startService(new Intent(getActivity(), NotificationService.class));
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		assert user != null;
		String uId = user.getUid();
		mPeriodRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("stundenplan")
				.child("publicPeriods");
		mLessonRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uId)
				.child("lessons");
		mPeriodRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (!dataSnapshot.hasChildren()) {
					periods = new ArrayList<>();
					periods.add(new Period(0, 8, 10, 8, 55));
					periods.add(new Period(1, 9, 0, 9, 45));
					periods.add(new Period(2, 9, 50, 10, 35));
					periods.add(new Period(3, 11, 5, 11, 50));
					periods.add(new Period(4, 11, 55, 12, 40));
					periods.add(new Period(5, 12, 45, 13, 30));
					periods.add(new Period(6, 14, 15, 15, 0));
					periods.add(new Period(7, 15, 0, 15, 45));
					periods.add(new Period(8, 15, 55, 16, 40));
					periods.add(new Period(9, 16, 40, 17, 25));

					mPeriodRef.setValue(periods);
				} else {
					periods = dataSnapshot.getValue(new GenericTypeIndicator<List<Period>>() {
					});
				}
				firstPeriodStartTime = Calendar.getInstance();
				firstPeriodStartTime.set(Calendar.HOUR_OF_DAY, periods.get(0).getStartHour());
				firstPeriodStartTime.set(Calendar.MINUTE, periods.get(0).getStartMinute());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final FragmentMainBinding fragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
		columnMonday = fragmentMainBinding.columnMonday;
		columnTuesday = fragmentMainBinding.columnTuesday;
		columnWednesday = fragmentMainBinding.columnWednesday;
		columnThursday = fragmentMainBinding.columnThursday;
		columnFriday = fragmentMainBinding.columnFriday;
		fragmentMainBinding.addLesson.setOnClickListener(this);
		ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getActivity(), new OnPinchListener());
		final ScalableScrollView scalableScrollView = fragmentMainBinding.scrollviewLesson;
		scalableScrollView.setScaleDetector(scaleGestureDetector);
		scalableScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				if (scalableScrollView.getScrollY() == 0) fragmentMainBinding.addLesson.show();
			}
		});
		switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				columnMonday.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider_black));
				break;
			case Calendar.TUESDAY:
				columnTuesday.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider_black));
				break;
			case Calendar.WEDNESDAY:
				columnWednesday.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider_black));
				break;
			case Calendar.THURSDAY:
				columnThursday.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider_black));
				break;
			case Calendar.FRIDAY:
				columnFriday.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider_black));
				break;
		}

		mPeriodRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (!dataSnapshot.hasChildren()) {
					periods = new ArrayList<>();
					periods.add(new Period(0, 8, 10, 8, 55));
					periods.add(new Period(1, 9, 0, 9, 45));
					periods.add(new Period(2, 9, 50, 10, 35));
					periods.add(new Period(3, 11, 5, 11, 50));
					periods.add(new Period(4, 11, 55, 12, 40));
					periods.add(new Period(5, 12, 45, 13, 30));
					periods.add(new Period(6, 14, 15, 15, 0));
					periods.add(new Period(7, 15, 0, 15, 45));
					periods.add(new Period(8, 15, 55, 16, 40));
					periods.add(new Period(9, 16, 40, 17, 25));

					mPeriodRef.setValue(periods);
				} else {
					periods = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Period>>() {
					});
				}

				mondayAdapter = new CustomFirebaseLessonAdapter(getActivity(), columnMonday, mLessonRef.child(Weekday.MONDAY.toString()), periods);
				tuesdayAdapter = new CustomFirebaseLessonAdapter(getActivity(), columnTuesday, mLessonRef.child(Weekday.TUESDAY.toString()), periods);
				wednesdayAdapter = new CustomFirebaseLessonAdapter(getActivity(), columnWednesday, mLessonRef.child(Weekday.WEDNESDAY.toString()), periods);
				thursdayAdapter = new CustomFirebaseLessonAdapter(getActivity(), columnThursday, mLessonRef.child(Weekday.THURSDAY.toString()), periods);
				fridayAdapter = new CustomFirebaseLessonAdapter(getActivity(), columnFriday, mLessonRef.child(Weekday.FRIDAY.toString()), periods);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		return fragmentMainBinding.getRoot();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mondayAdapter.cleanup();
		tuesdayAdapter.cleanup();
		wednesdayAdapter.cleanup();
		thursdayAdapter.cleanup();
		fridayAdapter.cleanup();
	}

	private void addLesson() {
		startActivity(new Intent(getActivity(), CreateLessonActivity.class));
	}

	@Override
	public void onClick(View v) {
		addLesson();
	}

	private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		float startingSpan;

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

			mondayAdapter.setShowRoomOnSingleLesson(showRoomOnSingleLesson);
			tuesdayAdapter.setShowRoomOnSingleLesson(showRoomOnSingleLesson);
			wednesdayAdapter.setShowRoomOnSingleLesson(showRoomOnSingleLesson);
			thursdayAdapter.setShowRoomOnSingleLesson(showRoomOnSingleLesson);
			fridayAdapter.setShowRoomOnSingleLesson(showRoomOnSingleLesson);

			mondayAdapter.setScalingFactor(mScalingFactor);
			tuesdayAdapter.setScalingFactor(mScalingFactor);
			wednesdayAdapter.setScalingFactor(mScalingFactor);
			thursdayAdapter.setScalingFactor(mScalingFactor);
			fridayAdapter.setScalingFactor(mScalingFactor);


			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			startingSpan = detector.getCurrentSpanY();
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			mLastScalingFactor = mScalingFactor;
		}
	}
}
