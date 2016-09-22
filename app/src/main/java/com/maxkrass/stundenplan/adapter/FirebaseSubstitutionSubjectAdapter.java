package com.maxkrass.stundenplan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.maxkrass.stundenplan.objects.SubstitutionSubject;

/**
 * Created by Max Krass on 22.09.2016.
 */

public class FirebaseSubstitutionSubjectAdapter extends FirebaseRecyclerAdapter<SubstitutionSubject, MyViewHolder> {

	@Override
	protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {

	}

	@Override
	protected SubstitutionSubject parseSnapshot(DataSnapshot snapshot) {
		return super.parseSnapshot(snapshot);
	}

	class MyViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

}
