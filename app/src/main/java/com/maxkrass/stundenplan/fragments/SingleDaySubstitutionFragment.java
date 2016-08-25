package com.maxkrass.stundenplan.fragments;

import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maxkrass.stundenplan.adapter.SingleDaySubstitutionRecyclerViewAdapter;
import com.maxkrass.stundenplan.databinding.FragmentSingleDaySubstitutionBinding;
import com.maxkrass.stundenplan.objects.SubstitutionEvent;
import com.maxkrass.stundenplan.tools.Tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Max made this for Stundenplan2 on 17.07.2016.
 */
public class SingleDaySubstitutionFragment extends Fragment {
	private RecyclerView                 mRecyclerView;
	//private SwipeRefreshLayout           mSwipeRefreshLayout;
	//private ProgressBar                  mProgressBar;
	private DatabaseReference            mSubstitutionSubjectsRef;
	private ArrayList<SubstitutionEvent> mEvents;

	public static SingleDaySubstitutionFragment newInstance(int day, String uId) {

		Bundle args = new Bundle();
		args.putInt("day", day);
		args.putString("uId", uId);
		SingleDaySubstitutionFragment fragment = new SingleDaySubstitutionFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mEvents = (ArrayList<SubstitutionEvent>) savedInstanceState.getSerializable("events");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentSingleDaySubstitutionBinding binding = FragmentSingleDaySubstitutionBinding.inflate(inflater);
		mRecyclerView = binding.singleDaySubstitutionGrid;
		//mSwipeRefreshLayout = binding.singleDaySubstitutionSwipeContainer;
		//mProgressBar = binding.singleDaySubstitutionSpinner;

		//mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
		//	@Override
		//	public void onRefresh() {
		//		new SubstitutionTask().execute();
		//	}
		//});
		mSubstitutionSubjectsRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(getArguments().getString("uId"))
				.child("substitutionSubjects");
		if (mEvents == null || mEvents.isEmpty()) {
			new SubstitutionTask().execute();
		} else {
			mRecyclerView.setAdapter(new SingleDaySubstitutionRecyclerViewAdapter(getActivity(), mEvents, mSubstitutionSubjectsRef));
			//mProgressBar.setVisibility(View.INVISIBLE);
		}
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
		mRecyclerView.addItemDecoration(new EqualSpaceItemDecoration((int) Tools.getPixels(4, getContext())));
		mRecyclerView.setHasFixedSize(true);
		return mRecyclerView;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mEvents != null && !mEvents.isEmpty()) {
			mRecyclerView.setAdapter(new SingleDaySubstitutionRecyclerViewAdapter(getActivity(), mEvents, mSubstitutionSubjectsRef));
			//mProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mEvents != null && !mEvents.isEmpty()) {
			outState.putSerializable("events", mEvents);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//if (mSwipeRefreshLayout!=null) {
		//	mSwipeRefreshLayout.setRefreshing(false);
		//	mSwipeRefreshLayout.destroyDrawingCache();
		//	mSwipeRefreshLayout.clearAnimation();
		//}
	}

	@Override
	public void onDestroyView() {
		//mSwipeRefreshLayout.removeAllViews();
		super.onDestroyView();
	}

	private class SubstitutionTask extends AsyncTask<Void, Void, ArrayList<SubstitutionEvent>> {

		@Override
		protected ArrayList<SubstitutionEvent> doInBackground(Void... params) {
			try {
				Document document = Jsoup.connect("http://www.mpg-plan.max-planck-gymnasium-duesseldorf.de/Vertretungsplan/Moodle/SII/t" + (getArguments().getInt("day")) + "/subst_001.htm").get();
				Element table = document.select("table.mon_list").first();
				Elements rows = table.select(".odd, .even");

				ArrayList<SubstitutionEvent> events = new ArrayList<>();

				for (Element element : rows) {
					SubstitutionEvent event = new SubstitutionEvent();
					for (int i = 0; i < element.childNodeSize(); i++) {
						switch (i) {
							case 0:
								event.setGrade(SubstitutionEvent.Grade.valueOf(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim()));
								break;
							case 1:
								event.setPeriod((element.child(i).ownText().substring(0, 1)));
								break;
							case 2:
								String struckSubject = element.child(i).html();
								if (struckSubject.startsWith("<strike>")) {
									struckSubject = struckSubject.replace("<strike>", "").replace("</strike>", "");
								}
								event.setSubject(struckSubject.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								break;
							case 3:
								event.setType(SubstitutionEvent.SubstitutionType.getTypeByString(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim()));
								break;
							case 4:
								String struckOldTeacher = element.child(i).html();
								if (struckOldTeacher.startsWith("<strike>")) {
									struckOldTeacher = struckOldTeacher.replace("<strike>", "").replace("</strike>", "");
								}
								event.setOldTeacher(struckOldTeacher.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								break;
							case 5:
								if (!event.getType().equals(SubstitutionEvent.SubstitutionType.Cancelled)) {
									String struckSub = element.child(i).html();
									if (struckSub.startsWith("<strike>")) {
										struckSub = struckSub.replace("<strike>", "").replace("</strike>", "");
									}
									event.setSub(struckSub.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								}
								break;
							case 6:
								if (!event.getType().equals(SubstitutionEvent.SubstitutionType.Cancelled)) {
									event.setNewLocation(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								}
								break;
						}
					}
					events.add(event);
				}
				return events;
			} catch (IOException e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}

		@Override
		protected void onPostExecute(ArrayList<SubstitutionEvent> events) {
			super.onPostExecute(events);
			mRecyclerView.setAdapter(new SingleDaySubstitutionRecyclerViewAdapter(getActivity(), events, mSubstitutionSubjectsRef));
			//mProgressBar.setVisibility(View.INVISIBLE);
			//mSwipeRefreshLayout.setRefreshing(false);
			SingleDaySubstitutionFragment.this.mEvents = events;
		}
	}

	private class EqualSpaceItemDecoration extends RecyclerView.ItemDecoration {

		private final int mSpace;

		public EqualSpaceItemDecoration(int mSpace) {
			this.mSpace = mSpace;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
		                           RecyclerView.State state) {
			outRect.bottom = mSpace;
			outRect.top = mSpace;
			outRect.left = mSpace;
			outRect.right = mSpace;
		}
	}

}
