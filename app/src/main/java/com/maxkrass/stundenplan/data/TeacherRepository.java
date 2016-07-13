package com.maxkrass.stundenplan.data;

import android.support.annotation.NonNull;

import com.maxkrass.stundenplan.objects.Teacher;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class TeacherRepository implements TeacherDataSource {
	@Override
	public void getTeachers(@NonNull LoadTeachersCallback callback) {
		List<Teacher> teachers = SugarRecord.listAll(Teacher.class);
		if (teachers.isEmpty()) {
			callback.onDataNotAvailable();
		} else {
			callback.onTeachersLoaded(teachers);
		}
	}

	@Override
	public void getTeacher(@NonNull Long teacherID, @NonNull GetTeacherCallback callback) {
		Teacher teacher = SugarRecord.findById(Teacher.class, teacherID);
		if (teacher != null)
			callback.onTeacherLoaded(teacher);
		else
			callback.onDataNotAvailable();
	}

	@Override
	public boolean teacherExists(String name) {
		return Select.from(Teacher.class).where(new Condition("name").eq(name)).first() != null;
	}

	@Override
	public void saveTeacher(@NonNull Teacher teacher) {
		SugarRecord.save(teacher);
	}

	@Override
	public void updateTeacher(@NonNull Teacher teacher) {
		SugarRecord.update(teacher);
	}

	@Override
	public void deleteTeacher(@NonNull Teacher teacher) {
		SugarRecord.delete(teacher);
	}
}
