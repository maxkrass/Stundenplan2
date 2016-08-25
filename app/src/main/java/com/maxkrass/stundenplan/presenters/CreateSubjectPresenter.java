package com.maxkrass.stundenplan.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.contracts.CreateSubjectContract;
import com.maxkrass.stundenplan.data.SubjectRepository;
import com.maxkrass.stundenplan.objects.Subject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Max made this for Stundenplan2 on 20.07.2016.
 */
public class CreateSubjectPresenter implements CreateSubjectContract.Presenter, ValueEventListener {

	@NonNull
	private final SubjectRepository mSubjectRepository;

	@NonNull
	private final CreateSubjectContract.View mCreateSubjectView;

	@Nullable
	private final String mSubjectKey;

	public CreateSubjectPresenter(@NonNull SubjectRepository subjectRepository, @NonNull CreateSubjectContract.View createSubjectView, @Nullable String subjectKey) {
		mSubjectRepository = checkNotNull(subjectRepository);
		mCreateSubjectView = checkNotNull(createSubjectView);
		mCreateSubjectView.setPresenter(this);
		mSubjectKey = subjectKey;
	}

	@Override
	public void start() {
		if (!isNewSubject()) {
			assert mSubjectKey != null;
			mSubjectRepository.getSubject(mSubjectKey, this);
		}
	}

	@Override
	public void validateSubject(final String name, final String abbreviation, final String color, final String teacher) {
		mCreateSubjectView.removeErrors();
		boolean error = false;
		if (name.isEmpty()) {
			mCreateSubjectView.nameInvalid();
			error = true;
		}
		if (abbreviation.isEmpty()) {
			mCreateSubjectView.abbreviationInvalid();
			error = true;
		}
		if (!error) {
			if (isNewSubject() || subjectNameWasChanged(name)) {
				mSubjectRepository.subjectExists(name, new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							mCreateSubjectView.subjectAlreadyExists();
						} else {
							saveSubject(name, abbreviation, color, teacher);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
			} else {
				saveSubject(name, abbreviation, color, teacher);
			}
		}
	}

	private void saveSubject(String name, String abbreviation, String color, String teacher) {
		OnCompleteListener<Void> listener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()) mCreateSubjectView.exitCreateDialog();
				else mCreateSubjectView.savingFailed();
			}
		};

		if (isNewSubject()) mSubjectRepository.createSubject(name, abbreviation, color, teacher, listener);
		else {
			assert mSubjectKey != null;
			mSubjectRepository.updateSubject(mSubjectKey, name, abbreviation, color, teacher, listener);
		}
	}

	private boolean subjectNameWasChanged(String name) {
		return !name.equals(mSubjectKey);
	}

	private boolean isNewSubject() {
		return mSubjectKey == null || mSubjectKey.isEmpty();
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		mCreateSubjectView.showSubject(dataSnapshot.getValue(Subject.class));
	}

	@Override
	public void onCancelled(DatabaseError databaseError) {
		mCreateSubjectView.exitCreateDialog();
	}
}
