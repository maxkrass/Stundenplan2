package com.maxkrass.stundenplan.objects;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.maxkrass.stundenplan.BR;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Max made this for Stundenplan on 16.04.2016.
 */
public class Period extends SugarRecord implements Observable {
	int startHour;
	int startMinute;
	int endHour;
	int endMinute;
	@Ignore
	transient PropertyChangeRegistry mCallbacks;

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

	public int getPeriodIndex() {
		return (int) (getId() - 1);
	}

	public Period() {
	}

	public Period(int startHour, int startMinute, int endHour, int endMinute) {
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

	public synchronized void notifyChange() {
		if (mCallbacks != null) {
			mCallbacks.notifyCallbacks(this, 0, null);
		}
	}

	public void notifyPropertyChanged(int fieldId) {
		if (mCallbacks != null) {
			mCallbacks.notifyCallbacks(this, fieldId, null);
		}
	}
}
