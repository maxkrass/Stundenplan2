package com.maxkrass.stundenplan.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateSubjectActivity;
import com.maxkrass.stundenplan.adapter.SubjectsAdapter;
import com.maxkrass.stundenplan.databinding.FragmentManageSubjectsBinding;

/**
 * Max made this for Stundenplan2 on 09.07.2016.
 */
public class ManageSubjectsFragment extends Fragment implements View.OnClickListener {
	public RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentManageSubjectsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_subjects, container, false);
		binding.addSubject.setOnClickListener(this);
		recyclerView = binding.subjectsRecyclerview;
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(new SubjectsAdapter(this));
		return binding.getRoot();
	}

	@Override
	public void onClick(View v) {
		getActivity().startActivity(new Intent(getActivity(), CreateSubjectActivity.class));
	}
}
