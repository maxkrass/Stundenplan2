package com.maxkrass.stundenplan.presenters;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.contracts.CreateTeacherContract;
import com.maxkrass.stundenplan.data.TeacherDataSource;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;

import java.util.Locale;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class CreateTeacherPresenter implements CreateTeacherContract.Presenter, ValueEventListener {

	@NonNull
	private final TeacherDataSource mTeacherRepository;

	@NonNull
	private final CreateTeacherContract.View mCreateTeacherView;

	@Nullable
	private final String mTeacherIDKey;

	public CreateTeacherPresenter(@NonNull TeacherDataSource teacherRepository, @NonNull CreateTeacherContract.View createTeacherView, @Nullable String teacherIDKey) {
		mTeacherRepository = checkNotNull(teacherRepository);
		mCreateTeacherView = checkNotNull(createTeacherView);
		mCreateTeacherView.setPresenter(this);
		mTeacherIDKey = teacherIDKey;
	}

	private void saveTeacher(String name, String email, String phone) {

		OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) mCreateTeacherView.exitCreateDialog();
				else mCreateTeacherView.savingFailed();
			}
		};

		if (isNewTeacher()) createTeacher(name, email, phone, listener);
		else updateTeacher(name, email, phone, listener);
	}

	@Override
	public void validateTeacher(final String name, final String email, final String phone) {
		mCreateTeacherView.removeErrors();
		boolean error = false;
		if (name.isEmpty()) {
			mCreateTeacherView.nameInvalid();
			error = true;
		}

		if (!email.isEmpty() && !Tools.isValidEmail(email)) {
			mCreateTeacherView.emailInvalid();
			error = true;
		}

		if (!error) {
			if (isNewTeacher() || teacherNameWasChanged(name)) {
				mTeacherRepository.teacherExists(name, new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
							mCreateTeacherView.nameExists();
						} else {
							saveTeacher(name, email, phone);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
			} else {
				saveTeacher(name, email, phone);
			}
		}
	}

	private void updateTeacher(String name, String email, String phone, OnCompleteListener<Void> listener) {
		assert mTeacherIDKey != null;
		mTeacherRepository.updateTeacher(mTeacherIDKey, name, phone.equals("") ? "" : PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry()), email, listener);
	}

	private void createTeacher(String name, String email, String phone, OnCompleteListener<Void> listener) {
		mTeacherRepository.saveTeacher(name, phone.equals("") ? "" : PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry()), email, listener);
	}

	@Override
	public void start() {
		populateTeacher();
	}

	private void populateTeacher() {
		if (!isNewTeacher()) {
			assert mTeacherIDKey != null;
			mTeacherRepository.getTeacher(mTeacherIDKey, this);
		}
	}

	private boolean isNewTeacher() {
		return mTeacherIDKey == null || Objects.equals(mTeacherIDKey, "");
	}

	private boolean teacherNameWasChanged(String name) {
		return !Objects.equals(mTeacherIDKey, name);
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		mCreateTeacherView.showTeacher(dataSnapshot.getValue(Teacher.class));
	}

	@Override
	public void onCancelled(DatabaseError databaseError) {
		mCreateTeacherView.exitCreateDialog();
	}
}
