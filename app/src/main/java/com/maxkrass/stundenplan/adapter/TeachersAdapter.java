package com.maxkrass.stundenplan.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.ManageTeacherActivity;
import com.maxkrass.stundenplan.activities.ViewTeacherActivity;
import com.maxkrass.stundenplan.databinding.TeacherViewBinding;
import com.maxkrass.stundenplan.objects.Teacher;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.TeacherViewHolder> {

	private ManageTeacherActivity activity;
	private List<Teacher> teacherList;
	private boolean select;

	public TeachersAdapter(ManageTeacherActivity activity, boolean select) {
		teacherList = SugarRecord.listAll(Teacher.class, "name");
		this.activity = activity;
		this.select = select;
	}

	@Override
	public TeacherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final TeacherViewHolder holder = new TeacherViewHolder((TeacherViewBinding) DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.teacher_view, parent, false));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int position = holder.getAdapterPosition();
				if (position == RecyclerView.NO_POSITION) return;
				if (select) {
					Intent resultIntent = new Intent();
					resultIntent.putExtra("position", position);
					activity.setResult(Activity.RESULT_OK, resultIntent);
					activity.finish();

				} else {
					final TeacherViewBinding binding = holder.getBinding();
					Intent intent = new Intent(activity, ViewTeacherActivity.class);
					intent.putExtra("teacherID", binding.getTeacher().getId());
					intent.putExtra("fontSize", binding.teacherName.getTextSize());
					intent.putExtra("padding",
							new Rect(binding.teacherName.getPaddingLeft(),
									binding.teacherName.getPaddingTop(),
									binding.teacherName.getPaddingRight(),
									binding.teacherName.getPaddingBottom()));
					intent.putExtra("color",
							binding.teacherName.getCurrentTextColor());
					ArrayList<Pair<View, String>> elements = new ArrayList<>();
					elements.add(new Pair<>(v.findViewById(R.id.teacher_name), "teacher_name"));
					elements.add(new Pair<>(activity.findViewById(R.id.manage_teacher_appbar), "teacher_appbar"));
					if(v.findViewById(R.id.call_teacher).getVisibility() != View.GONE) {
						elements.add(new Pair<>(v.findViewById(R.id.call_teacher), "teacher_phone_icon"));
					}
					if(v.findViewById(R.id.email_teacher).getVisibility() != View.GONE) {
						elements.add(new Pair<>(v.findViewById(R.id.email_teacher), "teacher_email_icon"));
					}
					Pair<View, String>[] viewStringPair = new Pair[elements.size()];
					View decorView = activity.getWindow().getDecorView();
					View statusBackground = decorView.findViewById(android.R.id.statusBarBackground);
					View navBackground = decorView.findViewById(android.R.id.navigationBarBackground);
					Pair<View, String> statusPair = Pair.create(statusBackground,
							statusBackground.getTransitionName());
					Pair<View, String> navPair = Pair.create(navBackground, navBackground.getTransitionName());
					elements.add(statusPair);
					elements.add(navPair);
					ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, elements.toArray(viewStringPair));
					activity.startActivity(intent, options.toBundle());
				}

			}
		});
		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ManageTeacherActivity.TeacherDialog teacherDialog = new ManageTeacherActivity.TeacherDialog();
				Bundle bundle = new Bundle();
				bundle.putLong("teacherID", holder.getBinding().getTeacher().getId());
				teacherDialog.setArguments(bundle);
				teacherDialog.show(activity.getFragmentManager(), "teacher");
				return true;
			}
		});
		return holder;
	}

	@Override
	public void onBindViewHolder(TeacherViewHolder holder, int position) {
		holder.binding.setTeacher(teacherList.get(position));
	}

	public void updateData() {
		teacherList = SugarRecord.listAll(Teacher.class, "name");
	}

	public Teacher remove(int position) {
		return teacherList.remove(position);
	}

	public void add(int position, Teacher teacher) {
		teacherList.add(position, teacher);
	}

	public void add(Teacher teacher) {
		teacherList.add(teacher);
	}

	@Override
	public int getItemCount() {
		return teacherList.size();
	}

	public class TeacherViewHolder extends RecyclerView.ViewHolder {

		public TeacherViewBinding getBinding() {
			return binding;
		}

		private final TeacherViewBinding binding;

		public TeacherViewHolder(TeacherViewBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}

}
