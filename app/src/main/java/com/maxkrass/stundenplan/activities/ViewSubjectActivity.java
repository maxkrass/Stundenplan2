package com.maxkrass.stundenplan.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.transition.Transition;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.EnterSharedElementCallback;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.TransitionUtils;
import com.maxkrass.stundenplan.databinding.ActivityViewSubjectBinding;
import com.maxkrass.stundenplan.objects.Subject;

public class ViewSubjectActivity extends BaseActivity {

	Subject subject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		subject = (Subject) getIntent().getSerializableExtra("subject");
		if (subject == null) {
			finish();
		} else {
			ActivityViewSubjectBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_subject);
			binding.setSubject(subject);
			Transition transition = TransitionUtils.makeSharedElementEnterTransition();
			getWindow().setSharedElementEnterTransition(transition);
			setEnterSharedElementCallback(new EnterSharedElementCallback(this));
			Toolbar toolbar = binding.viewSubjectToolbar;
			setActionBar(toolbar);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finishAfterTransition();
		}
		return super.onOptionsItemSelected(item);
	}
}
