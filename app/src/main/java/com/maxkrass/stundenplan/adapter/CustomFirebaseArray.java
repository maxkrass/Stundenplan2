package com.maxkrass.stundenplan.adapter;

import android.util.SparseArray;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Subject;

import java.util.HashMap;

/**
 * Max made this for Stundenplan2 on 24.07.2016.
 */
class CustomFirebaseArray implements ValueEventListener {
	private final Query               mLessonQuery;
	private       OnChangedListener   mListener;
	private       SparseArray<Lesson> mLessons;

	CustomFirebaseArray(Query ref) {
		mLessonQuery = ref;
		mLessons = new SparseArray<>(10);
		mLessonQuery.orderByValue();
		mLessonQuery.addValueEventListener(this);
	}

	void cleanup() {
		mLessonQuery.removeEventListener(this);
	}

	int getCount() {
		return mLessons.size();
	}

	Lesson getItem(int key) {
		return mLessons.get(key);
	}

	Lesson getItemByIndex(int index) {
		return mLessons.valueAt(index);
	}

	void setOnChangedListener(OnChangedListener listener) {
		mListener = listener;
	}

	private void notifyChangedListeners(HashMap<String, Subject> subjects) {
		if (mListener != null) {
			mListener.onChanged(mLessons, subjects);
		}
	}

	interface OnChangedListener {
		void onChanged(SparseArray<Lesson> lessons, HashMap<String, Subject> subjects);
	}

	public void onDataChange(DataSnapshot dataSnapshot) {
		mLessons = new SparseArray<>();
		if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
			for (DataSnapshot child : dataSnapshot.getChildren()) {
				Lesson lesson = child.getValue(Lesson.class);
				mLessons.put(lesson.getPeriod(), lesson);
			}
			mLessonQuery
					.getRef()
					.getParent()
					.getParent()
					.child("subjects")
					.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							HashMap<String, Subject> mSubjects = new HashMap<>();
							if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
								for (DataSnapshot child : dataSnapshot.getChildren()) {
									Subject subject = child.getValue(Subject.class);
									mSubjects.put(subject.getName(), subject);
								}
							}
							notifyChangedListeners(mSubjects);
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
		}
	}

	public void onCancelled(DatabaseError firebaseError) {
	}


}
