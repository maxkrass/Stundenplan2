package com.maxkrass.stundenplan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maxkrass.stundenplan.fragments.SingleDaySubstitutionFragment;

/**
 * Max made this for Stundenplan2 on 16.07.2016.
 */
public class SubstitutionPlanPagerAdapter extends FragmentPagerAdapter {

	private final SingleDaySubstitutionFragment fragment1;
	private final SingleDaySubstitutionFragment fragment2;
	private final SingleDaySubstitutionFragment fragment3;

	public SubstitutionPlanPagerAdapter(FragmentManager fm, String uId) {
		super(fm);
		fragment1 = SingleDaySubstitutionFragment.newInstance(1, uId);
		fragment2 = SingleDaySubstitutionFragment.newInstance(2, uId);
		fragment3 = SingleDaySubstitutionFragment.newInstance(3, uId);
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
