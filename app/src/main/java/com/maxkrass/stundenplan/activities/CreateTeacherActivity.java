package com.maxkrass.stundenplan.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.data.TeacherRepository;
import com.maxkrass.stundenplan.databinding.ActivityCreateTeacherBinding;
import com.maxkrass.stundenplan.fragments.CreateTeacherFragment;
import com.maxkrass.stundenplan.presenters.CreateTeacherPresenter;

public class CreateTeacherActivity extends BaseActivity {

	private static final String CREATE_TEACHER_TAG = "create_teacher_fragment";
	public static final int REQUEST_CREATE_TEACHER = 0x300;
	public static final int REQUEST_EDIT_TEACHER = 0x200;
	Toolbar toolbar;
	CreateTeacherFragment createTeacherFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCreateTeacherBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_create_teacher);
		toolbar = binding.createTeacherToolbar;
		createTeacherFragment = (CreateTeacherFragment) getSupportFragmentManager().findFragmentByTag(CREATE_TEACHER_TAG);
		if (createTeacherFragment == null) {
			createTeacherFragment = new CreateTeacherFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, createTeacherFragment).commit();
		}
		setActionBar(toolbar);
		binding.saveTeacher.setOnClickListener(createTeacherFragment);
		binding.cancelTeacher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		new CreateTeacherPresenter(new TeacherRepository(getUid()), createTeacherFragment, getIntent().getStringExtra("teacherKey"));
	}
}
