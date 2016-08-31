package com.maxkrass.stundenplan.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.SubstitutionGridItemBinding;
import com.maxkrass.stundenplan.objects.SubstitutionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Max made this for Stundenplan2 on 28.07.2016.
 */
public class SingleDaySubstitutionRecyclerViewAdapter extends RecyclerView.Adapter<SingleDaySubstitutionRecyclerViewAdapter.SubstitutionItemHolder> {

	private final Activity mActivity;

	private final ArrayList<SubstitutionEvent> mEvents;
	private final DatabaseReference            mSubstitutionSubjectsRef;
	private       Set<String>                  mSubstitutionSubjects;

	public SingleDaySubstitutionRecyclerViewAdapter(Activity activity, ArrayList<SubstitutionEvent> events, DatabaseReference substitutionSubjectsRef) {
		mActivity = activity;
		mEvents = events;
		mSubstitutionSubjectsRef = substitutionSubjectsRef;
		//mSubstitutionSubjectsRef.addValueEventListener(new ValueEventListener() {
		//	@Override
		//	public void onDataChange(DataSnapshot dataSnapshot) {
		//		HashMap<String, Boolean> hashMap = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Boolean>>() {
		//		});
		//		if (hashMap != null) mSubstitutionSubjects = hashMap.keySet();
		//		else mSubstitutionSubjects = new HashSet<>();
		//	}
//
		//	@Override
		//	public void onCancelled(DatabaseError databaseError) {
//
		//	}
		//});
	}

	@Override
	public SubstitutionItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final SubstitutionGridItemBinding binding = SubstitutionGridItemBinding.inflate(mActivity.getLayoutInflater());
		binding.addSubstitutionSubject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSubstitutionSubjects.add(binding.getSubstitutionEvent().getSubject());
				HashMap<String, Boolean> subjects = new HashMap<>();
				for (String s : mSubstitutionSubjects) {
					subjects.put(s, true);
				}
				mSubstitutionSubjectsRef.setValue(subjects);
			}
		});
		return new SubstitutionItemHolder(mActivity.getLayoutInflater().inflate(R.layout.substitution_grid_item, null));
	}

	@Override
	public void onBindViewHolder(SubstitutionItemHolder holder, int position) {
		holder.getBinding().setSubstitutionEvent(mEvents.get(position));
		holder.getBinding().setSubjects(mSubstitutionSubjects);
	}

	@Override
	public int getItemCount() {
		return mEvents.size();
	}

	class SubstitutionItemHolder extends RecyclerView.ViewHolder {

		private final SubstitutionGridItemBinding binding;

		public SubstitutionItemHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
		}

		public SubstitutionGridItemBinding getBinding() {
			return binding;
		}
	}
}
