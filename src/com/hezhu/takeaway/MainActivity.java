package com.hezhu.takeaway;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends CommonActivity {

	private static final String welcome_info = "欢迎使用外卖神器，这是一个专为方便大家叫外卖而开发的免费应用，应用中包含了揭阳市许多知名外卖商家，轻轻一点即可叫外卖，支持收藏自己最常吃的外卖商家等功能，从此不必苦记外卖号码，按菜单键还有更多实用功能。\n本应用不定时更新。\n\n减少白色污染，从你我他做起。";
	private static final String welcome_info_2 = "本应用目前还在完善阶段，欢迎大家将自己的意见和建议发送到邮箱hezhu.takeaway@gmail.com或点击菜单栏中的“意见/建议”给我们提出宝贵的意见，以便我们不断地完善功能，万分感谢。";
	private static final String update_info = 
			"改进 UI；\n" +
			"修复 短信订餐号码错误的BUG。";
	private static int OwnListVersionCode = 10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = getSharedPreferences("data",Context.MODE_PRIVATE);
		editor = sp.edit();
		if(sp.getInt("ListVersionCode", 0) < OwnListVersionCode)moveListXML();
		initDefaultList();
		try {
			versionName = getPackageManager().getPackageInfo("com.hezhu.takeaway", 0).versionName;
			versionCode = getPackageManager().getPackageInfo("com.hezhu.takeaway", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sp.getBoolean("isFirst", true))
		{
			new AlertDialog.Builder(this).setTitle("欢迎使用").setMessage(welcome_info).setPositiveButton("下一页", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					new AlertDialog.Builder(MainActivity.this).setTitle("欢迎使用").setMessage(welcome_info_2).setNegativeButton("关闭", null).show();
				}
				
			}).setNegativeButton("跳过指引", null).show();
			editor.putBoolean("isFirst", false);
		}
		int currentVersionCode;
		try{
			currentVersionCode = sp.getInt("versionCode", versionCode);
		} catch (Exception e) {
			currentVersionCode = (int) sp.getLong("versionCode", versionCode);
		}
		if(currentVersionCode!=versionCode)
		{
			new AlertDialog.Builder(this).setTitle("新版本的更新内容").setMessage(update_info).setPositiveButton("关闭", null).show();
		}
		editor.putInt("versionCode", versionCode);
		editor.commit();
		initListView();
		
		((Button) findViewById(R.id.FavoriteButton)).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
				startActivity(intent);
			}
			
		});
		((TextView) findViewById(R.id.Title)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openOptionsMenu();
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		currentList = list;
		if(isExit){
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		if(isChange)initList();
		isChange = false;
		isMainPage = true;
		setThemeColor(sp.getInt("theme", 0));
		super.onStart();
	}
	
	private void moveListXML(){
		try {
			InputStream in = this.getAssets().open("list.xml");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while((line = reader.readLine())!=null){
				buffer.append(line);
			}
			updateList(buffer.toString());
		} catch (Exception e) {}
	}
}