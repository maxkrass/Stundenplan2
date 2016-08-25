package com.maxkrass.stundenplan.adapter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.maxkrass.stundenplan.activities.ViewSubjectActivity;
import com.maxkrass.stundenplan.databinding.SubjectViewBinding;
import com.maxkrass.stundenplan.fragments.ManageSubjectsFragment;
import com.maxkrass.stundenplan.objects.Subject;

import java.util.ArrayList;

/**
 * Max made this for Stundenplan2 on 19.07.2016.
 */
public class FirebaseSubjectAdapter extends FirebaseRecyclerAdapter<Subject, FirebaseSubjectAdapter.SubjectViewHolder> {

	private final ManageSubjectsFragment fragment;

	public FirebaseSubjectAdapter(Class<Subject> modelClass, Class<SubjectViewHolder> viewHolderClass, DatabaseReference ref, ManageSubjectsFragment fragment) {
		super(modelClass, com.maxkrass.stundenplan.R.layout.subject_view, viewHolderClass, ref);
		this.fragment = fragment;
	}

	@Override
	protected void populateViewHolder(SubjectViewHolder viewHolder, Subject model, int position) {
		viewHolder.fragment = fragment;
		viewHolder.binding.setSubject(model);
		((GradientDrawable) viewHolder.binding.subjectColor.getBackground())
				.setColor(viewHolder.binding.getSubject().getColorInt());
	}

	public static class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

		final SubjectViewBinding binding;
		ManageSubjectsFragment fragment;

		public SubjectViewHolder(View itemView) {
			super(itemView);
			binding = SubjectViewBinding.bind(itemView);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
		}

		@Override
		public boolean onLongClick(View view) {
			fragment.showLongClickDialog(binding.getSubject());
			return true;
		}

		@Override
		public void onClick(View view) {
			if (fragment.mSelect) {
				fragment.mOnSubjectChosenListener.onSubjectChosen(binding.getSubject());
			} else {
				Intent newActivity = new Intent(fragment.getActivity(), ViewSubjectActivity.class);
				newActivity.putExtra("subject", binding.getSubject());
				fragment.getActivity().getWindow().setExitTransition(new Fade().excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
				ArrayList<Pair<? extends View, String>> elements = new ArrayList<>();
				elements.add(new Pair<>(binding.subjectColor, "subject_color"));
				elements.add(new Pair<>(binding.subjectName, "subject_name"));
				Pair<View, String>[] viewStringPair = new Pair[elements.size()];
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity(), elements.toArray(viewStringPair));
				fragment.startActivity(newActivity, options.toBundle());
			}
		}
	}

}
