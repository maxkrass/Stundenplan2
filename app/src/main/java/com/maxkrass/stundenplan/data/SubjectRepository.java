package com.maxkrass.stundenplan.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Subject;

import java.util.HashMap;

/**
 * Max made this for Stundenplan2 on 20.07.2016.
 */
public class SubjectRepository {

	private DatabaseReference mSubjectRef;

	public SubjectRepository(String uId) {
		this.mSubjectRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uId)
				.child("subjects");
	}

	public void getSubject(@NonNull String key, ValueEventListener listener) {
		mSubjectRef.child(key).addListenerForSingleValueEvent(listener);
	}

	public void subjectExists(String name, ValueEventListener listener) {
		mSubjectRef.child(name).addListenerForSingleValueEvent(listener);
	}

	public void updateSubject(@NonNull String key, String name, String abbreviation, String color, HashMap<String, Boolean> teacher, OnCompleteListener<Void> listener) {
		if (!key.equals(name)) {
			mSubjectRef
					.child(key)
					.removeValue();
		}

		mSubjectRef
				.child(key)
				.setValue(new Subject(name, abbreviation, teacher, color))
				.addOnCompleteListener(listener);
	}

	public void createSubject(String name, String abbreviation, String color, HashMap<String, Boolean> teacher, OnCompleteListener<Void> listener) {
		mSubjectRef
				.child(name)
				.setValue(new Subject(name, abbreviation, teacher, color))
				.addOnCompleteListener(listener);
	}

}
