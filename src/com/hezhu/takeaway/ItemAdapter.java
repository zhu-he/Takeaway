package com.hezhu.takeaway;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {

	private List<ItemHolder> items;
	private CommonActivity activity;
	
	public ItemAdapter(CommonActivity context, List<ItemHolder> items) {
		this.items = items;
		this.activity = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ItemHolder holder = null;
		if(convertView==null) {
			holder = new ItemHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.listview_item, null);
			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}
		((TextView) convertView.findViewById(R.id.ItemTitle)).setText(items.get(position).Name);
		String NumberString = "";
		String[] Number = items.get(position).Number;
		for(int i=0;i<Number.length;i++) {
			NumberString+=Number[i];
			if(i != Number.length - 1) NumberString = NumberString + ",";
		}
		((TextView) convertView.findViewById(R.id.ItemSubtitle)).setText(NumberString);
		((ImageButton) convertView.findViewById(R.id.CallButton)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.selectNumber(position);
			}
			
		});
		return convertView;
	}
}
