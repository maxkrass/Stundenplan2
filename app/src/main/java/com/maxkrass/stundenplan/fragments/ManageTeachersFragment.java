package com.maxkrass.stundenplan.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.FirebaseTeacherAdapter;
import com.maxkrass.stundenplan.databinding.FragmentManageTeachersBinding;
import com.maxkrass.stundenplan.objects.Teacher;

/**
 * Max made this for Stundenplan2 on 10.07.2016.
 */
public class ManageTeachersFragment extends Fragment {
	public  boolean                 mSelect;
	public  OnTeacherChosenListener mOnTeacherChosenListener;
	private RecyclerView            recyclerView;

	private DatabaseReference mTeacherRef;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentManageTeachersBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_teachers, container, false);
		// TODO Add a suggest a new teacher binding.addTeacher.setOnClickListener(this);
		if (getArguments() != null) {
			mSelect = getArguments().getBoolean("select");
		}
		mTeacherRef = FirebaseDatabase.getInstance()
				.getReference("stundenplan").child("publicTeachers");
		if (mSelect) {
			try {
				mOnTeacherChosenListener = (OnTeacherChosenListener) getActivity();
			} catch (ClassCastException e) {
				throw new ClassCastException(getActivity().toString()
						+ " must implement OnTeacherChosenListener");
			}
		}
		recyclerView = binding.teachersRecyclerView;
		recyclerView.setHasFixedSize(true);

		return binding.getRoot();
	}

	@Override
	public void onStart() {
		super.onStart();

		FirebaseRecyclerAdapter<Teacher, FirebaseTeacherAdapter.TeacherViewHolder> teachersAdapter = new FirebaseTeacherAdapter(
				Teacher.class,
				FirebaseTeacherAdapter.TeacherViewHolder.class,
				mTeacherRef,
				this
		);

		recyclerView.setAdapter(teachersAdapter);
	}

	public void showLongClickDialog(final Teacher teacher) {
		new AlertDialog.Builder(getActivity())
				.setTitle(teacher.getTeacherName())
				.setItems(R.array.dialog_options, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								break;
							case 1:
								Snackbar.make(recyclerView, teacher.getTeacherName() + " deleted", Snackbar.LENGTH_LONG)
										.setAction("restore", new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												mTeacherRef.child(teacher.getTeacherName()).setValue(teacher);
											}
										}).show();

								dialog.dismiss();
								mTeacherRef.child(teacher.getTeacherName()).removeValue();
								break;
						}
					}
				}).create().show();
	}


	public interface OnTeacherChosenListener {
		void onTeacherChosen(Teacher teacher);

		void onNoneChosen();
	}
}
