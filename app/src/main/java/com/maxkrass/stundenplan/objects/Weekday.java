package com.maxkrass.stundenplan.objects;

public enum Weekday {
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
