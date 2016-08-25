package com.maxkrass.stundenplan.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.data.SubjectRepository;
import com.maxkrass.stundenplan.databinding.ActivityCreateSubjectBinding;
import com.maxkrass.stundenplan.fragments.CreateSubjectFragment;
import com.maxkrass.stundenplan.fragments.ManageTeachersFragment;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.presenters.CreateSubjectPresenter;

public class CreateSubjectActivity extends BaseActivity implements CreateSubjectFragment.OnChooseListener, ManageTeachersFragment.OnTeacherChosenListener {

	private Toolbar                      mToolbar;
	private View                         mRevealView;
	private View                         mRevealBackgroundView;
	private CreateSubjectFragment        createSubjectFragment;
	private ActivityCreateSubjectBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_create_subject);
		mRevealView = binding.reveal;
		mRevealBackgroundView = binding.revealBackground;
		String CREATE_SUBJECT_TAG = "create_subject_fragment";
		createSubjectFragment = (CreateSubjectFragment) getSupportFragmentManager().findFragmentByTag(CREATE_SUBJECT_TAG);
		if (createSubjectFragment == null) {
			createSubjectFragment = new CreateSubjectFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, createSubjectFragment, CREATE_SUBJECT_TAG).commit();
		}

		new CreateSubjectPresenter(new SubjectRepository(getUid()), createSubjectFragment, getIntent().getStringExtra("subjectKey"));

		mToolbar = binding.createSubjectToolbar;

		//((View) mToolbar.getParent()).setPadding(0, Tools.getStatusBarHeight(this), 0, 0);
		setActionBar(mToolbar);
		binding.cancelSubject.setOnClickListener(this);
		binding.saveSubject.setOnClickListener(createSubjectFragment);
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_REQUEST_CODE && resultCode == RESULT_OK) {

			Bundle bundle = data.getExtras();

			int position = bundle.getInt("position");

			Teacher teacher = SugarRecord.listAll(Teacher.class, "teacher_name").get(position);

			subject.setTeacher(teacher);

			selectTeacher.setText(teacher.getName());

		}
	}*/

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
			case R.id.cancel_subject:
				finish();
		}
	}

	private void animateAppAndStatusBar(int fromColor, final int toColor) {
		Animator animator = ViewAnimationUtils.createCircularReveal(
				mRevealView,
				mToolbar.getWidth() / 2,
				mToolbar.getHeight() / 2, 0,
				mToolbar.getWidth() / 2);

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mRevealView.setBackgroundColor(toColor);
			}
		});

		mRevealBackgroundView.setBackgroundColor(fromColor);
		animator.setStartDelay(200);
		animator.setDuration(125);
		animator.start();
		mRevealView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onShowChosenColor(String fromColor, String toColor) {
		animateAppAndStatusBar(Color.parseColor(fromColor), Color.parseColor(toColor));
	}

	@Override
	public void onRequestChooseTeacher() {
		ManageTeachersFragment manageTeachersFragment = new ManageTeachersFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("select", true);
		manageTeachersFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).hide(createSubjectFragment).add(R.id.second_fragment_container, manageTeachersFragment).addToBackStack(null).commit();
		binding.createSubjectTitle.setText("Choose Teacher");
		binding.saveSubject.setText(getString(R.string.action_none));
		binding.saveSubject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNoneChosen();
			}
		});
		binding.cancelSubject.setImageResource(R.drawable.ic_arrow_back_24dp);
		binding.cancelSubject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				restoreView();
			}
		});
	}

	@Override
	public void onTeacherChosen(Teacher teacher) {
		restoreView();
		createSubjectFragment.onTeacherChosen(teacher);
	}

	@Override
	public void onNoneChosen() {
		restoreView();
		createSubjectFragment.onTeacherChosen(null);
	}

	private void restoreView() {
		getSupportFragmentManager().popBackStack();
		getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, createSubjectFragment).commit();
		binding.createSubjectTitle.setText("New Subject");
		binding.saveSubject.setText(getString(R.string.action_save));
		binding.saveSubject.setOnClickListener(createSubjectFragment);
		binding.cancelSubject.setImageResource(R.drawable.ic_clear_24dp);
		binding.cancelSubject.setOnClickListener(this);
	}
}
