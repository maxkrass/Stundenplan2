package com.maxkrass.stundenplan.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.contracts.CreateLessonContract;
import com.maxkrass.stundenplan.data.LessonRepository;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Weekday;

import java.util.Objects;

/**
 * Max made this for Stundenplan2 on 22.07.2016.
 */
public class CreateLessonPresenter implements CreateLessonContract.Presenter, ValueEventListener {

	@NonNull
	private final CreateLessonContract.View mCreateLessonView;
	@NonNull
	private final LessonRepository          mLessonRepository;
	@Nullable
	private final Weekday                   mWeekday;
	@Nullable
	private final Integer                   mPeriod;
	@Nullable
	private final Boolean                   mDoublePeriod;

	public CreateLessonPresenter(@NonNull CreateLessonContract.View createLessonView, @NonNull LessonRepository lessonRepository, @Nullable Weekday weekday, @Nullable Integer period, @Nullable Boolean doublePeriod) {
		mCreateLessonView = createLessonView;
		mCreateLessonView.setPresenter(this);
		mLessonRepository = lessonRepository;
		mWeekday = weekday;
		mPeriod = period;
		mDoublePeriod = doublePeriod;
	}

	@Override
	public void validateLesson(final Subject subject, final Integer period, final String location, final Weekday weekday, final boolean doublePeriod) {
		if (subject == null) {
			mCreateLessonView.showError("You must select a Subject");
		} else {
			if (isNewLesson() || subjectTimeWasChanged(weekday, period, doublePeriod)) {
				mLessonRepository.lessonExists(weekday, new ValueEventListener() {

					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						boolean error = false;
						if (dataSnapshot.hasChild(period.toString())) {
							mCreateLessonView.showError("You already have a lesson " + weekday.toString() + "s " + (period + 1) + ". period");
							error = true;
						}
						if (doublePeriod && dataSnapshot.hasChild((period + 1) + "")) {
							mCreateLessonView.showError("You already have a lesson " + weekday.toString() + "s " + (period + 2) + ". period");
							error = true;
						}
						if (!error) {
							saveLesson(subject, period, location, weekday, doublePeriod);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
			} else {
				saveLesson(subject, period, location, weekday, doublePeriod);
			}
		}
	}

	private void saveLesson(Subject subject, Integer period, String location, Weekday weekday, boolean doublePeriod) {
		OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) mCreateLessonView.exitCreateDialog();
				else mCreateLessonView.showError("Saving Failed!");
			}
		};

		if (isNewLesson())
			mLessonRepository.createLesson(subject, weekday, period, location, doublePeriod, listener);
		else {
			mLessonRepository.updateLesson(subject, mWeekday, weekday, mPeriod, period, location, mDoublePeriod, doublePeriod, listener);
		}
	}

	private boolean subjectTimeWasChanged(Weekday weekday, Integer period, boolean doublePeriod) {
		return !Objects.equals(weekday, mWeekday) || !Objects.equals(period, mPeriod) || !Objects.equals(doublePeriod, mDoublePeriod);
	}

	@Override
	public void start() {
		if (!isNewLesson()) {
			mLessonRepository.getLesson(mWeekday, mPeriod, this);
		}
	}

	private boolean isNewLesson() {
		return mWeekday == null || mPeriod == null || mDoublePeriod == null;
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		mCreateLessonView.showLesson(dataSnapshot.getValue(Lesson.class), mDoublePeriod);
	}

	@Override
	public void onCancelled(DatabaseError databaseError) {
		mCreateLessonView.exitCreateDialog();
	}
}
