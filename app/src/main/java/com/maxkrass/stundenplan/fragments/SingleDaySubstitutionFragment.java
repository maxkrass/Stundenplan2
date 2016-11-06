package com.maxkrass.stundenplan.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.adapter.SingleDaySubRecyclerAdapter;
import com.maxkrass.stundenplan.adapter.SubstitutionPlanPagerAdapter;
import com.maxkrass.stundenplan.databinding.RecycleViewFragmentBinding;
import com.maxkrass.stundenplan.objects.SubstitutionEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleDaySubstitutionFragment extends Fragment implements SingleDaySubRecyclerAdapter.OnLoadingFinishedListener, SingleDaySubRecyclerAdapter.OnSubstitutionItemClickListener {
	private static final long IDLE_TIME = 60000;
	public SingleDaySubRecyclerAdapter recyclerAdapter;
	DatabaseReference mSubstitutionPlanRef, mSubstitutionSubjectsRef, mSubstitutionDatesRef;
	SubstitutionPlanPagerAdapter mPagerAdapter;
	RecycleViewFragmentBinding   mBinding;
	BottomSheetBehavior          mBottomSheetBehavior;
	private int    index;
	private String mTitle, mUId;
	private ArrayList<SubstitutionEvent> mLastEvents;
	private HashMap<String, String>      mSubstitutionSubjects;
	private SwipeRefreshLayout           swipeRefreshLayout;

	public static SingleDaySubstitutionFragment newInstance(int index, String uId, SubstitutionPlanPagerAdapter pagerAdapter) {
		SingleDaySubstitutionFragment fragment = new SingleDaySubstitutionFragment();
		fragment.mPagerAdapter = pagerAdapter;
		fragment.index = index;
		fragment.mUId = uId;
		switch (index) {
			case 1:
				fragment.mTitle = "Heute";
				break;
			case 2:
				fragment.mTitle = "Morgen";
				break;
			case 3:
				fragment.mTitle = "Übermorgen";
				break;
		}
		return fragment;
	}

	public String getTitle() {
		return mTitle;
	}

	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBinding = RecycleViewFragmentBinding.inflate(inflater, container, false);
		swipeRefreshLayout = mBinding.swipeRefreshLayout;
		swipeRefreshLayout.setOnRefreshListener(new SetBackRefreshListener());
		initRecyclerView();
		mSubstitutionPlanRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("stundenplan")
				.child("latestSubstitutionPlans")
				.child("plans")
				.child("day" + index);

		mSubstitutionDatesRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("stundenplan")
				.child("latestSubstitutionPlans")
				.child("dates")
				.child("date" + index);

		if (savedInstanceState != null && mUId == null) {
			mUId = savedInstanceState.getString("uId");
		}

		mSubstitutionSubjectsRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(mUId)
				.child("substitutionSubjects");
		mSubstitutionSubjects = new HashMap<>();
		mSubstitutionSubjectsRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.hasChildren()) {
					mSubstitutionSubjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, String>>() {
					});
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.substitutionBottomSheet);
		mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if (newState == BottomSheetBehavior.STATE_EXPANDED && !mSubstitutionSubjects.containsKey(mBinding.getSubstitutionEvent().getSubject())) {
					mBinding.addSubstitutionSubject.show();
				} else {
					mBinding.addSubstitutionSubject.hide();
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		mBinding.addSubstitutionSubject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mBinding.getSubstitutionEvent().getSubject().isEmpty() && mBinding.getSubstitutionEvent().getGrade() != null) {
					mSubstitutionSubjects.put(mBinding.getSubstitutionEvent().getSubject(), mBinding.getSubstitutionEvent().getGrade());
					mSubstitutionSubjectsRef.setValue(mSubstitutionSubjects).addOnSuccessListener(new OnSuccessListener<Void>() {
						@Override
						public void onSuccess(Void aVoid) {
							recyclerAdapter.setNewContent(mLastEvents);
						}
					});
				}
			}
		});
		refreshItems();
		return mBinding.getRoot();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("uId", mUId);
	}

	public void refreshItems() {
		swipeRefreshLayout.setRefreshing(true);
		mSubstitutionPlanRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				ArrayList<SubstitutionEvent> events;
				events = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<SubstitutionEvent>>() {
				});
				if (events != null) {
					mLastEvents = events;
					recyclerAdapter.setNewContent(events);
				} else {
					mLastEvents = new ArrayList<>();
					recyclerAdapter.setNewContent(mLastEvents);
				}
				mPagerAdapter.getTabLayout().getTabAt(index - 1).setText(mTitle);

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Snackbar.make(swipeRefreshLayout, "Bitte überprüfe deine Internetverbindung", Snackbar.LENGTH_LONG).show();
			}
		});

		mSubstitutionDatesRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String dateDay = dataSnapshot.getValue(String.class);
				if (dateDay != null && !dateDay.isEmpty()) {
					String day = dateDay.split(" ")[1];
					if (day != null && !day.isEmpty()) {
						mTitle = day;
					}
				}
				mPagerAdapter.getTabLayout().getTabAt(index - 1).setText(mTitle);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});
	}

	private void initRecyclerView() {
		RecyclerView recyclerView = mBinding.recyclerView;
		recyclerAdapter = new SingleDaySubRecyclerAdapter(getContext(), this, this);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
		recyclerView.setAdapter(this.recyclerAdapter);
		recyclerView.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				// TODO: Maybe add anything here, code below comes from Lars
				//if (newState == 1 && MainActivity.itemSheet.getState() != 5) {
				//	MainActivity.itemSheet.setState(5);
				//}
			}
		});
	}

	@Override
	public void onLoadingFinished() {
		swipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onItemClick(SubstitutionEvent event) {
		mBinding.setSubstitutionEvent(event);
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	/**
	 * Checks if enough time @code(IDLE_TIME) has passed since the last refresh
	 *
	 * @author Lars
	 */
	class SetBackRefreshListener implements OnRefreshListener {

		private long mLastChecked;

		SetBackRefreshListener() {
			mLastChecked = System.currentTimeMillis();
		}

		public void onRefresh() {

			if (mLastChecked < System.currentTimeMillis() - IDLE_TIME) {
				refreshItems();
				mLastChecked = System.currentTimeMillis();
			} else {
				onLoadingFinished();
			}
		}
	}
}