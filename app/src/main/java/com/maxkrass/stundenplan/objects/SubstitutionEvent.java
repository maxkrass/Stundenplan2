package com.maxkrass.stundenplan.objects;

import android.content.res.ColorStateList;
import android.databinding.BindingConversion;

import com.google.firebase.database.Exclude;

import java.util.Objects;

import static com.maxkrass.stundenplan.objects.SubstitutionEvent.SubstitutionType.LocationChange;
import static com.maxkrass.stundenplan.objects.SubstitutionEvent.SubstitutionType.Special;
import static com.maxkrass.stundenplan.objects.SubstitutionEvent.SubstitutionType.Substitution;

/**
 * Max made this for Stundenplan2 on 30.08.2016.
 */
public class SubstitutionEvent {

	private Grade            grade;
	private String           period;
	private String           subject;
	private SubstitutionType type;
	private String           oldTeacher;
	private String           sub;
	private String           newLocation;
	private String           annotation;

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
		annotation = "";
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

	public String getDisplayString() {
		String s = period + " Std. ";
		if (Objects.equals(type, Special))
			s = s.concat(type.toString() + " " + sub + " " + newLocation);
		else s = s.concat(oldTeacher + " " + subject + " " + type);
		if (Objects.equals(type, LocationChange)) s = s.concat(" " + newLocation);
		else if (Objects.equals(type, Substitution)) s = s.concat(" " + sub);
		return s;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getGrade() {
		return grade == null ? null : grade.name();
	}

	public void setGrade(String grade) {
		this.grade = grade == null ? null : Grade.valueOf(grade);
	}

	@Exclude
	public Grade getGradeVal() {
		return grade;
	}

	@Exclude
	public void setGradeVal(Grade grade) {
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

	public String getType() {
		return type == null ? "" : type.toString();
	}

	@Exclude
	public void setType(SubstitutionType type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = SubstitutionType.getTypeByString(type);
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
		EF, Q1, Q2, LR, SL;

		public static Grade getGradeByString(String s) {
			switch (s) {
				case "EF":
					return EF;
				case "Q1":
					return Q1;
				case "Q2":
					return Q2;
				case "LR":
					return LR;
				case "SL":
					return SL;
				default:
					return null;
			}
		}
	}

	public enum SubstitutionType {
		Cancelled("fällt aus"),
		Substitution("Vertr."),
		ClassChange("Unter.-Änd."),
		LocationChange("Raum-Änd."),
		Special("Sond"),
		Release("Freisetzung");

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
				case "Freisetzung":
					return Release;
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
