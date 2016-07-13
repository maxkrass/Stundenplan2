package com.maxkrass.stundenplan.objects;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.maxkrass.stundenplan.BR;
import com.orm.dsl.Table;

@Table
public class Subject extends BaseObservable {
	private Teacher teacher;
	private int colorIndex;
	private Long id;
	private String name;
	private String abbreviation;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Bindable
	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
		notifyPropertyChanged(BR.teacher);
	}

	@Bindable
	public int getColorIndex() {
		return colorIndex;
	}

	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
		notifyPropertyChanged(BR.colorIndex);
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

	public Subject(String name, String abbreviation, Teacher teacher, int colorIndex) {
		this.teacher = teacher;
		this.colorIndex = colorIndex;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public Subject() {
		teacher = null;
		colorIndex = 0;
		name = "";
		abbreviation = "";
	}
}
