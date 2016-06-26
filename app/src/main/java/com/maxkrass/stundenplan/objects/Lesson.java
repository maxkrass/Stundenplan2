package com.maxkrass.stundenplan.objects;

import android.support.annotation.NonNull;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Lesson extends SugarRecord implements Comparable<Lesson> {
	private Subject subject;
	private Weekday weekday;
	private String location;
	private boolean doublePeriod;
	private Period period;

	@Ignore
	private boolean showRoomLabel = true;

	public Lesson() {
	}

	public boolean isShowRoomLabel() {
		return showRoomLabel;
	}

	public void setShowRoomLabel(boolean showRoomLabel) {
		this.showRoomLabel = showRoomLabel;
	}

	public boolean isDoublePeriod() {
		return doublePeriod;
	}

	public void setDoublePeriod(boolean doublePeriod) {
		this.doublePeriod = doublePeriod;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Weekday getWeekday() {
		return weekday;
	}

	public void setWeekday(Weekday weekday) {
		this.weekday = weekday;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public int compareTo(@NonNull Lesson l) {

		if (this.weekday.equals(l.getWeekday())) {
			return (int) Math.signum(period.getId() - l.getPeriod().getId());
		} else {
			return this.weekday.compareTo(l.weekday);
		}

	}
}
