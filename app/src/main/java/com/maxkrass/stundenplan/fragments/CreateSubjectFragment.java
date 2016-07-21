package com.maxkrass.stundenplan.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.contracts.CreateSubjectContract;
import com.maxkrass.stundenplan.databinding.ColorRowBinding;
import com.maxkrass.stundenplan.databinding.FragmentCreateSubjectBinding;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Teacher;

import java.util.HashMap;

public class CreateSubjectFragment extends Fragment implements View.OnClickListener, CreateSubjectContract.View {

	public View colorIcon;

	TextView selectTeacher;
	Subject subject;
	ListAdapter adapter;
	FragmentCreateSubjectBinding binding;

	OnChooseListener mOnChooseListener;
	CreateSubjectContract.Presenter mPresenter;

	@Override
	public void setPresenter(CreateSubjectContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void nameInvalid() {
		((TextInputLayout) binding.subjectName.getParent()).setError("The name can't be empty");
	}

	@Override
	public void subjectAlreadyExists() {
		((TextInputLayout) binding.subjectName.getParent()).setError("You already created this subject");
	}

	@Override
	public void abbreviationInvalid() {
		((TextInputLayout) binding.subjectAbbr.getParent()).setError("I need an abbreviation");
	}

	@Override
	public void removeErrors() {
		((TextInputLayout) binding.subjectName.getParent()).setError("");
		((TextInputLayout) binding.subjectAbbr.getParent()).setError("");
	}

	@Override
	public void savingFailed() {
		Snackbar.make(binding.getRoot(), "Saving Failed!", Snackbar.LENGTH_SHORT);
	}

	@Override
	public void exitCreateDialog() {
		getActivity().finish();
	}

	@Override
	public void showSubject(Subject subject) {
		this.subject = subject;
	}

	public interface OnChooseListener {
		void onShowChosenColor(String fromColor, String toColor);

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

	public void onTeacherChosen(Teacher teacher) {
		HashMap<String, Boolean> map = new HashMap<>(1);
		map.put(teacher.getTeacherName(), true);
		subject.setTeacher(map);
	}

	@Override
	public void onResume() {
		super.onResume();
		mPresenter.start();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (subject == null) subject = new Subject();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_subject, container, false);
		selectTeacher = binding.selectTeacher;
		subject.setColor("#F44336");
		binding.setSubject(subject);
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

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				ColorRowBinding colorRowBinding;

				if (convertView == null) {
					colorRowBinding = ColorRowBinding.inflate(inflater, parent, false);
					convertView = colorRowBinding.getRoot();
				} else {
					colorRowBinding = DataBindingUtil.findBinding(convertView);
				}

				colorRowBinding.setColor(Color.values()[position]);

				return convertView;
			}
		};

		return binding.getRoot();
	}

	@Override
	public void onClick(View v) {
		mPresenter.validateSubject(subject.getName(), subject.getAbbreviation(), subject.getColor(), subject.getTeacher());
	}

	public void selectColor() {
		new AlertDialog.Builder(getActivity()).setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String[] colors = getResources().getStringArray(R.array.colors);
				String newColor = colors[which];
				//((GradientDrawable) colorIcon.getBackground()).setColor(newColor.getColor(getActivity()));
				binding.colorNameLabel.setText(newColor);

				if (which < Color.values().length && which >= 0) {
					colorIcon.setVisibility(View.VISIBLE);
					mOnChooseListener.onShowChosenColor(subject.getColor(), newColor);
					//((GradientDrawable) colorIcon.getBackground()).setColor(newColor.getColor(getActivity()));
					subject.setColor(newColor);
				}

			}
		}).show();
	}
}
