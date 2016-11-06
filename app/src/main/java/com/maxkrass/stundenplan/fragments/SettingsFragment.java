package com.maxkrass.stundenplan.fragments;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.FirebaseSubstitutionSubjectAdapter;
import com.maxkrass.stundenplan.databinding.FragmentSettingsBinding;
import com.maxkrass.stundenplan.objects.SubstitutionSubject;

/**
 * Max made this for Stundenplan on 05.02.2016.
 */
public class SettingsFragment extends Fragment {
	public static final String RECEIVE_SUBSTITUTION_NOTIFICATIONS = "receive_substitution-notifications";
	public static final String MY_SUBJECTS                        = "my_subjects";
	public static final String EF                                 = "ef";
	public static final String Q1                                 = "q1";
	public static final String Q2                                 = "q2";
	private String                  mUid;
	private RecyclerView            substitutionSubjectsList;
	private DatabaseReference       mSubstitutionSubjectsRef;
	private FragmentSettingsBinding mBinding;
	private SharedPreferences       preferences;

	public static SettingsFragment newInstance(String uid) {
		SettingsFragment fragment = new SettingsFragment();
		fragment.mUid = uid;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		mSubstitutionSubjectsRef = FirebaseDatabase
				.getInstance()
				.getReference("users")
				.child(mUid)
				.child("substitutionSubjects");
		mBinding.substitutionNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(RECEIVE_SUBSTITUTION_NOTIFICATIONS, isChecked).apply();
				if (isChecked) FirebaseMessaging.getInstance().subscribeToTopic("checkPlan");
				else FirebaseMessaging.getInstance().unsubscribeFromTopic("checkPlan");
			}
		});
		mBinding.checkBoxShowMySubjects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(MY_SUBJECTS, isChecked).apply();
			}
		});
		mBinding.checkBoxShowEf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(EF, isChecked).apply();
			}
		});
		mBinding.checkBoxShowQ1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(Q1, isChecked).apply();
			}
		});
		mBinding.checkBoxShowQ2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(Q2, isChecked).apply();
			}
		});
		substitutionSubjectsList = mBinding.substitutionSubjectsList;
		substitutionSubjectsList.setLayoutManager(new LinearLayoutManager(getContext()) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		});
		substitutionSubjectsList.setHasFixedSize(false);
		return mBinding.getRoot();
	}

	@Override
	public void onStart() {
		super.onStart();
		substitutionSubjectsList.setAdapter(
				new FirebaseSubstitutionSubjectAdapter(
						SubstitutionSubject.class,
						FirebaseSubstitutionSubjectAdapter.SubstitutionSubjectsViewHolder.class,
						mSubstitutionSubjectsRef));
		mBinding.substitutionNotificationSwitch.setChecked(preferences.getBoolean(RECEIVE_SUBSTITUTION_NOTIFICATIONS, true));
		mBinding.checkBoxShowMySubjects.setChecked(preferences.getBoolean(MY_SUBJECTS, true));
		mBinding.checkBoxShowEf.setChecked(preferences.getBoolean(EF, true));
		mBinding.checkBoxShowQ1.setChecked(preferences.getBoolean(Q1, true));
		mBinding.checkBoxShowQ2.setChecked(preferences.getBoolean(Q2, true));
	}
}
