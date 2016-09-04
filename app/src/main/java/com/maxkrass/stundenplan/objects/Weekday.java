package com.maxkrass.stundenplan.objects;

public enum Weekday {
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY;

	public static Weekday fromString(String s) {
		for (Weekday weekday :
				values()) {
			if (java.util.Objects.equals(s, weekday.name().toLowerCase())) return weekday;
		}
		return MONDAY;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
