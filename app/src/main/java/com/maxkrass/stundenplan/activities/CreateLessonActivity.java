package com.maxkrass.stundenplan.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.customViews.CheckBoxWidget;
import com.maxkrass.stundenplan.data.LessonRepository;
import com.maxkrass.stundenplan.databinding.ActivityCreateLessonBinding;
import com.maxkrass.stundenplan.fragments.CreateLessonFragment;
import com.maxkrass.stundenplan.fragments.ManageSubjectsFragment;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Weekday;
import com.maxkrass.stundenplan.presenters.CreateLessonPresenter;

public class CreateLessonActivity extends BaseActivity implements CreateLessonFragment.OnChooseSubjectListener, ManageSubjectsFragment.OnSubjectChosenListener {

	private int color;

	private View                        mRevealView;
	private View                        mRevealBackgroundView;
	private ActivityCreateLessonBinding binding;
	private Toolbar                     mToolbar;
	private CreateLessonFragment        createLessonFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_create_lesson);
		String CREATE_LESSON_TAG = "create_lesson_fragment";
		createLessonFragment = (CreateLessonFragment) getSupportFragmentManager().findFragmentByTag(CREATE_LESSON_TAG);
		if (createLessonFragment == null) {
			createLessonFragment = new CreateLessonFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, createLessonFragment, CREATE_LESSON_TAG).commit();
		}
		binding.saveLesson.setOnClickListener(createLessonFragment);
		binding.cancelLesson.setOnClickListener(this);
		mRevealView = binding.reveal;
		mRevealBackgroundView = binding.revealBackground;
		mToolbar = binding.createLessonToolbar;
		color = Color.LIGHT_GREEN.getColor(this);

		new CreateLessonPresenter(
				createLessonFragment,
				new LessonRepository(getUid()),
				(Weekday) getIntent().getSerializableExtra("weekday"),
				(Integer) getIntent().getSerializableExtra("period"),
				(Boolean) getIntent().getSerializableExtra("doublePeriod"));
	}

	public void onClick(View view) {
		if (view instanceof CheckBoxWidget)
			((CheckBoxWidget) view).toggle();
		else if (view.getId() == R.id.cancel_lesson)
			finish();
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
	public void onSubjectChosen(Subject subject) {
		restoreView();
		int newColor = subject.getColorInt();
		animateAppAndStatusBar(color, newColor);
		color = newColor;
		createLessonFragment.onSubjectChosen(subject);
	}

	@Override
	public void onNoneChosen() {
		restoreView();
		createLessonFragment.onNoSubjectChosen();
	}

	@Override
	public void onRequestChooseSubject() {
		ManageSubjectsFragment manageSubjectsFragment = new ManageSubjectsFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("select", true);
		manageSubjectsFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).hide(createLessonFragment).add(R.id.second_fragment_container, manageSubjectsFragment).addToBackStack(null).commit();
		binding.createLessonTitle.setText(R.string.choose_subject_title);
		binding.saveLesson.setText(R.string.action_none);
		binding.saveLesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNoneChosen();
			}
		});
		binding.cancelLesson.setImageResource(R.drawable.ic_arrow_back_24dp);
		binding.cancelLesson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				restoreView();
			}
		});
	}

	private void restoreView() {
		getSupportFragmentManager().popBackStack();
		getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.main_fragment_container, createLessonFragment).commit();
		binding.createLessonTitle.setText(R.string.new_lesson_title);
		binding.saveLesson.setText(R.string.action_save);
		binding.saveLesson.setOnClickListener(createLessonFragment);
		binding.cancelLesson.setImageResource(R.drawable.ic_clear_24dp);
		binding.cancelLesson.setOnClickListener(this);
	}
}
