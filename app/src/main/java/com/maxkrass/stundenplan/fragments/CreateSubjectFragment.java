package com.maxkrass.stundenplan.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.FragmentCreateSubjectBinding;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Teacher;
import com.orm.SugarRecord;

public class CreateSubjectFragment extends Fragment implements View.OnClickListener {

	public View colorIcon;

	TextView selectTeacher;
	EditText subjectName;
	EditText subjectAbbr;
	Subject subject;
	Color color;
	ListAdapter adapter;
	FragmentCreateSubjectBinding binding;

	OnChooseListener mOnChooseListener;

	public interface OnChooseListener {
		void onShowChosenColor(Color fromColor, Color toColor);

		void onRequestChooseTeacher();
	}

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnChooseListener = (OnChooseListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	public void onTeacherChosen(Long teacherID) {
		subject.setTeacher(SugarRecord.findById(Teacher.class, teacherID));
		//selectTeacher.setText(subject.getTeacher().getName());
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (subject == null)
			subject = new Subject();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_subject, container, false);
		binding.setSubject(subject);
		selectTeacher = binding.selectTeacher;
		subjectName = binding.subjectName;
		subjectAbbr = binding.subjectAbbr;
		color = Color.RED;
		binding.chooseColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectColor();
			}
		});
		selectTeacher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO Intent intent = new Intent(getApplicationContext(), ManageTeachersActivity.class);
				//intent.putExtra("select", true);
				//startActivityForResult(intent, SELECT_REQUEST_CODE);
				mOnChooseListener.onRequestChooseTeacher();
			}
		});
		colorIcon = binding.colorIcon;

		adapter = new ArrayAdapter<Color>(getActivity(), R.layout.color_row, Color.values()) {

			ViewHolder viewHolder;
			Color color;

			class ViewHolder {
				View color;
				TextView colorName;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = getActivity().getLayoutInflater();

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.color_row, parent, false);

					viewHolder = new ViewHolder();
					viewHolder.color = convertView.findViewById(R.id.color);
					viewHolder.colorName = (TextView) convertView.findViewById(R.id.name);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}

				color = Color.values()[position];

				((GradientDrawable) viewHolder.color.getBackground()).setColor(color.getColor(getActivity()));
				viewHolder.colorName.setText(color.toString());

				return convertView;
			}
		};

		return binding.getRoot();
	}

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
			SugarRecord.save(subject);
			getActivity().finish();
		}
	}

	public void selectColor() {
		new AlertDialog.Builder(getActivity()).setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Color newColor = Color.values()[which];
				subject.setColorIndex(which);
				((GradientDrawable) colorIcon.getBackground()).setColor(newColor.getColor(getActivity()));
				binding.colorNameLabel.setText(newColor.toString());

				if (which < Color.values().length && which >= 0) {
					colorIcon.setVisibility(View.VISIBLE);
					mOnChooseListener.onShowChosenColor(color, newColor);
					((GradientDrawable) colorIcon.getBackground()).setColor(newColor.getColor(getActivity()));
					color = newColor;
				}

			}
		}).create().show();
	}
}
