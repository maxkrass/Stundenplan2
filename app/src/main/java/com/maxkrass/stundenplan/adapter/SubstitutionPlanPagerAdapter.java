package com.maxkrass.stundenplan.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maxkrass.stundenplan.fragments.LarsSingleDaySubstitutionFragment;

/**
 * Max made this for Stundenplan2 on 16.07.2016.
 */
public class SubstitutionPlanPagerAdapter extends FragmentPagerAdapter {

	private final LarsSingleDaySubstitutionFragment fragment1, fragment2, fragment3;
	private       TabLayout                         mTabLayout;

	public SubstitutionPlanPagerAdapter(FragmentManager fm, String uId, TabLayout tabLayout) {
		super(fm);
		mTabLayout = tabLayout;
		fragment1 = LarsSingleDaySubstitutionFragment.newInstance(1, uId, this);
		fragment2 = LarsSingleDaySubstitutionFragment.newInstance(2, uId, this);
		fragment3 = LarsSingleDaySubstitutionFragment.newInstance(3, uId, this);
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
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return fragment1.getTitle();
			case 1:
				return fragment2.getTitle();
			case 2:
				return fragment3.getTitle();
			default:
				return "";
		}
	}

	public TabLayout getTabLayout() {
		return mTabLayout;
	}
}
