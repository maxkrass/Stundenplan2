package com.maxkrass.stundenplan.contracts;

import com.maxkrass.stundenplan.objects.Lesson;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Weekday;
import com.maxkrass.stundenplan.presenters.BasePresenter;
import com.maxkrass.stundenplan.views.BaseView;

/**
 * Max made this for Stundenplan2 on 22.07.2016.
 */
public class CreateLessonContract {

	public interface View extends BaseView<Presenter> {
		void showLesson(Lesson lesson, Boolean doublePeriod);

		void showError(String error);

		void exitCreateDialog();

		void showSubject(Subject subject);
	}

	public interface Presenter extends BasePresenter {
		void validateLesson(String subject, Integer period, String location, Weekday weekday, boolean doublePeriod);

		void loadSubject(String subjectName);
	}

}
