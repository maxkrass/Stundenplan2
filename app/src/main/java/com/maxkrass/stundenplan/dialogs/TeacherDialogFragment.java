package com.maxkrass.stundenplan.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.CreateTeacherActivity;
import com.maxkrass.stundenplan.adapter.TeachersAdapter;
import com.maxkrass.stundenplan.objects.Subject;
import com.maxkrass.stundenplan.objects.Teacher;
import com.maxkrass.stundenplan.tools.Tools;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Max made this for Stundenplan2 on 11.07.2016.
 */
public class TeacherDialogFragment extends DialogFragment {

	Teacher teacher;
	RecyclerView recyclerView;

	public TeacherDialogFragment() {
	}

	public static TeacherDialogFragment newInstance(RecyclerView recyclerView) {

		TeacherDialogFragment teacherDialogFragment = new TeacherDialogFragment();

		teacherDialogFragment.recyclerView = recyclerView;

		return teacherDialogFragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		teacher = SugarRecord.findById(Teacher.class, getArguments().getLong("teacherID"));
		if (teacher != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(teacher.getTeacherName());
			builder.setItems(R.array.dialog_options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							TeacherDialogFragment.this.dismiss();
							Intent intent = new Intent(getActivity(), CreateTeacherActivity.class);
							intent.putExtra("teacherID", teacher.getId());
							startActivity(intent);
							break;
						case 1:
							final int position = Tools.getTeacherPosition(teacher);
							Snackbar.make(recyclerView, teacher.getTeacherName() + " deleted", Snackbar.LENGTH_LONG)
									.setCallback(new Snackbar.Callback() {
										@Override
										public void onDismissed(Snackbar snackbar, int event) {
											switch (event) {
												case DISMISS_EVENT_ACTION:
												case DISMISS_EVENT_MANUAL:
													break;
												case DISMISS_EVENT_CONSECUTIVE:
												case DISMISS_EVENT_SWIPE:
												case DISMISS_EVENT_TIMEOUT:
													List<Subject> subjects = SugarRecord.find(Subject.class, "teacher = ?", teacher.getId().toString());
													for (Subject s : subjects) {
														Teacher blankTeacher = new Teacher();
														blankTeacher.setId(0L);
														s.setTeacher(blankTeacher);
														SugarRecord.save(s);
													}
													SugarRecord.delete(teacher);
													break;
											}
										}
									})
									.setAction("restore", new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											((TeachersAdapter) recyclerView.getAdapter()).add(position, teacher);
											recyclerView.getAdapter().notifyItemInserted(position);
										}
									}).show();

							TeacherDialogFragment.this.dismiss();
							((TeachersAdapter) recyclerView.getAdapter()).remove(position);
							recyclerView.getAdapter().notifyItemRemoved(position);
							break;
					}
				}
			});
			return builder.create();
		} else {
			this.dismiss();
			return null;
		}
	}
}
