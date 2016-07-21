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
import java.util.HashMap;

public class Subject implements Observable, Serializable {
	private HashMap<String, Boolean> teacher;
	private String color;
	private Long id;
	private String name;
	private String abbreviation;
	@Exclude
	private transient PropertyChangeRegistry mCallbacks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Bindable
	public HashMap<String, Boolean> getTeacher() {
		return teacher;
	}

	public void setTeacher(HashMap<String, Boolean> teacher) {
		this.teacher = teacher;
		notifyPropertyChanged(BR.teacher);
	}

	@Bindable
	public String getColor() {
		return color;
	}

	@Exclude
	public int getColorInt() {
		if (color != null && !color.isEmpty()) return Long.decode(color).intValue();
		return 0;
	}

	public void setColor(String color) {
		if (!color.startsWith("#")) color = "#" + color;
		this.color = color;
		notifyPropertyChanged(BR.color);
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

	public Subject(String name, String abbreviation, HashMap<String, Boolean> teacher, String color) {
		this.teacher = teacher;
		this.color = color;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public Subject() {
		teacher = new HashMap<>(1);
		color = "";
		name = "";
		abbreviation = "";
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

	@BindingConversion
	public static ColorDrawable convertStringToDrawable(String color) {
		return new ColorDrawable(Long.decode(color).intValue());
	}

	//@BindingConversion
	//public static int convertStringToInt(String color) {
	//	if (color != null && !color.isEmpty()) return Long.decode(color).intValue();
	//	return 0;
	//}

	@BindingConversion
	public static ColorStateList convertStringToColorStateList(String color) {
		return new ColorStateList(
				new int[][] {
						new int[]{}
				},
				new int[] {
						Long.decode(color).intValue()
				}
		);
	}
}
