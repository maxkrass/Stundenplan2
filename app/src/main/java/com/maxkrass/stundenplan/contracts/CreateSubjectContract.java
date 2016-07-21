package com.maxkrass.stundenplan.contracts;

import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.presenters.BasePresenter;
import com.maxkrass.stundenplan.views.BaseView;

import java.util.HashMap;

/**
 * Max made this for Stundenplan2 on 20.07.2016.
 */
public class CreateSubjectContract {

	public interface View extends BaseView<Presenter> {

		void nameInvalid();

		void subjectAlreadyExists();

		void abbreviationInvalid();

		void removeErrors();

		void savingFailed();

		void exitCreateDialog();

		void showSubject(Subject subject);
	}

	public interface Presenter extends BasePresenter {
		void validateSubject(String name, String abbreviation, String color, HashMap<String, Boolean> teacher);
	}

}
