package com.maxkrass.stundenplan.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateSubjectActivity;
import com.maxkrass.stundenplan.adapter.FirebaseSubjectAdapter;
import com.maxkrass.stundenplan.databinding.FragmentManageSubjectsBinding;
import com.maxkrass.stundenplan.objects.Subject;

import java.util.HashMap;

/**
 * Max made this for Stundenplan2 on 09.07.2016.
 */
public class ManageSubjectsFragment extends Fragment implements View.OnClickListener {

	public  boolean                 mSelect;
	public  OnSubjectChosenListener mOnSubjectChosenListener;
	private DatabaseReference       mSubjectRef;
	private DatabaseReference       mTeachersRef;
	private FirebaseSubjectAdapter  subjectAdapter;
	private RecyclerView            recyclerView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentManageSubjectsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_subjects, container, false);
		binding.addSubject.setOnClickListener(this);
		if (getArguments() != null) {
			mSelect = getArguments().getBoolean("select");
		}
		if (mSelect) {
			try {
				mOnSubjectChosenListener = (OnSubjectChosenListener) getActivity();
			} catch (ClassCastException e) {
				throw new ClassCastException(getActivity().toString()
						+ " must implement OnTeacherChosenListener");
			}
		}
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user == null) throw new RuntimeException("User mustn't be null");
		mSubjectRef = FirebaseDatabase.getInstance()
				.getReference("users").child(user.getUid()).child("subjects");
		mTeachersRef = FirebaseDatabase.getInstance()
				.getReference("users").child(user.getUid()).child("teachers");
		recyclerView = binding.subjectsRecyclerview;
		recyclerView.setHasFixedSize(true);
		return binding.getRoot();
	}

	@Override
	public void onStart() {
		super.onStart();

		subjectAdapter = new FirebaseSubjectAdapter(
				Subject.class,
				FirebaseSubjectAdapter.SubjectViewHolder.class,
				mSubjectRef,
				this
		);

		subjectAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				int friendlyMessageCount = subjectAdapter.getItemCount();
				int lastVisiblePosition =
						((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
				// If the recycler view is initially being loaded or the
				// user is at the bottom of the list, scroll to the bottom
				// of the list to show the newly added message.
				if (lastVisiblePosition == -1 ||
						(positionStart >= (friendlyMessageCount - 1) &&
								lastVisiblePosition == (positionStart - 1))) {
					recyclerView.scrollToPosition(positionStart);
				}
			}
		});
		recyclerView.setAdapter(subjectAdapter);
	}

	@Override
	public void onClick(View v) {
		getActivity().startActivity(new Intent(getActivity(), CreateSubjectActivity.class));
	}

	public void showLongClickDialog(final Subject subject) {
		new AlertDialog.Builder(getActivity())
				.setTitle(subject.getName())
				.setItems(R.array.dialog_options, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								Intent intent = new Intent(getActivity(), CreateSubjectActivity.class);
								intent.putExtra("subjectKey", subject.getName());
								startActivity(intent);
								dialog.dismiss();
								break;
							case 1:
								Snackbar.make(recyclerView, subject.getName() + " deleted", Snackbar.LENGTH_LONG)
										.setAction("restore", new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												mSubjectRef.child(subject.getName()).setValue(subject);
												if (!subject.getTeacher().isEmpty()) {
													mTeachersRef.child(subject.getTeacher()).child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
														@Override
														public void onDataChange(DataSnapshot dataSnapshot) {
															HashMap<String, Boolean> subjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Boolean>>() {
															});
															if (subjects == null)
																subjects = new HashMap<>();
															subjects.put(subject.getName(), true);
															mTeachersRef.child(subject.getTeacher()).child("subjects").setValue(subjects);
														}

														@Override
														public void onCancelled(DatabaseError databaseError) {

														}
													});
												}
											}
										}).show();

								dialog.dismiss();
								mSubjectRef.child(subject.getName()).removeValue();
								if (!subject.getTeacher().isEmpty()) {
									mTeachersRef.child(subject.getTeacher()).child("subjects").child(subject.getName()).removeValue();
								}
								break;
						}
					}
				}).show();
	}


	public interface OnSubjectChosenListener {
		void onSubjectChosen(Subject subject);

		void onNoneChosen();
	}
}
