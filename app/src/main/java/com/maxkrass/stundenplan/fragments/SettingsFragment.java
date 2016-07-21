package com.maxkrass.stundenplan.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.FragmentSettingsBinding;
import com.maxkrass.stundenplan.dialogs.TimePickerFragment;
import com.maxkrass.stundenplan.objects.Period;
import com.orm.SugarRecord;

/**
 * Max made this for Stundenplan on 05.02.2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
		ObservableArrayList<Period> periods = new ObservableArrayList<>();
		periods.addAll(SugarRecord.listAll(Period.class));
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
			TimePickerFragment timePickerFragment = new TimePickerFragment();
			Bundle bundle = new Bundle();
			TextView textView = (TextView) v;
			LinearLayout linearLayout = (LinearLayout) textView.getParent();
			boolean start = linearLayout.indexOfChild(textView) == 1;
			int period = ((LinearLayout) linearLayout.getParent()).indexOfChild(linearLayout);
			String time = textView.getText().toString();
			String[] times = time.split(":");
			bundle.putBoolean("start", start);
			bundle.putInt("period", period);
			bundle.putInt("hours", Integer.parseInt(times[0]));
			bundle.putInt("minutes", Integer.parseInt(times[1]));
			bundle.putInt("id", v.getId());
			timePickerFragment.setArguments(bundle);
			timePickerFragment.show(getFragmentManager(), "timerPicker");
		}
	}
}
