package com.maxkrass.stundenplan.data;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Weekday;

/**
 * Max made this for Stundenplan2 on 22.07.2016.
 */
public class LessonRepository {

	private final DatabaseReference mLessonRef;

	public LessonRepository(String uId) {
		mLessonRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uId)
				.child("lessons");
	}

	public void getLesson(Weekday weekday, Integer period, ValueEventListener listener) {
		mLessonRef.child(weekday.toString()).child(period.toString()).addListenerForSingleValueEvent(listener);
	}

	public void lessonExists(Weekday weekday, ValueEventListener listener) {
		mLessonRef.child(weekday.toString()).addListenerForSingleValueEvent(listener);
	}

	public void createLesson(String subject, Weekday weekday, Integer period, String location, boolean doublePeriod, OnCompleteListener<Void> listener) {
		mLessonRef.child(weekday.toString())
				.child(period.toString())
				.setValue(new Lesson(subject, period, weekday.toString(), location))
				.addOnCompleteListener(listener);

		if (doublePeriod) {
			mLessonRef.child(weekday.toString())
					.child(String.valueOf((period + 1)))
					.setValue(new Lesson(subject, period + 1, weekday.toString(), location))
					.addOnCompleteListener(listener);
		}

		//TODO addLessonToSubject
	}

	public void updateLesson(String subject, Weekday oldWeekday, Weekday newWeekday, Integer oldPeriod, Integer newPeriod, String location, Boolean wasDoublePeriod, Boolean isDoublePeriod, OnCompleteListener<Void> listener) {
		mLessonRef
				.child(oldWeekday.toString())
				.child(String.valueOf(oldPeriod))
				.removeValue();

		if (wasDoublePeriod) {
			mLessonRef
					.child(oldWeekday.toString())
					.child(String.valueOf(oldPeriod + 1))
					.removeValue();
		}

		mLessonRef
				.child(newWeekday.toString())
				.child(newPeriod.toString())
				.setValue(new Lesson(subject, newPeriod, newWeekday.toString(), location))
				.addOnCompleteListener(listener);

		if (isDoublePeriod) {
			mLessonRef.child(newWeekday.toString())
					.child(String.valueOf((newPeriod + 1)))
					.setValue(new Lesson(subject, newPeriod + 1, newWeekday.toString(), location));
		}

		//TODO addLessonToSubject
	}

	public void getSubject(String subjectName, ValueEventListener listener) {
		mLessonRef.getParent().child("subjects").child(subjectName).addListenerForSingleValueEvent(listener);
	}
}
