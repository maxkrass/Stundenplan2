package com.maxkrass.stundenplan.presenters;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.maxkrass.stundenplan.contracts.CreateTeacherContract;
import com.maxkrass.stundenplan.data.TeacherDataSource;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class CreateTeacherPresenter implements CreateTeacherContract.Presenter, TeacherDataSource.GetTeacherCallback {

	@NonNull
	private final TeacherDataSource mTeacherRepository;

	@NonNull
	private final CreateTeacherContract.View mCreateTeacherView;

	@Nullable
	private Long mTeacherID;

	public CreateTeacherPresenter(@NonNull TeacherDataSource teacherRepository, @NonNull CreateTeacherContract.View createTeacherView, @Nullable Long teacherID) {
		mTeacherRepository = checkNotNull(teacherRepository);
		mCreateTeacherView = checkNotNull(createTeacherView);
		mCreateTeacherView.setPresenter(this);
		mTeacherID = teacherID;
	}

	@Override
	public void saveTeacher(String name, String email, String phone) {
		if (isNewTeacher())
			createTeacher(name, email, phone);
		else
			updateTeacher(name, email, phone);
	}

	@Override
	public void validateTeacher(String name, String email, String phone) {
		boolean error = false;
		mCreateTeacherView.removeErrors();
		if (name.equals("")) {
			mCreateTeacherView.nameInvalid();
			error = true;
		} else if (isNewTeacher() && mTeacherRepository.teacherExists(name)) {
			mCreateTeacherView.nameExists();
			error = true;
		}
		if (!TextUtils.isEmpty(email) && !Tools.isValidEmail(email)) {
			mCreateTeacherView.emailInvalid();
			error = true;
		}
		if (!error) {
			saveTeacher(name, email, phone);
			//teachersAdapter.add(teacher);
			//teachersAdapter.notifyItemInserted(Stundenplan.getInstance().getTeachers().size() - 1);
		}
	}

	private void updateTeacher(String name, String email, String phone) {
		if (isNewTeacher())
			throw new RuntimeException("updateTeacher() was called but teacher is new.");
		Teacher teacher = new Teacher(name, phone.equals("") ? "" : PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry()), email);
		teacher.setId(mTeacherID);
		SugarRecord.update(teacher);
		mCreateTeacherView.exitCreateDialog();
		//TODO ((TeachersAdapter) ManageTeachersActivity.teacherRecyclerView.getAdapter()).updateData();
		//ManageTeachersActivity.teacherRecyclerView.getAdapter().notifyItemChanged(Tools.getTeacherPosition(teacher));
	}

	private void createTeacher(String name, String email, String phone) {
		if (mTeacherRepository.teacherExists(name))
			mCreateTeacherView.nameExists();
		else {
			mTeacherRepository.saveTeacher(new Teacher(name, phone.equals("") ? "" : PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry()), email));
			mCreateTeacherView.exitCreateDialog();
			//TODO ((TeachersAdapter) ManageTeachersActivity.teacherRecyclerView.getAdapter()).add(teacher);
			//ManageTeachersActivity.teacherRecyclerView.getAdapter().notifyItemInserted(SugarRecord.listAll(Teacher.class, "name").size() - 1);
		}

	}

	@Override
	public void start() {
		populateTeacher();
	}

	public void populateTeacher() {
		if (isNewTeacher())
			mCreateTeacherView.showTeacher(new Teacher());
		else
			mTeacherRepository.getTeacher(mTeacherID, this);
	}

	@Override
	public void onTeacherLoaded(Teacher teacher) {
		mCreateTeacherView.showTeacher(teacher);
	}

	@Override
	public void onDataNotAvailable() {
		mCreateTeacherView.exitCreateDialog();
	}

	private boolean isNewTeacher() {
		return mTeacherID == null || mTeacherID == 0;
	}
}
