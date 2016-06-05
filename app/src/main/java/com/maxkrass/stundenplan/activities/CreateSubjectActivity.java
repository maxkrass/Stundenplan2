package com.maxkrass.stundenplan.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.fragments.CreateSubjectFragment;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

public class CreateSubjectActivity extends BaseActivity {

	private final int SELECT_REQUEST_CODE = 123;

	private Subject subject = new Subject();

	TextView selectTeacher;
	EditText subjectName;
	EditText subjectAbbr;
	Toolbar mToolbar;
	Color color;
	View mRevealView;
	View mRevealBackgroundView;
	CreateSubjectFragment createSubjectFragment;
	ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_subject);
		mRevealView = findViewById(R.id.reveal);
		mRevealBackgroundView = findViewById(R.id.revealBackground);
		color = Color.RED;
		createSubjectFragment = ((CreateSubjectFragment)getFragmentManager().findFragmentById(R.id.fragment_create_subject));
		adapter = new ArrayAdapter<Color>(getApplicationContext(), R.layout.color_row, Color.values()) {

			ViewHolder viewHolder;
			Color color;

			class ViewHolder {
				View color;
				TextView colorName;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = getLayoutInflater();

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.color_row, null);

					viewHolder = new ViewHolder();
					viewHolder.color = convertView.findViewById(R.id.color);
					viewHolder.colorName = (TextView) convertView.findViewById(R.id.name);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}

				color = Color.values()[position];

				((GradientDrawable) viewHolder.color.getBackground()).setColor(color.getColor(CreateSubjectActivity.this));
				viewHolder.colorName.setText(color.toString());

				return convertView;
			}
		};
		//final FrameLayout chooseColor = (FrameLayout) findViewById(R.id.choose_color);
		/*chooseColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(CreateSubjectActivity.this);
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Color newColor = Color.values()[which];
						subject.setColorIndex(which);
						((GradientDrawable) chooseColor.findViewById(R.id.color_icon).getBackground()).setColor(newColor.getColor(CreateSubjectActivity.this));
						((TextView) chooseColor.findViewById(R.id.color_name_label)).setText(newColor.toString());

						if (which < Color.values().length && which >= 0) {
							createSubjectFragment.colorIcon.setVisibility(View.VISIBLE);
							int newColorValue = newColor.getColor(CreateSubjectActivity.this);
							animateAppAndStatusBar(color.getColor(CreateSubjectActivity.this), newColorValue);
							((GradientDrawable) createSubjectFragment.colorIcon.getBackground()).setColor(newColorValue);
							color = newColor;

						}

					}
				});
				builder.create().show();
			}
		});*/
		mToolbar = (Toolbar) findViewById(R.id.create_subject_toolbar);
		selectTeacher = (TextView) findViewById(R.id.select_teacher);
		selectTeacher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ManageTeacherActivity.class);
				intent.putExtra("select", true);
				startActivityForResult(intent, SELECT_REQUEST_CODE);
			}
		});
		//((View) mToolbar.getParent()).setPadding(0, Tools.getStatusBarHeight(this), 0, 0);
		//setActionBar(mToolbar);
		subjectName = (EditText) findViewById(R.id.subject_name);
		subjectAbbr = (EditText) findViewById(R.id.subject_abbr);
		ImageView cancel = (ImageView) findViewById(R.id.cancel_subject);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView saveSubject = (ImageView) findViewById(R.id.save_subject);
		saveSubject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean error = false;
				if (TextUtils.isEmpty(subjectName.getText())) {
					((TextInputLayout) subjectName.getParent()).setError("Das Fach braucht einen Namen");
					error = true;
				} else {
					((TextInputLayout) subjectName.getParent()).setError("");
					if (TextUtils.isEmpty(subjectAbbr.getText())) {
						((TextInputLayout) subjectAbbr.getParent()).setError("Bitte gib eine Abkürzung an");
						error = true;
					}
				}
				if (!error) {
					subject.setAbbreviation(subjectAbbr.getText().toString());
					subject.setName(subjectName.getText().toString());
					subject.save();
					finish();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_REQUEST_CODE && resultCode == RESULT_OK) {

			Bundle bundle = data.getExtras();

			int position = bundle.getInt("position");

			Teacher teacher = SugarRecord.listAll(Teacher.class, "name").get(position);

			subject.setTeacher(teacher);

			selectTeacher.setText(teacher.getName());

		}
	}

	public void selectColor(final View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(CreateSubjectActivity.this);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Color newColor = Color.values()[which];
				subject.setColorIndex(which);
				((GradientDrawable) v.findViewById(R.id.color_icon).getBackground()).setColor(newColor.getColor(CreateSubjectActivity.this));
				((TextView) v.findViewById(R.id.color_name_label)).setText(newColor.toString());

				if (which < Color.values().length && which >= 0) {
					createSubjectFragment.colorIcon.setVisibility(View.VISIBLE);
					int newColorValue = newColor.getColor(CreateSubjectActivity.this);
					animateAppAndStatusBar(color.getColor(CreateSubjectActivity.this), newColorValue);
					((GradientDrawable) createSubjectFragment.colorIcon.getBackground()).setColor(newColorValue);
					color = newColor;

				}

			}
		});
		builder.create().show();
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
}
