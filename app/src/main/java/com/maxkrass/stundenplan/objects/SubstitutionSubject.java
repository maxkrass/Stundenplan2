package com.maxkrass.stundenplan.objects;

/**
 * Created by Max Krass on 22.09.2016.
 */

public class SubstitutionSubject {

	private String grade, subject;

	public SubstitutionSubject() {
	}

	public SubstitutionSubject(String grade, String subject) {
		this.grade = grade;
		this.subject = subject;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
