package com.maxkrass.stundenplan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maxkrass.stundenplan.fragments.LarsSingleDaySubstitutionFragment;

/**
 * Max made this for Stundenplan2 on 16.07.2016.
 */
public class SubstitutionPlanPagerAdapter extends FragmentPagerAdapter {

	private final LarsSingleDaySubstitutionFragment fragment1;
	private final LarsSingleDaySubstitutionFragment fragment2;
	private final LarsSingleDaySubstitutionFragment fragment3;

	public SubstitutionPlanPagerAdapter(FragmentManager fm, String uId) {
		super(fm);
		fragment1 = LarsSingleDaySubstitutionFragment.newInstance(1);
		fragment2 = LarsSingleDaySubstitutionFragment.newInstance(2);
		fragment3 = LarsSingleDaySubstitutionFragment.newInstance(3);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return fragment1;
			case 1:
				return fragment2;
			case 2:
				return fragment3;
			default:
				return null;
		}
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
