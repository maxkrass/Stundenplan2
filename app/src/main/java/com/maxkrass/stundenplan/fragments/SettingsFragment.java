package com.maxkrass.stundenplan.fragments;

import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

/**
 * Max made this for Stundenplan on 05.02.2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
	private ObservableArrayList<Period> periods;
	private DatabaseReference           mPeriodRef;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		assert user != null;
		String uId = user.getUid();
		mPeriodRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uId)
				.child("periods");
		mPeriodRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				periods = new ObservableArrayList<>();
				if (!dataSnapshot.hasChildren()) {
					periods.add(new Period(0, 8, 10, 8, 55));
					periods.add(new Period(1, 9, 0, 9, 45));
					periods.add(new Period(2, 9, 50, 10, 35));
					periods.add(new Period(3, 11, 5, 11, 50));
					periods.add(new Period(4, 11, 55, 12, 40));
					periods.add(new Period(5, 12, 45, 13, 30));
					periods.add(new Period(6, 14, 15, 15, 0));
					periods.add(new Period(7, 15, 0, 15, 45));
					periods.add(new Period(8, 15, 55, 16, 40));
					periods.add(new Period(9, 16, 40, 17, 25));

					mPeriodRef.setValue(periods);
				} else {
					periods.addAll(dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Period>>() {
					}));
				}

				binding.setPeriods(periods);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		binding.setPeriods(periods);
		binding.start1.setOnClickListener(this);
		binding.start2.setOnClickListener(this);
		binding.start3.setOnClickListener(this);
		binding.start4.setOnClickListener(this);
		binding.start5.setOnClickListener(this);
		binding.start6.setOnClickListener(this);
		binding.start7.setOnClickListener(this);
		binding.start8.setOnClickListener(this);
		binding.start9.setOnClickListener(this);
		binding.start10.setOnClickListener(this);
		binding.end1.setOnClickListener(this);
		binding.end2.setOnClickListener(this);
		binding.end3.setOnClickListener(this);
		binding.end4.setOnClickListener(this);
		binding.end5.setOnClickListener(this);
		binding.end6.setOnClickListener(this);
		binding.end7.setOnClickListener(this);
		binding.end8.setOnClickListener(this);
		binding.end9.setOnClickListener(this);
		binding.end10.setOnClickListener(this);
		return binding.getRoot();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() != 0) {
			TextView textView = (TextView) v;
			LinearLayout linearLayout = (LinearLayout) textView.getParent();
			boolean start = linearLayout.indexOfChild(textView) == 1;
			int periodIndex = ((LinearLayout) linearLayout.getParent()).indexOfChild(linearLayout);
			Period period = periods.get(periodIndex);
			new TimePickerDialog(
					getContext(),
					new PeriodOnTimeSetListener(period, start),
					start ? period.getStartHour() : period.getEndHour(),
					start ? period.getStartMinute() : period.getEndMinute(), DateFormat.is24HourFormat(getContext()))
					.show();
		}
	}

	public class PeriodOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {

		final Period  period;
		final boolean start;

		public PeriodOnTimeSetListener(Period period, boolean start) {
			this.period = period;
			this.start = start;
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (start) {
				period.setStartHour(hourOfDay);
				period.setStartMinute(minute);
			} else {
				period.setEndHour(hourOfDay);
				period.setEndMinute(minute);
			}
			mPeriodRef.child(String.valueOf(period.getIndex())).setValue(period);
		}
	}
}
