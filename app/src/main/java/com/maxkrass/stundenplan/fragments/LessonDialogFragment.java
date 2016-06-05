package com.maxkrass.stundenplan.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.objects.Lesson;
import com.orm.SugarRecord;

/**
 * Max made this for Stundenplan on 30.01.2016.
 */
public class LessonDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Lesson lesson = SugarRecord.findById(Lesson.class, getArguments().getLong("lessonID"));
		String weekday = lesson.getWeekday().toString();
		builder.setTitle(weekday.charAt(0) + weekday.substring(1).toLowerCase());
		builder.setItems(R.array.dialog_options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		return builder.create();
	}
}
