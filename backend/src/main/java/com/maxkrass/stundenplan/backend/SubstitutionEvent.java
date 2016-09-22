package com.maxkrass.stundenplan.backend;

import java.util.Objects;

/**
 * Max made this for Stundenplan2 on 17.09.2016.
 */
public class SubstitutionEvent {

	private Grade            grade;
	private String           period;
	private String           subject;
	private SubstitutionType type;
	private String           oldTeacher;
	private String           sub;
	private String           newLocation;

	private String annotation;

	public SubstitutionEvent(Grade grade, String period, String subject, SubstitutionType type, String oldTeacher, String sub, String newLocation) {
		this.grade = grade;
		this.period = period;
		this.subject = subject;
		this.type = type;
		this.oldTeacher = oldTeacher;
		this.sub = sub;
		this.newLocation = newLocation;
	}

	public SubstitutionEvent() {
		period = "";
		subject = "";
		oldTeacher = "";
		sub = "";
		newLocation = "";
	}

	public String getDisplayString() {
		String s = period + " Std. ";
		if (Objects.equals(type, SubstitutionType.Special)) s = s.concat(type.toString());
		else s = s.concat(oldTeacher + " " + subject + " " + type);
		if (Objects.equals(type, SubstitutionType.LocationChange)) s = s.concat(" " + newLocation);
		else if (Objects.equals(type, SubstitutionType.Substitution)) s = s.concat(" " + sub);
		return s;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public SubstitutionType getType() {
		return type;
	}

	public void setType(SubstitutionType type) {
		this.type = type;
	}

	public String getOldTeacher() {
		return oldTeacher;
	}

	public void setOldTeacher(String oldTeacher) {
		this.oldTeacher = oldTeacher;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getNewLocation() {
		return newLocation;
	}

	public void setNewLocation(String newLocation) {
		this.newLocation = newLocation;
	}

	public enum Grade {
		EF, Q1, Q2, LR
	}

	public enum SubstitutionType {
		Cancelled("fällt aus"),
		Substitution("Vertr."),
		ClassChange("Unter.-Änd."),
		LocationChange("Raum-Änd."),
		Special("Sond");

		final String mType;

		SubstitutionType(String s) {
			mType = s;
		}

		public static SubstitutionType getTypeByString(String s) {
			switch (s) {
				case "fällt aus":
					return Cancelled;
				case "Vertr.":
					return Substitution;
				case "Unter.-Änd.":
					return ClassChange;
				case "Raum-Änd.":
					return LocationChange;
				case "Sond":
					return Special;
				default:
					return null;
			}
		}

		@Override
		public String toString() {
			return mType;
		}


	}
}
