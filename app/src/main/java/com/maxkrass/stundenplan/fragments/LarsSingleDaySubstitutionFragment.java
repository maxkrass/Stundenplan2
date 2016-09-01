package com.maxkrass.stundenplan.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.LarsRecyclerAdapter;
import com.maxkrass.stundenplan.objects.LarsSubstitutionEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LarsSingleDaySubstitutionFragment extends Fragment {
	private static final long idleTime = 60000;
	public  Object              aktuelleListe;
	public  LarsRecyclerAdapter recyclerAdapter;
	private int                 index;
	private RecyclerView        recyclerView;
	private SwipeRefreshLayout  swipeRefreshLayout;

	public static LarsSingleDaySubstitutionFragment newInstance(int index) {
		LarsSingleDaySubstitutionFragment fragment = new LarsSingleDaySubstitutionFragment();
		fragment.index = index;
		return fragment;
	}

	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.recycle_view_fragment, container, false);
		this.swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
		this.swipeRefreshLayout.setOnRefreshListener(new C08461());
		initRecyclerView(v);
		//MainActivity.rootRef.child(String.valueOf(this.index)).addValueEventListener(new C08472());
		refreshItems();
		return v;
	}

	public void refreshItems() {
		new BackgroundTask().execute();
	}

	private void initRecyclerView(View v) {
		this.recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
		this.recyclerAdapter = new LarsRecyclerAdapter(this.index);
		this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
		this.recyclerView.setAdapter(this.recyclerAdapter);
		this.recyclerView.addOnScrollListener(new C08483());
	}

	class BackgroundTask extends AsyncTask<Void, Void, ArrayList<LarsSubstitutionEvent>> {

		protected ArrayList<LarsSubstitutionEvent> doInBackground(Void... params) {
			try {
				Document document = Jsoup.connect("http://www.mpg-plan.max-planck-gymnasium-duesseldorf.de/Vertretungsplan/Moodle/SII/t" + index + "/subst_001.htm").get();
				Element table = document.select("table.mon_list").first();
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
										events.get(i1 - 1).setAnnotation((events.get(i1 - 1).getAnnotation() + " " + element.child(7).ownText()).trim());
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
				FirebaseCrash.log("HTML Parsing Error for day: " + getArguments().getInt("day"));
				return new ArrayList<>();
			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			LarsSingleDaySubstitutionFragment.this.swipeRefreshLayout.setRefreshing(true);
		}

		protected void onPostExecute(ArrayList<LarsSubstitutionEvent> events) {
			if (events != null && !events.isEmpty()) {
				LarsSingleDaySubstitutionFragment.this.recyclerAdapter.setNewContent(events);
			}
			LarsSingleDaySubstitutionFragment.this.swipeRefreshLayout.setRefreshing(false);
		}
	}

	/* renamed from: de.mpgdusseldorf.lpewewq.mpgdsseldorf.LarsSingleDaySubstitutionFragment.1 */
	class C08461 implements OnRefreshListener {
		C08461() {
		}

		public void onRefresh() {
			LarsSingleDaySubstitutionFragment.this.refreshItems();
		}
	}

	/* renamed from: de.mpgdusseldorf.lpewewq.mpgdsseldorf.LarsSingleDaySubstitutionFragment.2 */
	class C08472 implements ValueEventListener {
		private long lastChecked;

		C08472() {
			this.lastChecked = System.currentTimeMillis();
		}

		private void checkForIdleTime() {
			if (this.lastChecked < System.currentTimeMillis() - LarsSingleDaySubstitutionFragment.idleTime) {
				LarsSingleDaySubstitutionFragment.this.refreshItems();
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
}