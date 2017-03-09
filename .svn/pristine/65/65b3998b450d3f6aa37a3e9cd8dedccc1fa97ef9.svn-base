package com.ppfuns.model.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppfuns.model.entity.ChannelCategorys;
import com.ppfuns.vod.R;

import java.util.List;

public class ChannelCategoryAdapter extends BaseAdapter {

	private Activity owner;
	private List<ChannelCategorys> categoryList;

	public ChannelCategoryAdapter(Activity owner, List<ChannelCategorys> vec) {
		this.owner = owner;
		this.categoryList = vec;
	}

	public int getCount() {
		return categoryList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){			
			convertView = LayoutInflater.from(owner).inflate(R.layout.channel_detail_list_item, null);
			holder = new ViewHolder();
			holder.channelCategoryName = (TextView)convertView.findViewById(R.id.channelCategoryName);
			holder.rootlayout = (RelativeLayout) convertView.findViewById(R.id.rootlayout);
			convertView.setTag(holder);
		}else{
			 holder = (ViewHolder)convertView.getTag();
		}

		ChannelCategorys bean = categoryList.get(position);
			if(!TextUtils.isEmpty(bean.operationTagName)){
			holder.channelCategoryName.setText(bean.operationTagName);
		}else{
			holder.channelCategoryName.setText("");
		}
//		if(bean.isSelect){
//			holder.channelCategoryName.setTextColor(Color.parseColor("#ffffff"));
//			//holder.liebg.setVisibility(View.VISIBLE);
//
//		}else{
//			holder.channelCategoryName.setTextColor(Color.parseColor("#656576"));
//			//holder.liebg.setVisibility(View.INVISIBLE);
//
//		}
		return convertView;
	}
	
	static class ViewHolder{
		TextView channelCategoryName;
		View liebg;
		RelativeLayout rootlayout;

	}
}