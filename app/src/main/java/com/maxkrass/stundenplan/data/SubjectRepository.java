package com.maxkrass.stundenplan.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Subject;

import java.util.HashMap;

/**
 * Max made this for Stundenplan2 on 20.07.2016.
 */
public class SubjectRepository {

	private final DatabaseReference mSubjectRef;
	private final DatabaseReference mTeachersRef;

	public SubjectRepository(String uId) {
		this.mSubjectRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uId)
				.child("subjects");
		this.mTeachersRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uId)
				.child("teachers");
	}

	public void getSubject(@NonNull String key, ValueEventListener listener) {
		mSubjectRef.child(key).addListenerForSingleValueEvent(listener);
	}

	public void subjectExists(String name, ValueEventListener listener) {
		mSubjectRef.child(name).addListenerForSingleValueEvent(listener);
	}

	public void updateSubject(@NonNull String key, String name, String abbreviation, String color, String teacher, OnCompleteListener<Void> listener) {
		if (!key.equals(name)) {
			mSubjectRef
					.child(key)
					.removeValue();
		}

		mSubjectRef
				.child(key)
				.setValue(new Subject(name, abbreviation, teacher, color))
				.addOnCompleteListener(listener);

		addSubjectToTeacher(teacher, name);
	}

	public void createSubject(String name, String abbreviation, String color, String teacher, OnCompleteListener<Void> listener) {
		mSubjectRef
				.child(name)
				.setValue(new Subject(name, abbreviation, teacher, color))
				.addOnCompleteListener(listener);

		addSubjectToTeacher(teacher, name);
	}

	private void addSubjectToTeacher(final String teacher, final String subject) {
		if (!teacher.isEmpty()) {
			mTeachersRef.child(teacher).child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					HashMap<String, Boolean> subjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Boolean>>() {
					});
					if (subjects == null) subjects = new HashMap<>();
					subjects.remove(subject);
					subjects.put(subject, true);
					mTeachersRef.child(teacher).child("subjects").setValue(subjects);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}
	}

}
