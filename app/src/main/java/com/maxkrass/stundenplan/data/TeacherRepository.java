package com.maxkrass.stundenplan.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Teacher;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class TeacherRepository implements TeacherDataSource {

	DatabaseReference mTeacherRef;

	public TeacherRepository(String uId) {
		mTeacherRef = FirebaseDatabase.getInstance().getReference().child("users").child(uId).child("teachers");
	}

	@Override
	public void getTeacher(@NonNull String key, @NonNull ValueEventListener listener) {
		mTeacherRef.child(key).addListenerForSingleValueEvent(listener);
	}

	@Override
	public void teacherExists(String name, @NonNull ValueEventListener listener) {
		mTeacherRef.child(name).addListenerForSingleValueEvent(listener);
	}

	@Override
	public void saveTeacher(@NonNull String teacherName, String teacherPhone, String teacherEmail, OnCompleteListener<Void> listener) {
		mTeacherRef
				.child(teacherName)
				.setValue(new Teacher(teacherName, teacherPhone, teacherEmail))
				.addOnCompleteListener(listener);
	}

	@Override
	public void updateTeacher(@NonNull String key, @NonNull String teacherName, String teacherPhone, String teacherEmail, OnCompleteListener<Void> listener) {
		if (!key.equals(teacherName)) {
			mTeacherRef
					.child(key)
					.removeValue();
		}

		mTeacherRef
				.child(teacherName)
				.setValue(new Teacher(teacherName, teacherPhone, teacherEmail))
				.addOnCompleteListener(listener);
	}

	@Override
	public void deleteTeacher(@NonNull Teacher teacher) {
		mTeacherRef.child(teacher.getTeacherName()).removeValue();
	}
}
