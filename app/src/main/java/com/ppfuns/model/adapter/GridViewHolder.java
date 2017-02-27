package com.ppfuns.model.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ppfuns.model.entity.SubjectCategory;
import com.ppfuns.vod.R;

import java.util.List;

public class GridViewHolder extends ViewHolder {
	
	public ImageView img;
	public TextView subjectName;
	
	public GridViewHolder(View itemView) {
		super(itemView);
		img = (ImageView)itemView.findViewById(R.id.img);
		subjectName = (TextView)itemView.findViewById(R.id.subjectName);


	}

}
