package com.maxkrass.stundenplan.contracts;

import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.presenters.BasePresenter;
import com.maxkrass.stundenplan.views.BaseView;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class CreateTeacherContract {

	public interface View extends BaseView<Presenter> {

		void exitCreateDialog();

		void nameInvalid();

		void nameExists();

		void emailInvalid();

		void removeErrors();

		void showTeacher(Teacher teacher);

		void savingFailed();
	}

	public interface Presenter extends BasePresenter {

		void validateTeacher(String name, String email);

	}

}
