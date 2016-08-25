package com.maxkrass.stundenplan.objects;

import android.content.res.ColorStateList;
import android.databinding.BindingConversion;

import java.io.Serializable;

/**
 * Max made this for Stundenplan2 on 26.07.2016.
 */
public class SubstitutionEvent implements Serializable {

	private Grade            grade;
	private String           period;
	private String           subject;
	private SubstitutionType type;
	private String           oldTeacher;
	private String           sub;
	private String           newLocation;

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

	@BindingConversion
	public static ColorStateList getSubstitutionTypeColor(SubstitutionType type) {
		String color;
		switch (type) {
			case Cancelled:
				color = "#FF4CAF50";
				break;
			case Substitution:
				color = "#FFCDDC39";
				break;
			case ClassChange:
				color = "#FFCDDC39";
				break;
			case LocationChange:
				color = "#FFCDDC39";
				break;
			case Special:
				color = "#FF3F51B5";
				break;
			default:
				return null;
		}
		return new ColorStateList(
				new int[][]{
						new int[]{}
				},
				new int[]{
						android.graphics.Color.parseColor(color)
				}
		);
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
		EF, Q1, Q2
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
