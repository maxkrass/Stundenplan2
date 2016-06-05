package com.maxkrass.stundenplan.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.ActivitySettingsBinding;
import com.maxkrass.stundenplan.fragments.TimePickerFragment;
import com.maxkrass.stundenplan.objects.Period;
import com.orm.SugarRecord;

public class SettingsActivity extends BaseActivity {

	//public TextView start1;
	//public TextView end1;
	//public TextView start2;
	//public TextView end2;
	//public TextView start3;
	//public TextView end3;
	//public TextView start4;
	//public TextView end4;
	//public TextView start5;
	//public TextView end5;
	//public TextView start6;
	//public TextView end6;
	//public TextView start7;
	//public TextView end7;
	//public TextView start8;
	//public TextView end8;
	//public TextView start9;
	//public TextView end9;
	//public TextView start10;
	//public TextView end10;

	private static final String TAG = "SettingsActivity";
	public ActivitySettingsBinding activitySettingsBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		/*getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();*/
		activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
		ObservableArrayList<Period> periods = new ObservableArrayList<>();
		periods.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Period>>() {
			@Override
			public void onChanged(ObservableList<Period> periods) {
				Log.d(TAG, "onChanged");
			}

			@Override
			public void onItemRangeChanged(ObservableList<Period> periods, int i, int i1) {
				Log.d(TAG, "onItemRangeChanged");
			}

			@Override
			public void onItemRangeInserted(ObservableList<Period> periods, int i, int i1) {
				Log.d(TAG, "onItemRangeInserted");
			}

			@Override
			public void onItemRangeMoved(ObservableList<Period> periods, int i, int i1, int i2) {
				Log.d(TAG, "onItemRangeMoved");
			}

			@Override
			public void onItemRangeRemoved(ObservableList<Period> periods, int i, int i1) {
				Log.d(TAG, "onItemRangeRemoved");
			}
		});
		periods.addAll(SugarRecord.listAll(Period.class));
		activitySettingsBinding.setPeriods(periods);
		//start1 = (TextView) findViewById(R.id.start_1);
		//end1 = (TextView) findViewById(R.id.end_1);
		//start2 = (TextView) findViewById(R.id.start_2);
		//end2 = (TextView) findViewById(R.id.end_2);
		//start3 = (TextView) findViewById(R.id.start_3);
		//end3 = (TextView) findViewById(R.id.end_3);
		//start4 = (TextView) findViewById(R.id.start_4);
		//end4 = (TextView) findViewById(R.id.end_4);
		//start5 = (TextView) findViewById(R.id.start_5);
		//end5 = (TextView) findViewById(R.id.end_5);
		//start6 = (TextView) findViewById(R.id.start_6);
		//end6 = (TextView) findViewById(R.id.end_6);
		//start7 = (TextView) findViewById(R.id.start_7);
		//end7 = (TextView) findViewById(R.id.end_7);
		//start8 = (TextView) findViewById(R.id.start_8);
		//end8 = (TextView) findViewById(R.id.end_8);
		//start9 = (TextView) findViewById(R.id.start_9);
		//end9 = (TextView) findViewById(R.id.end_9);
		//start10 = (TextView) findViewById(R.id.start_10);
		//end10 = (TextView) findViewById(R.id.end_10);
	}

	public void pickTime(View v) {
		if (v.getId() != 0) {
			TimePickerFragment timePickerFragment = new TimePickerFragment();
			Bundle bundle = new Bundle();
			TextView textView = (TextView) v;
			LinearLayout linearLayout = (LinearLayout) textView.getParent();
			boolean start = linearLayout.indexOfChild(textView)==1;
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
