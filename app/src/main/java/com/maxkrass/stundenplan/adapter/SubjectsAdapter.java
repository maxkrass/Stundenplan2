package com.maxkrass.stundenplan.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.ViewSubjectActivity;
import com.maxkrass.stundenplan.databinding.SubjectViewBinding;
import com.maxkrass.stundenplan.fragments.ManageSubjectsFragment;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Subject;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

	private List<Subject> subjectList;
	private ManageSubjectsFragment fragment;

	public SubjectsAdapter(ManageSubjectsFragment activity) {
		subjectList = SugarRecord.listAll(Subject.class, "name");
		this.fragment = activity;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(SubjectViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());

	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.binding.setSubject(subjectList.get(position));
		((GradientDrawable) holder.binding.subjectColor.getBackground()).setColor(Color.values()[holder.binding.getSubject().getColorIndex()].getColor(fragment.getActivity()));

	}

	@Override
	public int getItemCount() {
		return subjectList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		SubjectViewBinding binding;

		ViewHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Intent activityIntent = fragment.getActivity().getIntent();
			if (activityIntent.getBooleanExtra("select", false)) {
				Intent result = new Intent();
				result.putExtra("subjectID", getAdapterPosition());
				fragment.getActivity().setResult(Activity.RESULT_OK, result);
				fragment.getActivity().finish();
			} else {
				Intent newActivity = new Intent(fragment.getActivity(), ViewSubjectActivity.class);
				newActivity.putExtra("subjectID", getAdapterPosition());
				fragment.getActivity().getWindow().setExitTransition(new Fade().excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
				ArrayList<Pair<View, String>> elements = new ArrayList<>();
				elements.add(new Pair<>(v.findViewById(R.id.subject_color), "subject_color"));
				elements.add(new Pair<>(v.findViewById(R.id.subject_name), "subject_name"));
				Pair<View, String>[] viewStringPair = new Pair[elements.size()];
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity(), elements.toArray(viewStringPair));
				fragment.startActivity(newActivity, options.toBundle());
			}
		}

		@Override
		public boolean onLongClick(View v) {
			return false;
		}
	}

}
