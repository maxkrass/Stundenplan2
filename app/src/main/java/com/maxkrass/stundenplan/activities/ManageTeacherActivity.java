package com.maxkrass.stundenplan.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.adapter.TeachersAdapter;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

import java.util.List;

public class ManageTeacherActivity extends BaseActivity {

	static RecyclerView teacherRecyclerView;
	boolean select = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		select = getIntent().getBooleanExtra("select", false);
		setContentView(R.layout.activity_manage_teacher);
		Toolbar toolbar = (Toolbar) findViewById(R.id.manage_teacher_toolbar);
		toolbar.setPadding(0, Tools.getStatusBarHeight(this), 0, 0);
		setActionBar(toolbar);
		teacherRecyclerView = (RecyclerView) findViewById(R.id.teachers_recyclerview);
		teacherRecyclerView.setHasFixedSize(true);
		teacherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	    /*View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("position", teacherRecyclerView.getChildAdapterPosition(v));
                    setResult(RESULT_OK, resultIntent);
                    finish();

                } else {
                    Intent intent = new Intent(getApplicationContext(), ViewTeacherActivity.class);
                    intent.putExtra("teacherID", teacherRecyclerView.getChildAdapterPosition(v));
                    intent.putExtra(IntentUtil.FONT_SIZE, binding.author.getTextSize());
                    intent.putExtra(IntentUtil.PADDING,
                            new Rect(binding.author.getPaddingLeft(),
                                    binding.author.getPaddingTop(),
                                    binding.author.getPaddingRight(),
                                    binding.author.getPaddingBottom()));
                    intent.putExtra(IntentUtil.TEXT_COLOR,
                            binding.author.getCurrentTextColor());
                    getWindow().setExitTransition(new /*Slide(Gravity.TOP)*//*Fade().excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
                    ArrayList<Pair<View, String>> elements = new ArrayList<>();
                    elements.add(new Pair<>(v.findViewById(R.id.teacher_name), "teacher_name"));
                    if(v.findViewById(R.id.call_teacher).getVisibility() != View.GONE) {
                        elements.add(new Pair<>(v.findViewById(R.id.call_teacher), "teacher_phone_icon"));
                    }
                    if(v.findViewById(R.id.email_teacher).getVisibility() != View.GONE) {
                        elements.add(new Pair<>(v.findViewById(R.id.email_teacher), "teacher_email_icon"));
                    }
                    Pair<View, String>[] viewStringPair = new Pair[elements.size()];
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ManageTeacherActivity.this, elements.toArray(viewStringPair));
                    startActivity(intent, options.toBundle());
                }
            }
        };*/
		teacherRecyclerView.setAdapter(new TeachersAdapter(this, select));
		final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_teacher);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                /*floatingActionButton.setClickable(false);
                int[] location = new int[2];
                floatingActionButton.getLocationOnScreen(location);

                location[0] += floatingActionButton.getWidth() / 2;
                location[1] += floatingActionButton.getHeight() / 2;

                final Intent intent = new Intent(getApplicationContext(), CreateTeacherActivity.class);

                final RevealLayout mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
                final View mRevealView = findViewById(R.id.reveal_view);

                mRevealView.setVisibility(View.VISIBLE);
                mRevealLayout.setVisibility(View.VISIBLE);

                mRevealLayout.show(location[0], location[1], 150);

                final TransitionDrawable transitionDrawable = (TransitionDrawable) mRevealView.getBackground();
                transitionDrawable.startTransition(150);

                floatingActionButton.hide();

                floatingActionButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent.putExtra("edit", false);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ManageTeacherActivity.this, mRevealLayout, "fabTransition");
                        startActivity(intent, options.toBundle());
                        getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));
                        getWindow().setExitTransition(new Slide(Gravity.TOP));

                        //overridePendingTransition(0, R.anim.hold);
                    }
                }, 150);
                floatingActionButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        transitionDrawable.resetTransition();
                        floatingActionButton.show();
                        floatingActionButton.setClickable(true);
                        mRevealView.setVisibility(View.INVISIBLE);
                        mRevealLayout.setVisibility(View.INVISIBLE);
                    }
                }, 600);*/
				startActivity(new Intent(getApplicationContext(), CreateTeacherActivity.class).putExtra("edit", false));
			}
		});
	}

	public static class TeacherDialog extends DialogFragment {

		Teacher teacher;

		public TeacherDialog() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			teacher = SugarRecord.findById(Teacher.class, getArguments().getLong("teacherID"));
			if (teacher != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_teacher, null);
				((TextView) view.findViewById(R.id.dialog_teacher_title)).setText(teacher.getName());
				view.findViewById(R.id.dialog_teacher_delete).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final int position = Tools.getTeacherPosition(teacher);

						TeacherDialog.this.dismiss();
						Snackbar.make(teacherRecyclerView, teacher.getName() + " deleted", Snackbar.LENGTH_LONG)
								.setCallback(new Snackbar.Callback() {
									@Override
									public void onDismissed(Snackbar snackbar, int event) {
										switch (event) {
											case DISMISS_EVENT_ACTION:
											case DISMISS_EVENT_MANUAL:
												break;
											case DISMISS_EVENT_CONSECUTIVE:
											case DISMISS_EVENT_SWIPE:
											case DISMISS_EVENT_TIMEOUT:
												List<Subject> subjects = Subject.find(Subject.class, "teacher = ?", teacher.getId().toString());
												for (Subject s : subjects) {
													Teacher blankTeacher = new Teacher();
													blankTeacher.setId(0L);
													s.setTeacher(blankTeacher);
													s.save();
												}
												teacher.delete();
												break;
										}
									}
								})
								.setAction("restore", new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										((TeachersAdapter) teacherRecyclerView.getAdapter()).add(position, teacher);
										teacherRecyclerView.getAdapter().notifyItemInserted(position);
									}
								}).show();
						((TeachersAdapter) teacherRecyclerView.getAdapter()).remove(position);
						teacherRecyclerView.getAdapter().notifyItemRemoved(position);
					}
				});
				view.findViewById(R.id.dialog_teacher_edit).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						TeacherDialog.this.dismiss();
						Intent intent = new Intent(getActivity(), CreateTeacherActivity.class);
						intent.putExtra("edit", true);
						intent.putExtra("teacherId", teacher.getId());
						startActivity(intent);
					}
				});
				builder.setView(view);
				return builder.create();
			} else {
				return null;
			}
		}
	}

}
