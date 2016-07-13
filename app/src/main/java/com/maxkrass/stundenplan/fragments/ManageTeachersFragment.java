package com.maxkrass.stundenplan.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateTeacherActivity;
import com.maxkrass.stundenplan.adapter.TeachersAdapter;
import com.maxkrass.stundenplan.databinding.FragmentManageTeachersBinding;

/**
 * Max made this for Stundenplan2 on 10.07.2016.
 */
public class ManageTeachersFragment extends Fragment implements View.OnClickListener {
	public RecyclerView recyclerView;
	boolean mSelect;
	public OnTeacherChosenListener mOnTeacherChosenListener;

	public interface OnTeacherChosenListener {
		void onTeacherChosen(Long teacherID);

		void onNoneChosen();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentManageTeachersBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_teachers, container, false);
		binding.addTeacher.setOnClickListener(this);
		mSelect = getArguments().getBoolean("select");
		if (mSelect) {
			try {
				mOnTeacherChosenListener = (OnTeacherChosenListener) getActivity();
			} catch (ClassCastException e) {
				throw new ClassCastException(getActivity().toString()
						+ " must implement OnHeadlineSelectedListener");
			}
		}
		recyclerView = binding.teachersRecyclerview;
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(new TeachersAdapter(this, mSelect));
		return binding.getRoot();
	}

	@Override
	public void onClick(View v) {
		getActivity().startActivity(new Intent(getActivity(), CreateTeacherActivity.class));
	}
}
