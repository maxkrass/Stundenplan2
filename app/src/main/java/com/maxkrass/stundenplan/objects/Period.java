package com.maxkrass.stundenplan.objects;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.google.firebase.database.Exclude;
import com.maxkrass.stundenplan.BR;

/**
 * Max made this for Stundenplan on 16.04.2016.
 */
public class Period implements Observable {
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private int index;

	@Exclude
	private transient PropertyChangeRegistry mCallbacks;

	public Period() {
	}

	public Period(int index, int startHour, int startMinute, int endHour, int endMinute) {
		this.index = index;
		this.startHour = startHour;
		this.startMinute = startMinute;
		if (startHour <= endHour && startMinute <= endMinute) {
			this.endHour = endHour;
			this.endMinute = endMinute;
		} else {
			this.endMinute = (startMinute + 45) % 60;
			this.endHour = startHour + ((startMinute + 45) / 60);
		}
	}

	public int getIndex() {
		return index;
	}

	@Bindable
	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
		notifyPropertyChanged(BR.endMinute);
	}

	@Bindable
	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
		notifyPropertyChanged(BR.startHour);
	}

	@Bindable
	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
		notifyPropertyChanged(BR.startMinute);
	}

	@Bindable
	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
		notifyPropertyChanged(BR.endHour);
	}

	@Override
	public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
		if (mCallbacks == null) {
			mCallbacks = new PropertyChangeRegistry();
		}
		mCallbacks.add(callback);
	}

	@Override
	public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
		if (mCallbacks != null) {
			mCallbacks.remove(callback);
		}
	}

	private void notifyPropertyChanged(int fieldId) {
		if (mCallbacks != null) {
			mCallbacks.notifyCallbacks(this, fieldId, null);
		}
	}
}
