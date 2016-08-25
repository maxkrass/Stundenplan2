package com.maxkrass.stundenplan.objects;

import android.content.res.ColorStateList;
import android.databinding.Bindable;
import android.databinding.BindingConversion;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.graphics.drawable.ColorDrawable;

import com.google.firebase.database.Exclude;
import com.maxkrass.stundenplan.BR;

import java.io.Serializable;

public class Subject implements Observable, Serializable {
	private           String                 teacher;
	private           String                 color;
	private           String                 name;
	private           String                 abbreviation;
	@Exclude
	private transient PropertyChangeRegistry mCallbacks;

	public Subject(String name, String abbreviation, String teacher, String color) {
		this.teacher = teacher;
		this.color = color;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public Subject() {
		teacher = "";
		color = "";
		name = "";
		abbreviation = "";
	}

	@BindingConversion
	public static ColorDrawable convertStringToDrawable(String color) {
		return new ColorDrawable(android.graphics.Color.parseColor(color));
	}

	@BindingConversion
	public static ColorStateList convertStringToColorStateList(String color) {
		return new ColorStateList(
				new int[][]{
						new int[]{}
				},
				new int[]{
						android.graphics.Color.parseColor(color)
				}
		);
	}

	@Bindable
	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
		notifyPropertyChanged(BR.teacher);
	}

	@Bindable
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		if (!color.startsWith("#")) color = "#" + color;
		this.color = color;
		notifyPropertyChanged(BR.color);
	}

	@Exclude
	public int getColorInt() {
		if (color != null && !color.isEmpty()) return android.graphics.Color.parseColor(color);
		return android.graphics.Color.parseColor("#00000000");
	}

	@Bindable
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		notifyPropertyChanged(BR.name);
	}

	@Bindable
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
		notifyPropertyChanged(BR.abbreviation);
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

	//@BindingConversion
	//public static int convertStringToInt(String color) {
	//	if (color != null && !color.isEmpty()) return Long.decode(color).intValue();
	//	return 0;
	//}

	private void notifyPropertyChanged(int fieldId) {
		if (mCallbacks != null) {
			mCallbacks.notifyCallbacks(this, fieldId, null);
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Subject &&
				getName().equals(((Subject) o).getName()) &&
				getTeacher().equals(((Subject) o).getTeacher()) &&
				getColor().equals(((Subject) o).getColor());
	}
}
