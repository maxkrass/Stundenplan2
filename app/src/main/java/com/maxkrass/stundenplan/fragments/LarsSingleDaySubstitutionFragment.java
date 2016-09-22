package com.maxkrass.stundenplan.fragments;

import android.os.AsyncTask;
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

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.adapter.LarsRecyclerAdapter;
import com.maxkrass.stundenplan.adapter.SubstitutionPlanPagerAdapter;
import com.maxkrass.stundenplan.databinding.RecycleViewFragmentBinding;
import com.maxkrass.stundenplan.objects.LarsSubstitutionEvent;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LarsSingleDaySubstitutionFragment extends Fragment implements LarsRecyclerAdapter.OnLoadingFinishedListener, LarsRecyclerAdapter.OnSubstitutionItemClickListener {
	private static final long idleTime = 60000;
	public LarsRecyclerAdapter recyclerAdapter;
	DatabaseReference            mSubstitutionPlanRef;
	DatabaseReference            mSubstitutionSubjectsRef;
	SubstitutionPlanPagerAdapter mPagerAdapter;
	RecycleViewFragmentBinding   mBinding;
	BottomSheetBehavior          mBottomSheetBehavior;
	private int                     index;
	private String                  mTitle;
	private String                  mUId;
	private HashMap<String, String> mSubstitutionSubjects;
	private SwipeRefreshLayout      swipeRefreshLayout;

	public static LarsSingleDaySubstitutionFragment newInstance(int index, String uId, SubstitutionPlanPagerAdapter pagerAdapter) {
		LarsSingleDaySubstitutionFragment fragment = new LarsSingleDaySubstitutionFragment();
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
		swipeRefreshLayout.setOnRefreshListener(new C08461());
		initRecyclerView();
		mSubstitutionPlanRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(mUId)
				.child("substitutionPlan")
				.child(index + "");
		mSubstitutionSubjectsRef = FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(mUId)
				.child("substitutionSubjects");
		mSubstitutionPlanRef.addValueEventListener(new C08472());
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
					mSubstitutionSubjects.put(mBinding.getSubstitutionEvent().getSubject(), mBinding.getSubstitutionEvent().getGrade().name());
					mSubstitutionSubjectsRef.setValue(mSubstitutionSubjects);
					recyclerAdapter.notifyDataSetChanged();
				}
			}
		});
		refreshItems();
		return mBinding.getRoot();
	}

	public void refreshItems() {
		new BackgroundTask().execute();
	}

	private void initRecyclerView() {
		RecyclerView recyclerView = mBinding.recyclerView;
		recyclerAdapter = new LarsRecyclerAdapter(this, this);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
		recyclerView.setAdapter(this.recyclerAdapter);
		recyclerView.addOnScrollListener(new C08483());
	}

	@Override
	public void onLoadingFinished() {
		swipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onItemClick(LarsSubstitutionEvent event) {
		mBinding.setSubstitutionEvent(event);
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	class BackgroundTask extends AsyncTask<Void, Void, ArrayList<LarsSubstitutionEvent>> {

		protected ArrayList<LarsSubstitutionEvent> doInBackground(Void... params) {
			try {
				Connection.Response response = Jsoup.connect("http://www.mpg-plan.max-planck-gymnasium-duesseldorf.de/Vertretungsplan/Moodle/SII/t" + index + "/subst_001.htm").execute();
				Document document = response.parse();
				Element table = document.select("table.mon_list").first();
				String date = document.select("div.mon_title").first().ownText();
				mTitle = date.substring(date.indexOf(" "));
				Elements rows = table.select(".odd, .even");

				ArrayList<LarsSubstitutionEvent> events = new ArrayList<>();
				outerLoop:
				for (int i1 = 0; i1 < rows.size(); i1++) {
					Element element = rows.get(i1);
					LarsSubstitutionEvent event = new LarsSubstitutionEvent();
					for (int i = 0; i < element.childNodeSize(); i++) {
						switch (i) {
							case 0:
								String grade = element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim().toUpperCase();
								if (!grade.isEmpty())
									event.setGrade(LarsSubstitutionEvent.Grade.valueOf(grade));
								else {
									if (i1 > 0)
										events.get(events.size() - 1).setAnnotation((events.get(events.size() - 1).getAnnotation() + " " + element.child(7).ownText()).trim());
									continue outerLoop;
								}
								break;
							case 1:
								event.setPeriod(element.child(i).ownText());
								break;
							case 2:
								String struckSubject = element.child(i).html();
								if (struckSubject.startsWith("<strike>")) {
									struckSubject = struckSubject.replace("<strike>", "").replace("</strike>", "");
								}
								event.setSubject(struckSubject.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim().toUpperCase());
								break;
							case 3:
								event.setType(LarsSubstitutionEvent.SubstitutionType.getTypeByString(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim()));
								break;
							case 4:
								String struckOldTeacher = element.child(i).html();
								if (struckOldTeacher.startsWith("<strike>")) {
									struckOldTeacher = struckOldTeacher.replace("<strike>", "").replace("</strike>", "");
								}
								event.setOldTeacher(struckOldTeacher.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								break;
							case 5:
								if (!Objects.equals(event.getType(), LarsSubstitutionEvent.SubstitutionType.Cancelled)) {
									String struckSub = element.child(i).html();
									if (struckSub.startsWith("<strike>")) {
										struckSub = struckSub.replace("<strike>", "").replace("</strike>", "");
									}
									event.setSub(struckSub.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								}
								break;
							case 6:
								if (!Objects.equals(event.getType(), LarsSubstitutionEvent.SubstitutionType.Cancelled)) {
									event.setNewLocation(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
								}
								break;
							case 7:
								event.setAnnotation(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
						}
					}
					events.add(event);

				}
				return events;
			} catch (IOException e) {
				e.printStackTrace();
				Snackbar.make(swipeRefreshLayout, "Bitte überprüfe deine Internetverbindung", Snackbar.LENGTH_LONG).show();
				FirebaseCrash.log("HTML Parsing Error for day: " + index + ". IOException.");
				FirebaseCrash.report(e);
				return new ArrayList<>();
			} catch (Exception e) {
				e.printStackTrace();
				FirebaseCrash.log("HTML Parsing Error for day: " + index + ". Exception.");
				FirebaseCrash.report(e);
				return new ArrayList<>();
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			swipeRefreshLayout.setRefreshing(true);
		}

		protected void onPostExecute(ArrayList<LarsSubstitutionEvent> events) {
			if (events != null) {
				recyclerAdapter.setNewContent(events);
			} else {
				onLoadingFinished();
			}
			mPagerAdapter.getTabLayout().getTabAt(index - 1).setText(mTitle);
		}
	}

	/* renamed from: de.mpgdusseldorf.lpewewq.mpgdsseldorf.LarsSingleDaySubstitutionFragment.1 */
	class C08461 implements OnRefreshListener {
		private long lastChecked;

		C08461() {
			lastChecked = System.currentTimeMillis();
		}

		public void onRefresh() {

			if (lastChecked < System.currentTimeMillis() - idleTime) {
				refreshItems();
				lastChecked = System.currentTimeMillis();
			} else {
				onLoadingFinished();
			}
		}
	}

	/* renamed from: de.mpgdusseldorf.lpewewq.mpgdsseldorf.LarsSingleDaySubstitutionFragment.3 */
	class C08483 extends OnScrollListener {
		C08483() {
		}

		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			//if (newState == 1 && MainActivity.itemSheet.getState() != 5) {
			//	MainActivity.itemSheet.setState(5);
			//}
		}
	}

	class C08472 implements ValueEventListener {
		private long lastChecked;

		C08472() {
			this.lastChecked = System.currentTimeMillis();
		}

		private void checkForIdleTime() {
			if (this.lastChecked < System.currentTimeMillis() - idleTime) {
				refreshItems();
				this.lastChecked = System.currentTimeMillis();
			}
		}

		public void onDataChange(DataSnapshot dataSnapshot) {
			checkForIdleTime();
		}

		public void onCancelled(DatabaseError databaseError) {
			checkForIdleTime();
		}


	}
}