package com.maxkrass.stundenplan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maxkrass.stundenplan.fragments.SingleDaySubstitutionFragment;

/**
 * Created by Max on 16.07.2016.
 */
public class SubstitutionPlanPagerAdapter extends FragmentPagerAdapter {
	public SubstitutionPlanPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return SingleDaySubstitutionFragment.newInstance(position);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Heute";
			case 1:
				return "Morgen";
			case 2:
				return "Übermorgen";
			default:
				return "";
		}
	}
}
