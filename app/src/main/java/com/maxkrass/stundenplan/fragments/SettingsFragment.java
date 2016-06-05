package com.maxkrass.stundenplan.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.maxkrass.stundenplan.R;

/**
 * Max made this for Stundenplan on 05.02.2016.
 */
public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preference);
	}
}
