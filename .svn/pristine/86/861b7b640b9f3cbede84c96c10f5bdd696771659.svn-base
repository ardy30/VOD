package com.ppfuns.model.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppfuns.model.entity.SubjectCategory;
import com.ppfuns.vod.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<GridViewHolder> {
	private List<SubjectCategory> subjectList;
	private int picWidth,picHeight;
	private int paddingWidth,paddingHeight;

	public RecyclerViewAdapter(List<SubjectCategory> subjectList,int picWidth) {
		this.subjectList = subjectList;
		this.picWidth = picWidth;
		this.picHeight = (int)(picWidth*0.666051);
		this.paddingWidth = paddingWidth;
		this.paddingHeight = paddingHeight;
	}

	@Override
	public int getItemCount() {
		return subjectList.size();
	}

	@Override
	public void onBindViewHolder(GridViewHolder arg0, int arg1) {
		GridViewHolder holder = arg0;
		holder.img.setImageResource(R.drawable.ex);
		holder.subjectName.setText(subjectList.get(arg1).getSubjectName());
	}

	@Override
	public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_grid_item, parent, false);
		TextView tv = (TextView)view.findViewById(R.id.subjectName);
		ImageView img = (ImageView)view.findViewById(R.id.img);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(picWidth, picHeight+tv.getMeasuredHeight()+60);
		view.setLayoutParams(lp);
		LinearLayout.LayoutParams imgParam = (LinearLayout.LayoutParams)img.getLayoutParams();
		imgParam.width = picWidth-paddingWidth;
		imgParam.height = picHeight-paddingHeight;
		img.setLayoutParams(imgParam);
		img.setFocusable(true);
		tv.setFocusable(false);
		return new GridViewHolder(view);
	}

}
