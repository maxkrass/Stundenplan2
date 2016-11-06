package com.maxkrass.stundenplan.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.SubstitutionSubjectItemBinding;
import com.maxkrass.stundenplan.objects.SubstitutionSubject;

/**
 * Max Krass made this for Stundenplan2 on 22.09.2016.
 */

public class FirebaseSubstitutionSubjectAdapter extends FirebaseRecyclerAdapter<SubstitutionSubject, FirebaseSubstitutionSubjectAdapter.SubstitutionSubjectsViewHolder> {

	private static final String TAG = "FirebaseSubstitutionSub";
	private DatabaseReference mSubstitutionSubjectRef;

	public FirebaseSubstitutionSubjectAdapter(Class<SubstitutionSubject> modelClass, Class<SubstitutionSubjectsViewHolder> viewHolderClass, DatabaseReference ref) {
		super(modelClass, R.layout.substitution_subject_item, viewHolderClass, ref);
		mSubstitutionSubjectRef = ref;
	}

	public DatabaseReference getSubstitutionSubjectRef() {
		return mSubstitutionSubjectRef;
	}

	@Override
	protected SubstitutionSubject parseSnapshot(DataSnapshot snapshot) {
		Log.d(TAG, snapshot.getValue(String.class) + "=" + snapshot.getKey());
		return new SubstitutionSubject(snapshot.getValue(String.class), snapshot.getKey());
	}

	@Override
	protected void populateViewHolder(SubstitutionSubjectsViewHolder viewHolder, SubstitutionSubject model, int position) {
		viewHolder.mSubstitutionSubjectRef = mSubstitutionSubjectRef;
		viewHolder.mBinding.setSubstitutionSubject(model);
	}

	public static class SubstitutionSubjectsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		SubstitutionSubjectItemBinding mBinding;
		DatabaseReference              mSubstitutionSubjectRef;

		public SubstitutionSubjectsViewHolder(View itemView) {
			super(itemView);
			mBinding = SubstitutionSubjectItemBinding.bind(itemView);
			mBinding.substitutionSubjectsDelete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			mSubstitutionSubjectRef.child(mBinding.getSubstitutionSubject().getSubject()).removeValue();
		}
	}

}
