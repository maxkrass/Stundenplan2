package com.maxkrass.stundenplan.data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.objects.Teacher;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public interface TeacherDataSource {

	void getTeacher(@NonNull String key, @NonNull ValueEventListener listener);

	void teacherExists(String name, @NonNull ValueEventListener listener);

	void saveTeacher(@NonNull String teacherName, String teacherPhone, String teacherEmail, OnCompleteListener<Void> listener);

	void updateTeacher(@NonNull String key, @NonNull String teacherName, String teacherPhone, String teacherEmail, OnCompleteListener<Void> listener);

	void deleteTeacher(@NonNull Teacher teacher);

}
