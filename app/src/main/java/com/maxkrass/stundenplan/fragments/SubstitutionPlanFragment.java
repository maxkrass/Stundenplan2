package com.maxkrass.stundenplan.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.SubstitutionPlanPagerAdapter;
import com.maxkrass.stundenplan.databinding.FragmentSubstitutionPlanBinding;

public class SubstitutionPlanFragment extends Fragment {

	private OnFragmentInteractionListener mListener;
	private ViewPager viewPager;
	private TabLayout tabLayout;

	public SubstitutionPlanFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		FragmentSubstitutionPlanBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_substitution_plan, container, false);
		viewPager = binding.substitutionPlanViewPager;
		tabLayout = binding.substitutionPlanTabLayout;
		viewPager.setAdapter(new SubstitutionPlanPagerAdapter(getFragmentManager()));
		tabLayout.setupWithViewPager(viewPager);
		return binding.getRoot();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
	}
}
