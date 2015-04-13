package com.hezhu.takeaway;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class FavoriteActivity extends CommonActivity {
	
	View.OnClickListener addButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ArrayList<String> listname = new ArrayList<String>();
			final boolean[] choice = new boolean[list.size()];
			for(int i=0;i<list.size();i++){
				listname.add(list.get(i).Name);
				choice[i] = isFavorite(i, list);
			}
			final boolean[] _choice = choice.clone();
			new AlertDialog.Builder(FavoriteActivity.this).setTitle("选择要添加的店铺").setMultiChoiceItems(listname.toArray(new String[listname.size()]), choice, new OnMultiChoiceClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1,
						boolean arg2) {
					// TODO Auto-generated method stub
					choice[arg1] = arg2;
				}
				
			}).setPositiveButton("确定", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					for(int i=0;i<list.size();i++){
						if(choice[i] && !_choice[i]) addFavorite(i, list);
						else if(!choice[i] && _choice[i]) removeFavorite(i, list);
					}
					initFavorite();
				}
				
			}).setNegativeButton("取消", null).show();
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		currentList = favoriteList;
		isChange = false;
		isMainPage = false;
		setThemeColor(sp.getInt("theme", 0));
		((ImageButton) findViewById(R.id.ReturnButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		((ImageButton) findViewById(R.id.AddButton)).setOnClickListener(addButton);
		((TextView) findViewById(R.id.FavoriteTip)).setOnClickListener(addButton);
		((TextView) findViewById(R.id.Title)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openOptionsMenu();
			}
		});
		initFavorite();
	}

	@Override
	public void setThemeColor(int index){
		findViewById(R.id.MainLayout).setBackgroundColor(themeColor[index][0]);
		findViewById(R.id.FavoriteTip).setBackgroundColor(themeColor[index][1]);
		findViewById(R.id.listView).setBackgroundColor(themeColor[index][1]);
		editor.putInt("theme", index).commit();
	}
	
}
