package com.maxkrass.stundenplan.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.RecyclerRowBinding;
import com.maxkrass.stundenplan.fragments.SettingsFragment;
import com.maxkrass.stundenplan.objects.SubstitutionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SingleDaySubRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
	private static final int VIEW_TYPE_HEADER = 111, VIEW_TYPE_ITEM = 222;
	private ArrayList<ItemGroup>            mItemGroups;
	private OnLoadingFinishedListener       mListener;
	private Context                         mContext;
	private OnSubstitutionItemClickListener mClickListener;

	public SingleDaySubRecyclerAdapter(Context context, OnLoadingFinishedListener listener, OnSubstitutionItemClickListener clickListener) {
		mContext = context;
		mItemGroups = new ArrayList<>();
		mListener = listener;
		mClickListener = clickListener;
	}

	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEW_TYPE_HEADER) {
			return new RecyclerHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false));
		}
		if (viewType == VIEW_TYPE_ITEM) {
			RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
			return new RecyclerItemViewHolder(binding.getRoot());
		}
		return null;
	}

	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		int size = 0;
		for (ItemGroup g : mItemGroups) {
			if (position == size) {
				bindHeader((RecyclerHeaderViewHolder) viewHolder, g);
				return;
			}
			size += g.getGroupSize();
			if (position < size) {
				bindItem((RecyclerItemViewHolder) viewHolder, g.items.get((g.items.size()) - (size - position)));
				return;
			}
		}
	}

	public int getItemViewType(int position) {
		if (this.mItemGroups != null && position >= 0) {
			int size = 0;
			for (ItemGroup group : this.mItemGroups) {
				if (position == size) {
					return VIEW_TYPE_HEADER;
				}
				size += group.getGroupSize();
				if (position < size) {
					return VIEW_TYPE_ITEM;
				}
			}
		}
		return 0;
	}

	public int getItemCount() {
		if (mItemGroups == null || mItemGroups.isEmpty()) {
			return 0;
		}
		int size = 0;
		for (ItemGroup itemGroup : mItemGroups) {
			size += itemGroup.getGroupSize();
		}
		return size;
	}

	private void bindItem(RecyclerItemViewHolder viewHolder, SubstitutionEvent event) {
		viewHolder.binding.setEvent(event);
	}

	private void bindHeader(RecyclerHeaderViewHolder viewHolder, ItemGroup g) {
		String headerText = g.headerText;
		if (g.items.size() > 0) headerText += " (" + g.items.size() + ")";
		viewHolder.header.setText(headerText);
	}

	public void setNewContent(final ArrayList<SubstitutionEvent> list) {
		//Map<String, Boolean> settings = MainActivity.settingsMap;
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		this.mItemGroups.clear();
		if (list != null) {
			FirebaseDatabase
					.getInstance()
					.getReference()
					.child("users")
					.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
					.child("substitutionSubjects")
					.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							HashMap<String, String> mySavedSubjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, String>>() {
							});
							ItemGroup mySubjects = new ItemGroup("Meine Fächer");
							ItemGroup efSubs = new ItemGroup("EF");
							ItemGroup q1Subs = new ItemGroup("Q1");
							ItemGroup q2Subs = new ItemGroup("Q2");
							for (SubstitutionEvent event : list) {
								if (mySavedSubjects != null && !mySavedSubjects.isEmpty() && (mySavedSubjects.containsKey(event.getSubject()) && Objects.equals(mySavedSubjects.get(event.getSubject()), event.getGrade())
										&& mySavedSubjects.containsValue(event.getGrade()))) {
									mySubjects.items.add(event);
								} else {
									switch (event.getGradeVal()) {
										case EF:
											efSubs.items.add(event);
											break;
										case Q1:
											q1Subs.items.add(event);
											break;
										case Q2:
											q2Subs.items.add(event);
											break;
									}
								}
							}
							if (preferences.getBoolean(SettingsFragment.MY_SUBJECTS, true) && !mySubjects.items.isEmpty())
								mItemGroups.add(mySubjects);
							if (preferences.getBoolean(SettingsFragment.EF, true) && !efSubs.items.isEmpty())
								mItemGroups.add(efSubs);
							if (preferences.getBoolean(SettingsFragment.Q1, true) && !q1Subs.items.isEmpty())
								mItemGroups.add(q1Subs);
							if (preferences.getBoolean(SettingsFragment.Q2, true) && !q2Subs.items.isEmpty())
								mItemGroups.add(q2Subs);
							if (mItemGroups.isEmpty())
								mItemGroups.add(new ItemGroup("keine Vertretungen"));
							notifyDataSetChanged();
							mListener.onLoadingFinished();
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
							mListener.onLoadingFinished();
						}
					});
		}
	}

	public interface OnSubstitutionItemClickListener {
		void onItemClick(SubstitutionEvent event);
	}

	public interface OnLoadingFinishedListener {
		void onLoadingFinished();
	}

	private class ItemGroup {
		String                       headerText;
		ArrayList<SubstitutionEvent> items;

		ItemGroup(String headerText) {
			this.headerText = headerText;
			this.items = new ArrayList<>();
		}

		int getGroupSize() {
			return this.items.size() + 1;
		}
	}

	private class RecyclerHeaderViewHolder extends ViewHolder {
		private TextView header;

		RecyclerHeaderViewHolder(View parent) {
			super(parent);
			this.header = (TextView) parent.findViewById(R.id.textViewHeader);
		}
	}

	private class RecyclerItemViewHolder extends ViewHolder implements View.OnClickListener {

		private final RecyclerRowBinding binding;

		RecyclerItemViewHolder(View parent) {
			super(parent);
			binding = DataBindingUtil.bind(parent);
			itemView.setOnClickListener(this);
			itemView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					switch (motionEvent.getAction()) {
						case MotionEvent.ACTION_DOWN:
							binding.textViewUpper.setSelected(true);
							binding.textViewLower.setSelected(true);
							break;
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							binding.textViewUpper.setSelected(false);
							binding.textViewLower.setSelected(false);
							break;
					}
					return false;
				}
			});
		}

		@Override
		public void onClick(View view) {
			mClickListener.onItemClick(binding.getEvent());
		}
	}
}