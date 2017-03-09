package com.ppfuns.model.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppfuns.model.entity.Category;
import com.ppfuns.vod.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

	private Activity owner;
	private List<Category> categoryList;

	public CategoryAdapter(Activity owner, List<Category> vec) {
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
			convertView = LayoutInflater.from(owner).inflate(R.layout.collect_detail_list_item, null);
			holder = new ViewHolder();
			holder.channelCategoryName = (TextView)convertView.findViewById(R.id.channelCategoryName);
			//holder.liebg  = (View)convertView.findViewById(R.id.liebg);
			convertView.setTag(holder);
		}else{
			 holder = (ViewHolder)convertView.getTag();
		}

		Category bean = categoryList.get(position);
		if(!TextUtils.isEmpty(bean.getCategoryName())){
			holder.channelCategoryName.setText(bean.getCategoryName());
		}else{
			holder.channelCategoryName.setText("");
		}
//		if(bean.isSelect()){
//			holder.channelCategoryName.setTextColor(Color.parseColor("#ffffff"));
//			//holder.liebg.setVisibility(View.VISIBLE);
//		}else{
//			holder.channelCategoryName.setTextColor(Color.parseColor("#656576"));
//			//holder.liebg.setVisibility(View.INVISIBLE);
//		}
		return convertView;
	}
	
	static class ViewHolder{
		TextView channelCategoryName;
		View liebg;
	}
}