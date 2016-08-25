package com.maxkrass.stundenplan.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateTeacherActivity;
import com.maxkrass.stundenplan.contracts.CreateTeacherContract;
import com.maxkrass.stundenplan.databinding.FragmentCreateTeacherBinding;
import com.maxkrass.stundenplan.objects.Teacher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class CreateTeacherFragment extends Fragment implements CreateTeacherContract.View, View.OnClickListener {

	private CreateTeacherContract.Presenter mPresenter;
	private CreateTeacherActivity           mActivity;
	private FragmentCreateTeacherBinding    binding;

	private Teacher teacher;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof CreateTeacherActivity) {
			mActivity = (CreateTeacherActivity) context;
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_teacher, container, false);
		teacher = new Teacher();
		binding.setTeacher(teacher);
		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mPresenter != null)
			mPresenter.start();
	}

	@Override
	public void exitCreateDialog() {
		getActivity().finish();
	}

	@Override
	public void nameInvalid() {
		if (mActivity != null) mActivity.nameInvalid();
	}

	@Override
	public void nameExists() {
		if (mActivity != null) mActivity.nameExists();
	}

	@Override
	public void emailInvalid() {
		binding.teacherEmailTil.setError("This email address is wrong");
	}

	@Override
	public void removeErrors() {
		binding.teacherEmailTil.setError("");
		if (mActivity != null) mActivity.removeErrors();
	}

	@Override
	public void showTeacher(Teacher teacher) {
		binding.setTeacher(teacher);
	}

	@Override
	public void savingFailed() {
		Snackbar.make(binding.getRoot(), "Saving Failed", Snackbar.LENGTH_SHORT);
	}

	@Override
	public void setPresenter(@NonNull CreateTeacherContract.Presenter presenter) {
		mPresenter = checkNotNull(presenter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.save_teacher:
				teacher.setTeacherName(mActivity != null ? mActivity.getTeacherName() : "");
				mPresenter.validateTeacher(teacher.getTeacherName(), teacher.getEmail(), teacher.getPhone());
				break;
		}
	}
}
