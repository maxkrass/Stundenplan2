package com.maxkrass.stundenplan.fragments;

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
import com.maxkrass.stundenplan.contracts.CreateTeacherContract;
import com.maxkrass.stundenplan.databinding.FragmentCreateTeacherBinding;
import com.maxkrass.stundenplan.objects.Teacher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class CreateTeacherFragment extends Fragment implements CreateTeacherContract.View, View.OnClickListener {

	private CreateTeacherContract.Presenter mPresenter;
	private FragmentCreateTeacherBinding binding;

	Teacher teacher;

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
		mPresenter.start();
	}

	@Override
	public void exitCreateDialog() {
		getActivity().finish();
	}

	@Override
	public void nameInvalid() {
		binding.teacherNameTil.setError("The name can't be empty");
	}

	@Override
	public void nameExists() {
		binding.teacherNameTil.setError("You already saved this teacher");
	}

	@Override
	public void emailInvalid() {
		binding.teacherEmailTil.setError("This email address is wrong");
	}

	@Override
	public void removeErrors() {
		binding.teacherEmailTil.setError("");
		binding.teacherNameTil.setError("");
	}

	@Override
	public void setPresenter(@NonNull CreateTeacherContract.Presenter presenter) {
		mPresenter = checkNotNull(presenter);
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
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.save_teacher:
				mPresenter.validateTeacher(teacher.getTeacherName(), teacher.getEmail(), teacher.getPhone());
				break;
		}
	}
}
