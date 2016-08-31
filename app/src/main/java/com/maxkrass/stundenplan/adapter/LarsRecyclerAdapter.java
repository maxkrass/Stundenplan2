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
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.RecyclerRowBinding;
import com.maxkrass.stundenplan.objects.LarsSubstitutionEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class LarsRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
	private static final int VIEWTYPE_HEADER = 111;
	private static final int VIEWTYPE_ITEM   = 222;
	private int                  index;
	private ArrayList<ItemGroup> mItemGroup;

	public LarsRecyclerAdapter(int index) {
		this.mItemGroup = new ArrayList<>();
		this.index = index;
	}

	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == VIEWTYPE_HEADER) {
			return new RecyclerHeaderViewHolderKlasse(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false));
		}
		if (viewType == VIEWTYPE_ITEM) {
			RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()));
			return new RecyclerItemViewHolderKlasse(binding.getRoot());
		}
		return null;
	}

	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		int size = 0;
		try {
			for (ItemGroup g : this.mItemGroup) {
				if (position == size) {
					bindHeader((RecyclerHeaderViewHolderKlasse) viewHolder, g);
					return;
				}
				size += g.getGroupSize();
				if (position < size) {
					bindItem((RecyclerItemViewHolderKlasse) viewHolder, g.items.get((g.getGroupSize() - 1) - (size - position)));
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getItemViewType(int position) {
		if (this.mItemGroup != null && position >= 0) {
			int size = 0;
			for (ItemGroup group : this.mItemGroup) {
				if (position == size) {
					return VIEWTYPE_HEADER;
				}
				size += group.getGroupSize();
				if (position < size) {
					return VIEWTYPE_ITEM;
				}
			}
		}
		return 0;
	}

	public int getItemCount() {
		if (mItemGroup == null || mItemGroup.isEmpty()) {
			return 0;
		}
		int size = 0;
		for (ItemGroup itemGroup : mItemGroup) {
			size += itemGroup.getGroupSize();
		}
		return size;
	}

	private void bindItem(RecyclerItemViewHolderKlasse viewHolder, LarsSubstitutionEvent event) {
		viewHolder.binding.setEvent(event);
		//TODO viewHolder.itemView.setOnClickListener(new C04981(headerText, dataList));
	}

	private void bindHeader(RecyclerHeaderViewHolderKlasse viewHolder, ItemGroup g) {
		viewHolder.header.setText(g.headerText);
	}

	public void setNewContent(ArrayList<LarsSubstitutionEvent> list) {
		//Map<String, Boolean> settings = MainActivity.settingsMap;
		this.mItemGroup.clear();
		if (list != null) {
			try {
				//TODO get the real saved subjects
				HashMap<String, LarsSubstitutionEvent.Grade> mySavedSubjects = new HashMap<>();
				ItemGroup mySubjects = new ItemGroup("Meine Fächer");
				ItemGroup efSubs = new ItemGroup("EF");
				ItemGroup q1Subs = new ItemGroup("Q1");
				ItemGroup q2Subs = new ItemGroup("Q2");
				for (LarsSubstitutionEvent event : list) {
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
					if (mySavedSubjects.containsKey(event.getSubject()) && mySavedSubjects.get(event.getSubject()).equals(event.getGrade()))
						mySubjects.items.add(event);
				}
				mItemGroup.add(mySubjects);
				mItemGroup.add(efSubs);
				mItemGroup.add(q1Subs);
				mItemGroup.add(q2Subs);
			} catch (Exception e2) {
				this.mItemGroup.clear();
				e2.printStackTrace();
			}
			notifyDataSetChanged();
		}
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

	class RecyclerHeaderViewHolderKlasse extends ViewHolder {
		private TextView header;

		public RecyclerHeaderViewHolderKlasse(View parent) {
			super(parent);
			this.header = (TextView) parent.findViewById(R.id.textViewHeader);
		}
	}

	class RecyclerItemViewHolderKlasse extends ViewHolder {

		private final RecyclerRowBinding binding;

		public RecyclerItemViewHolderKlasse(View parent) {
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