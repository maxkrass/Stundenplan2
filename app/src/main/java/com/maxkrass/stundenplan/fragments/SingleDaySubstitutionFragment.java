package com.maxkrass.stundenplan.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Max on 17.07.2016.
 */
public class SingleDaySubstitutionFragment extends Fragment {

	public static SingleDaySubstitutionFragment newInstance(int day) {

		Bundle args = new Bundle();
		args.putInt("day", day);
		SingleDaySubstitutionFragment fragment = new SingleDaySubstitutionFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	
}
