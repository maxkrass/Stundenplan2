package com.maxkrass.stundenplan.data;

import android.support.annotation.NonNull;

import com.maxkrass.stundenplan.objects.Teacher;

import java.util.List;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public interface TeacherDataSource {

	interface LoadTeachersCallback {
		void onTeachersLoaded(List<Teacher> teachers);

		void onDataNotAvailable();

		//void onDataNotAvailable();
	}

	interface GetTeacherCallback {
		void onTeacherLoaded(Teacher teacher);

		void onDataNotAvailable();
	}

	void getTeachers(@NonNull LoadTeachersCallback callback);

	void getTeacher(@NonNull Long teacherID, @NonNull GetTeacherCallback callback);

	boolean teacherExists(String name);

	void saveTeacher(@NonNull Teacher teacher);

	void updateTeacher(@NonNull Teacher teacher);

	void deleteTeacher(@NonNull Teacher teacher);

}
