package com.maxkrass.stundenplan.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.SubjectsAdapter;

import java.util.ArrayList;

public class ManageSubjectsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_subjects);
		Toolbar toolbar = (Toolbar) findViewById(R.id.manage_subjects_toolbar);
		setActionBar(toolbar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.subjects_recyclerview);

		recyclerView.setHasFixedSize(true);

		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent activityIntent = getIntent();
				if (activityIntent.getBooleanExtra("select", false)) {
					Intent result = new Intent();
					result.putExtra("subjectID", recyclerView.getChildAdapterPosition(v));
					setResult(Activity.RESULT_OK, result);
					finish();
				} else {
					Intent newActivity = new Intent(ManageSubjectsActivity.this, ViewSubjectActivity.class);
					newActivity.putExtra("subjectID", recyclerView.getChildAdapterPosition(v));
					getWindow().setExitTransition(new Fade().excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
					ArrayList<Pair<View, String>> elements = new ArrayList<>();
					elements.add(new Pair<>(v.findViewById(R.id.subject_color), "subject_color"));
					elements.add(new Pair<>(v.findViewById(R.id.subject_name), "subject_name"));
					Pair<View, String>[] viewStringPair = new Pair[elements.size()];
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ManageSubjectsActivity.this, elements.toArray(viewStringPair));
					startActivity(newActivity, options.toBundle());
				}
			}
		};

		View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		};

		recyclerView.setAdapter(new SubjectsAdapter(ManageSubjectsActivity.this, onClickListener, onLongClickListener));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_manage_subjects, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void createSubject(View v) {
		startActivity(new Intent(ManageSubjectsActivity.this, CreateSubjectActivity.class));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
