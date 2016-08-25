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

	private static final String TAG = "SubstitutionPlan";
	private OnFragmentInteractionListener mListener;
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
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
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
		tabLayout.setupWithViewPager(viewPager);

		return viewPager;
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
