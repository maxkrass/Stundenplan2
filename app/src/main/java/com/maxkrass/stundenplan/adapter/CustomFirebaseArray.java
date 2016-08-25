package com.maxkrass.stundenplan.adapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Lesson;

import java.util.HashMap;

/**
 * Max made this for Stundenplan2 on 24.07.2016.
 */
class CustomFirebaseArray implements ValueEventListener {
	private final Query                    mLessonQuery;
	private       OnChangedListener        mListener;
	private       HashMap<Integer, Lesson> mLessons;

	public CustomFirebaseArray(Query ref) {
		mLessonQuery = ref;
		mLessons = new HashMap<>(10);
		//mLessonQuery.addChildEventListener(this);
		mLessonQuery.orderByValue();
		mLessonQuery.addValueEventListener(this);
	}

	public void cleanup() {
		mLessonQuery.removeEventListener(this);
	}

	public int getCount() {
		return mLessons.size();

	}

	public Lesson getItem(int key) {
		return mLessons.get(key);
	}

	public Lesson getItemByIndex(int index) {
		int counter = 0;
		for (HashMap.Entry<Integer, Lesson> entry : mLessons.entrySet()) {
			if (counter == index) return entry.getValue();
			counter++;
		}
		return null;
	}

	public void onDataChange(DataSnapshot dataSnapshot) {
		mLessons = new HashMap<>();
		if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
			for (DataSnapshot child : dataSnapshot.getChildren()) {
				Lesson lesson = child.getValue(Lesson.class);
				mLessons.put(lesson.getPeriod(), lesson);
			}
			notifyChangedListeners();
		}
	}

	public void onCancelled(DatabaseError firebaseError) {
	}

	public void setOnChangedListener(OnChangedListener listener) {
		mListener = listener;
	}

	private void notifyChangedListeners() {
		if (mListener != null) {
			mListener.onChanged(mLessons);
		}
	}

	public interface OnChangedListener {
		void onChanged(HashMap<Integer, Lesson> lessons);
	}

}