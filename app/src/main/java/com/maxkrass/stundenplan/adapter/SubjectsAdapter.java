package com.maxkrass.stundenplan.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxkrass.stundenplan.databinding.SubjectViewBinding;
import com.maxkrass.stundenplan.objects.Color;
import com.maxkrass.stundenplan.objects.Subject;
import com.orm.SugarRecord;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

	private List<Subject> subjectList;
	private Context context;
	private View.OnLongClickListener onLongClickListener;
	private View.OnClickListener onClickListener;

	public SubjectsAdapter(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		subjectList = SugarRecord.listAll(Subject.class, "name");
		this.context = context;
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(SubjectViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());

	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.binding.setSubject(subjectList.get(position));
		((GradientDrawable) holder.binding.subjectColor.getBackground()).setColor(Color.values()[holder.binding.getSubject().getColorIndex()].getColor(context));

	}

	@Override
	public int getItemCount() {
		return subjectList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		SubjectViewBinding binding;

		ViewHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
			itemView.setOnClickListener(onClickListener);
			itemView.setOnLongClickListener(onLongClickListener);
		}
	}

}
