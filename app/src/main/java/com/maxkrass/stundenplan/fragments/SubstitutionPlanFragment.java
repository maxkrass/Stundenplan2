package com.maxkrass.stundenplan.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.activities.MainActivity;
import com.maxkrass.stundenplan.adapter.SubstitutionPlanPagerAdapter;
import com.maxkrass.stundenplan.databinding.FragmentSubstitutionPlanBinding;

public class SubstitutionPlanFragment extends Fragment {

	private MainActivity                  mActivity;

	public static SubstitutionPlanFragment newInstance(String uId) {

		Bundle args = new Bundle();
		args.putString("uId", uId);
		SubstitutionPlanFragment fragment = new SubstitutionPlanFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof MainActivity) {
			mActivity = (MainActivity) getActivity();
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		FragmentSubstitutionPlanBinding binding = FragmentSubstitutionPlanBinding.inflate(inflater);
		ViewPager viewPager = binding.substitutionPlanViewPager;
		TabLayout tabLayout = mActivity.tabLayout;
		viewPager.setAdapter(new SubstitutionPlanPagerAdapter(getChildFragmentManager(), getArguments().getString("uId")));
		viewPager.setOffscreenPageLimit(2);
		tabLayout.setupWithViewPager(viewPager);

		return viewPager;
	}

}
