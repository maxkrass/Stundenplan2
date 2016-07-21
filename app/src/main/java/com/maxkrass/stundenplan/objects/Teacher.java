package com.maxkrass.stundenplan.objects;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.maxkrass.stundenplan.BR;

import java.io.Serializable;

public class Teacher implements Observable, Comparable<Teacher>, Serializable {
	private String teacherName;
	private String phone;
	private String email;
	@Exclude
	private transient PropertyChangeRegistry mCallbacks;

	public Teacher() {
		this("", "", "");
	}

	public Teacher(String name, String phone, String email) {
		this.teacherName = name;
		this.phone = phone;
		this.email = email;
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
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
		notifyPropertyChanged(BR.phone);
	}

	@Bindable
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
		notifyPropertyChanged(BR.email);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Teacher
				&& ((Teacher) o).getTeacherName().equals(getTeacherName())
				&& ((Teacher) o).getEmail().equals(getEmail())
				&& ((Teacher) o).getPhone().equals(getPhone());
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

	public void notifyPropertyChanged(int fieldId) {
		if (mCallbacks != null) {
			mCallbacks.notifyCallbacks(this, fieldId, null);
		}
	}

	@Override
	public int compareTo(@NonNull Teacher another) {
		return teacherName.compareTo(another.getTeacherName());
	}
}
