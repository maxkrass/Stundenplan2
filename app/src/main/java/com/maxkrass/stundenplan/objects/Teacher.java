package com.maxkrass.stundenplan.objects;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.maxkrass.stundenplan.BR;

import java.io.Serializable;
import java.util.HashMap;

public class Teacher implements Observable, Comparable<Teacher>, Serializable {
	private           String                   teacherName;

	private           String                   contraction;
	private           HashMap<String, Boolean> subjects;
	@Exclude
	private transient PropertyChangeRegistry   mCallbacks;


	public Teacher() {
		this("", "");
	}

	public Teacher(String name, String contraction) {
		this.teacherName = name;
		this.contraction = contraction;
	}

	public HashMap<String, Boolean> getSubjects() {
		return subjects;
	}

	public void setSubjects(HashMap<String, Boolean> subjects) {
		this.subjects = subjects;
	}

	public String getContraction() {
		return contraction;
	}

	public void setContraction(String contraction) {
		this.contraction = contraction;
	}

	@Bindable
	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String name) {
		this.teacherName = name;
		notifyPropertyChanged(BR.teacherName);
	}

	@Bindable
	public String getEmail() {
		return contraction.toLowerCase() + "@max-planck.com";
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Teacher
				&& ((Teacher) o).getTeacherName().equals(getTeacherName())
				&& ((Teacher) o).getContraction().equals(getContraction());
	}

	@Override
	public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
		if (mCallbacks == null) {
			mCallbacks = new PropertyChangeRegistry();
		}
		mCallbacks.add(callback);
	}

	@Override
	public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
		if (mCallbacks != null) {
			mCallbacks.remove(callback);
		}
	}

	private void notifyPropertyChanged(int fieldId) {
		if (mCallbacks != null) {
			mCallbacks.notifyCallbacks(this, fieldId, null);
		}
	}

	@Override
	public int compareTo(@NonNull Teacher another) {
		return teacherName.compareTo(another.getTeacherName());
	}
}
