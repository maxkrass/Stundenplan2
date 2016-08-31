package com.maxkrass.stundenplan.adapter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.ViewTeacherActivity;
import com.maxkrass.stundenplan.databinding.TeacherViewBinding;
import com.maxkrass.stundenplan.fragments.ManageTeachersFragment;
import com.maxkrass.stundenplan.objects.Teacher;

import java.util.ArrayList;

/**
 * Max made this for Stundenplan2 on 19.07.2016.
 */
public class FirebaseTeacherAdapter extends FirebaseRecyclerAdapter<Teacher, FirebaseTeacherAdapter.TeacherViewHolder> {

	private final ManageTeachersFragment fragment;

	public FirebaseTeacherAdapter(Class<Teacher> modelClass, Class<TeacherViewHolder> viewHolderClass, DatabaseReference ref, ManageTeachersFragment fragment) {
		super(modelClass, R.layout.teacher_view, viewHolderClass, ref);
		this.fragment = fragment;
	}

	@Override
	protected Teacher parseSnapshot(DataSnapshot snapshot) {
		return new Teacher(snapshot.getKey(), snapshot.getKey());
	}

	@Override
	protected void populateViewHolder(TeacherViewHolder viewHolder, Teacher model, int position) {
		viewHolder.binding.setTeacher(model);
		viewHolder.fragment = this.fragment;
	}

	public static class TeacherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		public final TeacherViewBinding     binding;
		private      ManageTeachersFragment fragment;

		public TeacherViewHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);

		}
		@Override
		public boolean onLongClick(View v) {
			fragment.showLongClickDialog(binding.getTeacher());
			return true;
		}

		@Override
		public void onClick(View view) {
			if (!fragment.mSelect) {
				Intent intent = new Intent(fragment.getActivity(), ViewTeacherActivity.class);
				intent.putExtra("teacher", binding.getTeacher());
				intent.putExtra("fontSize", binding.teacherName.getTextSize());
				intent.putExtra("padding",
						new Rect(binding.teacherName.getPaddingLeft(),
								binding.teacherName.getPaddingTop(),
								binding.teacherName.getPaddingRight(),
								binding.teacherName.getPaddingBottom()));
				intent.putExtra("color",
						binding.teacherName.getCurrentTextColor());
				ArrayList<Pair> elements = new ArrayList<>();
				elements.add(new Pair<>(binding.teacherName, "teacher_name"));
				elements.add(new Pair<>(fragment.getActivity().findViewById(R.id.main_app_bar_layout), fragment.getString(R.string.main_app_bar_layout_transition_name)));
				if (binding.emailTeacher.getVisibility() != View.GONE) {
					elements.add(new Pair<>(binding.emailTeacher, "teacher_email_icon"));
				}
				Pair[] viewStringPair = new Pair[elements.size()];
				View decorView = fragment.getActivity().getWindow().getDecorView();
				View statusBackground = decorView.findViewById(android.R.id.statusBarBackground);
				//View navBackground = decorView.findViewById(android.R.id.navigationBarBackground);
				Pair<View, String> statusPair = Pair.create(statusBackground,
						statusBackground.getTransitionName());
				//Pair<View, String> navPair = Pair.create(navBackground, navBackground.getTransitionName());
				elements.add(statusPair);
				//elements.add(navPair);
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity(), elements.toArray(viewStringPair));
				fragment.startActivity(intent, options.toBundle());
			} else {
				fragment.mOnTeacherChosenListener.onTeacherChosen(binding.getTeacher());
			}
		}
	}

}
