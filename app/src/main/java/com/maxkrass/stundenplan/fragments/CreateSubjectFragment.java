package com.maxkrass.stundenplan.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.contracts.CreateSubjectContract;
import com.maxkrass.stundenplan.databinding.ColorRowBinding;
import com.maxkrass.stundenplan.databinding.FragmentCreateSubjectBinding;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Teacher;

public class CreateSubjectFragment extends Fragment implements View.OnClickListener, CreateSubjectContract.View {

	private View colorIcon;

	private Subject                      subject;
	private ListAdapter                  adapter;
	private FragmentCreateSubjectBinding binding;

	private OnChooseListener                mOnChooseListener;
	private CreateSubjectContract.Presenter mPresenter;

	@Override
	public void setPresenter(CreateSubjectContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void nameInvalid() {
		((TextInputLayout) binding.subjectName.getParent().getParent()).setError(getString(R.string.subject_name_invalid));
	}

	@Override
	public void subjectAlreadyExists() {
		((TextInputLayout) binding.subjectName.getParent().getParent()).setError("You already created this subject");
	}

	@Override
	public void abbreviationInvalid() {
		((TextInputLayout) binding.subjectAbbr.getParent().getParent()).setError("I need an abbreviation");
	}

	@Override
	public void removeErrors() {
		((TextInputLayout) binding.subjectName.getParent().getParent()).setError("");
		((TextInputLayout) binding.subjectAbbr.getParent().getParent()).setError("");
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
		binding.setSubject(this.subject);
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

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (subject == null) subject = new Subject();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_subject, container, false);
		subject.setColor("#F44336");
		binding.setSubject(subject);
		binding.chooseColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectColor();
			}
		});
		binding.selectTeacher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnChooseListener.onRequestChooseTeacher();
			}
		});
		colorIcon = binding.colorIcon;

		adapter = new ArrayAdapter<String>(getActivity(), R.layout.color_row, getResources().getStringArray(R.array.colors)) {

			final String[] colorNames = getResources().getStringArray(R.array.colorNames);

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

				((GradientDrawable) colorRowBinding.colorIcon.getBackground()).setColor(android.graphics.Color.parseColor(getItem(position)));
				colorRowBinding.name.setText(colorNames[position]);

				return convertView;
			}
		};

		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();
		mPresenter.start();
	}

	public void onTeacherChosen(Teacher teacher) {
		subject.setTeacher(teacher == null ? null : teacher.getTeacherName());
	}

	private void selectColor() {
		new AlertDialog.Builder(getActivity()).setAdapter(adapter,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newColor = getResources().getStringArray(R.array.colors)[which];
						((GradientDrawable) colorIcon.getBackground()).setColor(Color.parseColor(newColor));
						binding.colorNameLabel.setText(getResources().getStringArray(R.array.colorNames)[which]);
						mOnChooseListener.onShowChosenColor(subject.getColor(), newColor);
						subject.setColor(newColor);
					}

				}
		).show();
	}

	public interface OnChooseListener {
		void onShowChosenColor(String fromColor, String toColor);

		void onRequestChooseTeacher();
	}

	@Override
	public void onClick(View v) {
		mPresenter.validateSubject(subject.getName(), subject.getAbbreviation(), subject.getColor(), subject.getTeacher());
	}


}
