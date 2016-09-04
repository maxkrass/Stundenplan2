package com.maxkrass.stundenplan.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.RecyclerRowBinding;
import com.maxkrass.stundenplan.objects.LarsSubstitutionEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class LarsRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
	private static final int VIEW_TYPE_HEADER = 111;
	private static final int VIEW_TYPE_ITEM   = 222;
	private ArrayList<ItemGroup>      mItemGroups;
	private OnLoadingFinishedListener mListener;

	public LarsRecyclerAdapter(OnLoadingFinishedListener listener) {
		mItemGroups = new ArrayList<>();
		mListener = listener;
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
				bindItem((RecyclerItemViewHolder) viewHolder, g.items.get((g.getGroupSize() - 1) - (size - position)));
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

	private void bindItem(RecyclerItemViewHolder viewHolder, LarsSubstitutionEvent event) {
		viewHolder.binding.setEvent(event);
		//TODO viewHolder.itemView.setOnClickListener(new C04981(headerText, dataList));
	}

	private void bindHeader(RecyclerHeaderViewHolder viewHolder, ItemGroup g) {
		viewHolder.header.setText(g.headerText);
	}

	public void setNewContent(final ArrayList<LarsSubstitutionEvent> list) {
		//Map<String, Boolean> settings = MainActivity.settingsMap;
		this.mItemGroups.clear();
		if (list != null) {
			//TODO get the real saved subjects
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
							for (LarsSubstitutionEvent event : list) {
								if (mySavedSubjects.containsKey(event.getSubject()) && Objects.equals(mySavedSubjects.get(event.getSubject()), event.getGrade().name())
										|| Objects.equals(event.getType(), LarsSubstitutionEvent.SubstitutionType.Special) && mySavedSubjects.containsValue(event.getGrade().name())) {
									mySubjects.items.add(event);
								} else {
									switch (event.getGrade()) {
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
							if (!mySubjects.items.isEmpty()) mItemGroups.add(mySubjects);
							if (!efSubs.items.isEmpty()) mItemGroups.add(efSubs);
							if (!q1Subs.items.isEmpty()) mItemGroups.add(q1Subs);
							if (!q2Subs.items.isEmpty()) mItemGroups.add(q2Subs);
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

	public interface OnLoadingFinishedListener {
		void onLoadingFinished();
	}

	/* renamed from: de.mpgdusseldorf.lpewewq.mpgdsseldorf.MyRecyclerAdapter.1 */
	class C04981 implements OnClickListener {
		final /* synthetic */ ArrayList val$dataList;
		final /* synthetic */ String    val$headerText;

		C04981(String str, ArrayList arrayList) {
			this.val$headerText = str;
			this.val$dataList = arrayList;
		}

		public void onClick(View view) {
			//MainActivity.showSheet(LarsRecyclerAdapter.this.index, this.val$headerText, this.val$dataList);
		}
	}

	/* renamed from: de.mpgdusseldorf.lpewewq.mpgdsseldorf.MyRecyclerAdapter.2 */
	class C04992 implements Comparator<ArrayList<String>> {
		C04992() {
		}

		public int compare(ArrayList<String> list1, ArrayList<String> list2) {
			return list1.get(1).trim().substring(0, 1).compareToIgnoreCase(list2.get(1).trim().substring(0, 1));
		}
	}

	private class ItemGroup {
		String                           headerText;
		ArrayList<LarsSubstitutionEvent> items;

		public ItemGroup(String headerText) {
			this.headerText = headerText;
			this.items = new ArrayList<>();
		}

		public int getGroupSize() {
			return this.items.size() + 1;
		}
	}

	class RecyclerHeaderViewHolder extends ViewHolder {
		private TextView header;

		public RecyclerHeaderViewHolder(View parent) {
			super(parent);
			this.header = (TextView) parent.findViewById(R.id.textViewHeader);
		}
	}

	class RecyclerItemViewHolder extends ViewHolder {

		private final RecyclerRowBinding binding;

		public RecyclerItemViewHolder(View parent) {
			super(parent);
			binding = DataBindingUtil.bind(parent);
			this.itemView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					switch (motionEvent.getAction()) {
						case ConnectionResult.SUCCESS /*0*/:
							binding.textViewUpper.setSelected(true);
							binding.textViewLower.setSelected(true);
							break;
						case ConnectionResult.SERVICE_MISSING /*1*/:
						case ConnectionResult.SERVICE_DISABLED /*3*/:
							binding.textViewUpper.setSelected(false);
							binding.textViewLower.setSelected(false);
							break;
					}
					return false;
				}
			});
		}
	}
}