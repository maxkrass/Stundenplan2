package com.maxkrass.stundenplan.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.objects.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static org.roboguice.shaded.goole.common.base.Preconditions.checkNotNull;

public class GradeCalculatorFragment extends Fragment implements OnClickListener {
	private HashMap<String, Subject> mSubjects;
	private String                   mUid;
	private TextView                 mSubjectList;
	private TextView                 mGradeList;
	private Stack<Integer>           mGrades;
	private TextView                 mGradeAverage;
	private TextView                 mPointsAverage;

	public GradeCalculatorFragment() {
		mSubjects = new HashMap<>();
	}

	public static GradeCalculatorFragment newInstance(String uuid) {
		GradeCalculatorFragment fragment = new GradeCalculatorFragment();
		checkNotNull(uuid);
		fragment.mUid = uuid;
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_grade_calculator, container, false);
		mGrades = new Stack<>();
		mGradeAverage = (TextView) v.findViewById(R.id.notenD);
		mPointsAverage = (TextView) v.findViewById(R.id.punkteD);
		mGradeList = (TextView) v.findViewById(R.id.notenListe);
		mSubjectList = (TextView) v.findViewById(R.id.fachListeTxt);
		Button b0 = (Button) v.findViewById(R.id.b0);
		Button b1 = (Button) v.findViewById(R.id.b1);
		Button b2 = (Button) v.findViewById(R.id.b2);
		Button b3 = (Button) v.findViewById(R.id.b3);
		Button b4 = (Button) v.findViewById(R.id.b4);
		Button b5 = (Button) v.findViewById(R.id.b5);
		Button b6 = (Button) v.findViewById(R.id.b6);
		Button b7 = (Button) v.findViewById(R.id.b7);
		Button b8 = (Button) v.findViewById(R.id.b8);
		Button b9 = (Button) v.findViewById(R.id.b9);
		Button b10 = (Button) v.findViewById(R.id.b10);
		Button b11 = (Button) v.findViewById(R.id.b11);
		Button b12 = (Button) v.findViewById(R.id.b12);
		Button b13 = (Button) v.findViewById(R.id.b13);
		Button b14 = (Button) v.findViewById(R.id.b14);
		Button b15 = (Button) v.findViewById(R.id.b15);
		Button back = (Button) v.findViewById(R.id.bBack);
		Button delete = (Button) v.findViewById(R.id.bDelete);
		b0.setOnClickListener(this);
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		b5.setOnClickListener(this);
		b6.setOnClickListener(this);
		b7.setOnClickListener(this);
		b8.setOnClickListener(this);
		b9.setOnClickListener(this);
		b10.setOnClickListener(this);
		b11.setOnClickListener(this);
		b12.setOnClickListener(this);
		b13.setOnClickListener(this);
		b14.setOnClickListener(this);
		b15.setOnClickListener(this);
		back.setOnClickListener(this);
		delete.setOnClickListener(this);
		b0.setOnClickListener(this);

		FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(mUid)
				.child("subjects")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						mSubjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Subject>>() {
						});
						if (mSubjects == null) mSubjects = new HashMap<>();
						setUpUI();
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});

		return v;
	}

	public void setUpUI() {
		if (mGrades.isEmpty()) {
			mPointsAverage.setText(getString(R.string.points_average, 0.0f));
			mGradeAverage.setText(getString(R.string.grade_average, 0.0f));
		} else {
			double pointsSum = 0.0d;
			for (Integer grade : mGrades) {
				pointsSum += (double) grade;
			}
			double pointsAverage = pointsSum / ((double) mGrades.size());
			mPointsAverage.setText(getString(R.string.points_average, pointsAverage));
			double gradeAverage = (17.0d - pointsAverage) / 3.0d;
			mGradeAverage.setText(getString(R.string.grade_average, gradeAverage));
		}
		mGradeList.setText(mGrades.toString());
		if (mSubjects.isEmpty()) {
			mSubjectList.setText(R.string.no_saved_subject);
			return;
		}
		mSubjectList.setText("");
		int i = 0;
		for (Map.Entry<String, Subject> subject : mSubjects.entrySet()) {
			if (i < mGrades.size()) {
				mSubjectList.append(subject.getValue().getName() + " (" + mGrades.get(i) + "), ");
			} else if (i < mSubjects.size() - 1) {
				mSubjectList.append(subject.getValue().getName() + ", ");
			} else {
				mSubjectList.append(subject.getValue().getName());
			}
			i++;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bBack:
				if (!mGrades.isEmpty()) {
					mGrades.pop();
					break;
				}
				break;
			case R.id.bDelete:
				mGrades.clear();
				break;
			default:
				Button b = (Button) v;
				mGrades.push(Integer.valueOf(b.getText().toString()));
		}
		setUpUI();
	}
}