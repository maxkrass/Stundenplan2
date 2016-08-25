package com.maxkrass.stundenplan.objects;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Locale;

public class Lesson implements Comparable<Lesson> {
	private Subject subject;
	private String  weekday;
	private String  location;
	private int     period;

	@Exclude
	private transient boolean showRoomLabel = true;

	public Lesson() {
	}

	public Lesson(Subject subject, int period, String weekday, String location) {
		this.subject = subject;
		this.period = period;
		this.weekday = weekday;
		this.location = location;
	}

	public boolean isShowRoomLabel() {
		return showRoomLabel;
	}

	public void setShowRoomLabel(boolean showRoomLabel) {
		this.showRoomLabel = showRoomLabel;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public int compareTo(@NonNull Lesson l) {

		if (this.weekday.equals(l.getWeekday())) {
			return (int) Math.signum(period - l.getPeriod());
		} else {
			return this.weekday.compareTo(l.weekday);
		}

	}

	@Override
	public String toString() {
		return String.format(Locale.GERMAN, "[ %s, %s, %s, %d ]", subject, weekday, location, period);
	}
}
