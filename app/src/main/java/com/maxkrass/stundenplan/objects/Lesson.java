package com.maxkrass.stundenplan.objects;

import android.support.annotation.NonNull;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Locale;

public class Lesson extends SugarRecord implements Comparable<Lesson> {
	private Subject subject;
	private Weekday weekday;
	private String location;
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

	public boolean hasSucceedingLesson() {
		return Select.from(Lesson.class).where(Condition.prop("subject").eq(subject)).and(Condition.prop("location").eq(location)).and(Condition.prop("weekday").eq(weekday)).and(Condition.prop("period").eq(period.getId() + 1)).first() != null;
	}

	public boolean isSucceedingLesson() {
		return Select.from(Lesson.class).where(Condition.prop("subject").eq(subject)).and(Condition.prop("location").eq(location)).and(Condition.prop("weekday").eq(weekday)).and(Condition.prop("period").eq(period.getId() - 1)).first() != null;
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

	@Override
	public String toString() {
		return String.format(Locale.GERMAN, "[ %s, %s, %s, %d ]", subject.getName(), weekday.name(), location, period.getId());
	}
}
