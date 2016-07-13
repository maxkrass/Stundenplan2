package com.maxkrass.stundenplan.objects;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.maxkrass.stundenplan.BR;
import com.orm.dsl.Table;

@Table
public class Teacher extends BaseObservable {
	private String teacherName;
	private String phone;
	private String email;

	public void setId(Long id) {
		this.id = id;
	}

	private Long id;

	public Long getId() {
		return id;
	}

	public Teacher() {
		teacherName = "";
		phone = "";
		email = "";
	}

	@Bindable
	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
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

	public Teacher(String name, String phone, String email) {
		this.teacherName = name;
		this.phone = phone;
		this.email = email;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Teacher && ((Teacher) o).getTeacherName().equals(getTeacherName()) && ((Teacher) o).getEmail().equals(getEmail()) && ((Teacher) o).getPhone().equals(getPhone()) && ((Teacher) o).getId().equals(getId());
	}
}
