package com.maxkrass.stundenplan.fragments;

import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.FragmentSettingsBinding;
import com.maxkrass.stundenplan.objects.Period;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Max made this for Stundenplan on 05.02.2016.
 */
public class SettingsFragment extends Fragment {
	private DatabaseReference mSubstitutionSubjectsRef;
	private HashMap<String, String>  mSubstitutionSubjects;
	private String mUid;

	public static SettingsFragment newInstance(String uid) {
		SettingsFragment fragment = new SettingsFragment();
		fragment.mUid = uid;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
		mSubstitutionSubjectsRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(mUid)
				.child("substitutionSubjects");
		mSubstitutionSubjectsRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				mSubstitutionSubjects = new HashMap<>();
				if (dataSnapshot.hasChildren()) {
					mSubstitutionSubjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, String>>() {});
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		RecyclerView substitutionSubjectsList = binding.substitutionSubjectsList;
		substitutionSubjectsList.setAdapter(new );
		return binding.getRoot();
	}
}
