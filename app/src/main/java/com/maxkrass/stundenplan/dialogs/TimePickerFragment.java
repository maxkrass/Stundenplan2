package com.maxkrass.stundenplan.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.maxkrass.stundenplan.databinding.FragmentSettingsBinding;
import com.maxkrass.stundenplan.objects.Period;

/**
 * Max made this for Stundenplan on 06.02.2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	int id;
	private static final String TAG = "TimePickerFragment";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		id = getArguments().getInt("id");

		if (id != 0) {

			int hour = getArguments().getInt("hours");
			int minute = getArguments().getInt("minutes");

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		} else {
			dismiss();
			return null;
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		FragmentSettingsBinding fragmentSettingsBinding = DataBindingUtil.findBinding(getActivity().findViewById(id));
		Period period = fragmentSettingsBinding.getPeriods().get(getArguments().getInt("period"));
		if (getArguments().getBoolean("start")) {
			period.setStartHour(hourOfDay);
			period.setStartMinute(minute);
		} else {
			period.setEndHour(hourOfDay);
			period.setEndMinute(minute);
		}
		period.save();
	}
}
