package com.hezhu.takeaway;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

@SuppressLint("HandlerLeak")
public class CommonActivity extends Activity {
	public static boolean isMainPage;
	public static boolean isChange = false;
	public static boolean isExit = false;
	private int lastPosition = -1;
	public static List<ItemHolder> list = new ArrayList<ItemHolder>();
	public static List<ItemHolder> defaultList = new ArrayList<ItemHolder>();
	public static List<ItemHolder> favoriteList = new ArrayList<ItemHolder>();
	public static List<ItemHolder> currentList;
	public static String versionName;
	public static int versionCode;
	public static String ListVersionName;
	public static int ListVersionCode;
	public static int newVersionCode;
	public static String newVersionName;
	public static String newPathName;
	public static int newListVersionCode;
	public static String newListVersionName;
	public static String newListString;
	public static int[][] themeColor = {
		{0xff222222,0xffffffff},
		{0xff0064b6,0xddffffff},
		{0xffee6f90,0xddffffff},
		{0xff87ac06,0xddffffff},
		{0xff993399,0xddffffff},
		{0xffff3333,0xddffffff},
		{0xffff8033,0xddffffff},
		{0xffffda00,0xddffffff},
		{0xff4b5057,0xddffffff},
		{0xff5e4737,0xddffffff}
	};
	public static final String[] themename = {
		"黑白配","天际蓝","蜜桃粉","青草绿","葡萄紫","樱桃红","热带橙","柠檬黄","子夜灰","摩卡棕"
	};
	public static SharedPreferences sp;
	public static SharedPreferences.Editor editor;
	public DatabaseHelper sqlite = new DatabaseHelper(this);
	private static final String VersionURL = "https://hezhu-hezhu.rhcloud.com/takeaway/xml/version.php";
	private static final String ListURL = "https://hezhu-hezhu.rhcloud.com/takeaway/xml/list.php";
	private static final String DownloadURL = "https://hezhu-hezhu.rhcloud.com/takeaway/files/";
	private static final String FeedbackURL = "https://hezhu-hezhu.rhcloud.com/takeaway/feedback_b.php";
	ProgressDialog updateDialog;
	//更新数据
	OnClickListener updateListListener = new OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if(isConnected()){
				showProgressDialog("正在更新数据","请稍等…");
				new Thread(new Runnable(){
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							URL url = new URL(ListURL);
			                URLConnection conn = url.openConnection();
			                conn.setConnectTimeout(10000);
			                conn.setReadTimeout(10000);
			                InputStream in = conn.getInputStream();
			    			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			    			StringBuffer buffer = new StringBuffer();
			    			String line = "";
			    			while((line = reader.readLine())!=null){
			    				buffer.append(line);
			    			}
			    			newListString = buffer.toString();
			                handle.sendEmptyMessage(2);
						} catch (Exception e) {
							// TODO Auto-generated catch block
			                handle.sendEmptyMessage(-1);
							e.printStackTrace();
						}
					}
					
				}).start();
			} else Toast.makeText(getApplication(), "请检查网络连接", Toast.LENGTH_SHORT).show();
		}
		
	};
	//更新要用到的东西
	Handler handle = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(!updateDialog.isShowing())return;
			updateDialog.cancel();
			switch(msg.what){
			case -1:
				Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
				break;
			case 0:
				if(newVersionCode>versionCode){
					new AlertDialog.Builder(CommonActivity.this)
					.setTitle("发现新版本")
					.setMessage("发现应用有新版本("+newVersionName+")，是否更新？")
					.setPositiveButton("是", new OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_VIEW);  
							intent.setData(Uri.parse(DownloadURL + newPathName));  
							startActivity(intent);  
						}
						
					})
					.setNegativeButton("否",	 null)
					.show();
				} else {
					if(newListVersionCode>ListVersionCode){
						new AlertDialog.Builder(CommonActivity.this)
						.setTitle("发现新版本")
						.setMessage("发现店铺信息有新版本("+newListVersionName+")，是否更新？")
						.setPositiveButton("是", updateListListener)
						.setNegativeButton("否",	 null)
						.show();
					} else Toast.makeText(getApplication(), "您的应用及店铺信息均为最新版本，无需更新", Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				Toast.makeText(getApplication(), "发送成功，感谢您的意见和建议", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(getApplication(), "更新成功", Toast.LENGTH_SHORT).show();
				updateList(newListString);
    			initDefaultList();
    			if(!isMainPage)initFavorite();
				break;
			case 3:
				if(newListVersionCode>ListVersionCode)updateListListener.onClick(updateDialog, 0);
				else Toast.makeText(getApplication(), "您的店铺信息为最新版本，无需更新", Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	//主Menu创建
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	//设置主题
	public void setThemeColor(int index){
		findViewById(R.id.MainLayout).setBackgroundColor(themeColor[index][0]);
		findViewById(R.id.listView).setBackgroundColor(themeColor[index][1]);
		editor.putInt("theme", index).commit();
	}
	//所有Menu点击事件
	@SuppressWarnings("deprecation")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		switch(item.getItemId()) {
		case R.id.menu_quit:
			finish();
			isExit = true;
			if(isMainPage)
				android.os.Process.killProcess(android.os.Process.myPid());
			break;
		case R.id.menu_about:
			AlertDialog dialog = new AlertDialog.Builder(CommonActivity.this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle(getString(R.string.app_name))
			.setMessage("应用版本："+versionName+"\n数据版本："+ListVersionName+"\n作者：何柱\n邮箱：hezhu.takeaway@gmail.com")
			.setNegativeButton("关闭", null)
			.show();
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//			params.width = ((getWindowManager().getDefaultDisplay().getWidth()<getWindowManager().getDefaultDisplay().getHeight())
//					?getWindowManager().getDefaultDisplay().getWidth():getWindowManager().getDefaultDisplay().getHeight())*3/4;
			params.width = getWindowManager().getDefaultDisplay().getWidth()*4/5;
			dialog.getWindow().setAttributes(params);
			break;
		case R.id.menu_feedback:
			if(isConnected()){
				final EditText et = new EditText(this);
				et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				et.setGravity(Gravity.TOP);
				et.setSingleLine(false);
				et.setHorizontallyScrolling(false);
	//			et.setHeight(((getWindowManager().getDefaultDisplay().getWidth()>getWindowManager().getDefaultDisplay().getHeight())
	//					?getWindowManager().getDefaultDisplay().getWidth():getWindowManager().getDefaultDisplay().getHeight())/3);
				et.setHeight(getWindowManager().getDefaultDisplay().getHeight()/3);
				new AlertDialog.Builder(this)
				.setTitle("意见/建议")
				.setView(et)
				.setPositiveButton("发送", new OnClickListener(){
	
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if(et.getText().toString().replaceAll("[\n|\r| ]","") == ""){
							Toast.makeText(getApplication(), "内容不能为空", Toast.LENGTH_SHORT).show();
							return;
						}
							showProgressDialog("正在发送意见/建议","请稍等…");
							new Thread(new Runnable(){
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										URL url = new URL(FeedbackURL + "?text="+URLEncoder.encode(et.getText().toString().replace("\n", "<br />").replace("+", "&#43;")));
						                URLConnection conn = url.openConnection();
						                conn.setConnectTimeout(10000);
						                conn.setReadTimeout(10000);
						                conn.getInputStream();
						                handle.sendEmptyMessage(1);
									} catch (Exception e) {
										// TODO Auto-generated catch block
						                handle.sendEmptyMessage(-1);
										e.printStackTrace();
									}
								}
								
							}).start();
					}
					
				})
				.setNegativeButton("取消", null).show();
				//dialog.getWindow().setAttributes(dialog.getWindow().getAttributes());
			} else Toast.makeText(getApplication(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_update_list:
			if(isConnected()){
				showProgressDialog("正在检查更新","请稍等…");
				new Thread(new Runnable(){
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							URL url = new URL(VersionURL);
			                URLConnection conn = url.openConnection();
			                conn.setConnectTimeout(10000);
			                conn.setReadTimeout(10000);
			                InputStream in = conn.getInputStream();
			                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			                DocumentBuilder builder = factory.newDocumentBuilder();
			                Document doc = builder.parse(in);
			                newListVersionCode = Integer.parseInt(doc.getElementsByTagName("lvc").item(0).getFirstChild().getNodeValue());
			                handle.sendEmptyMessage(3);
						} catch (Exception e) {
							// TODO Auto-generated catch block
			                handle.sendEmptyMessage(-1);
							e.printStackTrace();
						}
					}
					
				}).start();
			} else Toast.makeText(getApplication(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_update:
			if(isConnected()){
				showProgressDialog("正在检查更新","请稍等…");
				new Thread(new Runnable(){
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							URL url = new URL(VersionURL);
			                URLConnection conn = url.openConnection();
			                conn.setConnectTimeout(10000);
			                conn.setReadTimeout(10000);
			                InputStream in = conn.getInputStream();
			                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			                DocumentBuilder builder = factory.newDocumentBuilder();
			                Document doc = builder.parse(in);
			                newVersionCode = Integer.parseInt(doc.getElementsByTagName("vc").item(0).getFirstChild().getNodeValue());
			                newVersionName = doc.getElementsByTagName("vn").item(0).getFirstChild().getNodeValue();
			                newPathName = doc.getElementsByTagName("pn").item(0).getFirstChild().getNodeValue();
			                newListVersionCode = Integer.parseInt(doc.getElementsByTagName("lvc").item(0).getFirstChild().getNodeValue());
			                newListVersionName = doc.getElementsByTagName("lvn").item(0).getFirstChild().getNodeValue();
			                handle.sendEmptyMessage(0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
			                handle.sendEmptyMessage(-1);
							e.printStackTrace();
						}
					}
					
				}).start();
			} else Toast.makeText(getApplication(), "请检查网络连接", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_add:
			final View vi = inflater.inflate(R.layout.add_item_dialog, null);
			new AlertDialog.Builder(this).setTitle("添加店铺").setView(vi).setPositiveButton("添加", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(((TextView) vi.findViewById(R.id.editTextName)).getText().toString().replaceAll("[\n|\r| ]","") == ""){
						Toast.makeText(getApplication(), "名字不能为空", Toast.LENGTH_SHORT).show();
						return;
					}
					Cursor cursor = sqlite.getReadableDatabase().rawQuery("select * from CustomList", null);
					int sequence = cursor.getCount();
					SQLiteDatabase dbw = sqlite.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("name", ((TextView) vi.findViewById(R.id.editTextName)).getText().toString());
					values.put("number", ((TextView) vi.findViewById(R.id.editTextNumber)).getText().toString().replace("，", ","));
					values.put("detail", ((TextView) vi.findViewById(R.id.editTextDetail)).getText().toString());
					values.put("sequence", sequence);
					dbw.insert("CustomList", null, values);
					initList();
					if(((CheckBox) vi.findViewById(R.id.AddCheckBox)).isChecked())addFavorite(list.size()-1, list);
					initFavorite();
					setListPosition(list.size());
					if(!isMainPage)isChange = true;
				}
				
			}).setNegativeButton("取消", null).show();
			//dialog.getWindow().setAttributes(dialog.getWindow().getAttributes());
			break;
		case R.id.menu_theme:
			final int oldTheme = sp.getInt("theme", 0);
			new AlertDialog.Builder(this).setTitle("请选择主题").setSingleChoiceItems(themename, oldTheme, new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					setThemeColor(which);
				}
				
			}).setPositiveButton("保存", null).setNegativeButton("取消", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					setThemeColor(oldTheme);
				}
				
			}).show();
			//dialog.getWindow().setAttributes(dialog.getWindow().getAttributes());
			break;
		case 97:
			if(isFavorite(lastPosition, currentList))removeFavorite(lastPosition, currentList);
			else addFavorite(lastPosition, currentList);
			if(!isMainPage){
				initFavorite();
				setListPosition(lastPosition);
			}
			break;
		case 98:
			final View editVi = inflater.inflate(R.layout.add_item_dialog, null);
			final TextView Name = (TextView) editVi.findViewById(R.id.editTextName);
			final TextView Number = (TextView) editVi.findViewById(R.id.editTextNumber);
			final TextView Detail = (TextView) editVi.findViewById(R.id.editTextDetail);
			final CheckBox cb = (CheckBox) editVi.findViewById(R.id.AddCheckBox);
			final ItemHolder ih = currentList.get(lastPosition);
			Name.setText(ih.Name);
			String NumberText = "";
			for(int i=0;i<ih.Number.length;i++) {
				NumberText+=ih.Number[i];
				if(i != ih.Number.length - 1) NumberText = NumberText + ",";
			}
			Number.setText(NumberText);
			Detail.setText(ih.Detail);
			cb.setChecked(isFavorite(lastPosition, currentList));
			new AlertDialog.Builder(this).setTitle("编辑店铺").setView(editVi).setPositiveButton("保存", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(((TextView) editVi.findViewById(R.id.editTextName)).getText().toString().replaceAll("[\n|\r| ]","") == ""){
						Toast.makeText(getApplication(), "名字不能为空", Toast.LENGTH_SHORT).show();
						return;
					}
					SQLiteDatabase dbw = sqlite.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("name", Name.getText().toString());
					values.put("number", Number.getText().toString().replace("，", ","));
					values.put("detail", Detail.getText().toString());
					dbw.update("CustomList", values, "_id=?", new String[]{Integer.toString(ih.ID)});
					initList();
					if(cb.isChecked())addFavorite(lastPosition, currentList);
					else removeFavorite(lastPosition, currentList);
					initFavorite();
					setListPosition(lastPosition);
					if(!isMainPage)isChange = true;
				}
				
			}).setNegativeButton("取消", null).show();
			//dialog.getWindow().setAttributes(dialog.getWindow().getAttributes());
			break;
		case 99:
			int ID = currentList.get(lastPosition).ID;
			SQLiteDatabase dbr = sqlite.getReadableDatabase();
			SQLiteDatabase dbw = sqlite.getWritableDatabase();
			Cursor cursor = dbr.rawQuery("select * from CustomList where _id=?", new String[]{Integer.toString(ID)});
			cursor.moveToFirst();
			int sequence = cursor.getInt(4);
			cursor = dbr.rawQuery("select * from CustomList", null);
			dbw.execSQL("delete from CustomList where _id=?", new String[]{Integer.toString(ID)});
			dbw.execSQL("update CustomList set sequence=sequence-1 where sequence>?", new String[]{Integer.toString(sequence)});
			initList();
			initFavorite();
			setListPosition(lastPosition);
			if(!isMainPage)isChange = true;
			dbr.close();
			dbw.close();
			break;
		case 100:
			showDetail(lastPosition);
			break;
		default:
			ItemHolder _ih = currentList.get(lastPosition);
			if(item.getItemId()-101<_ih.Number.length)Call(_ih.Number[item.getItemId()-101]);
			else Send(_ih.SMS[item.getItemId()-_ih.Number.length-101]);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	//订餐界面
	public void selectNumber(final int position) {
		final ItemHolder ih = currentList.get(position);
		List<String> items = new ArrayList<String>();
		for(String s:ih.Number)items.add("拨打电话 "+s);
		if(ih.SMS!=null)for(String s:ih.SMS)items.add("发送短信 "+s);
		new AlertDialog.Builder(this).setTitle("请选择订餐方式").setItems(items.toArray(new String[]{}), new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1<ih.Number.length)Call(ih.Number[arg1]);
				else Send(ih.SMS[arg1-ih.Number.length]);
			}
			
		}).setNegativeButton("取消", null).show();
	}
	//拨打指定号码
	public void Call(String number) {
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+number)); 
		startActivity(intent);
		Toast.makeText(getApplication(), "请按拨号键", Toast.LENGTH_SHORT).show();
	}
	//发送信息
	public void Send(String number) {
		startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("smsto:" + number)));  
	}
	//显示详细信息
	public void showDetail(final int position) {
		ItemHolder ih = currentList.get(position);
		Builder dialog = new AlertDialog.Builder(CommonActivity.this).setTitle(ih.Name)
		.setPositiveButton("订餐", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				selectNumber(position);
			}
			
		}).setNeutralButton((isFavorite(position, currentList))?"取消收藏":"收藏", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if(isFavorite(position, currentList)) removeFavorite(position, currentList);
				else addFavorite(position, currentList);
				if(!isMainPage){
					initFavorite();
					setListPosition(position);
				}
			}
			
		})
		.setNegativeButton("取消", null);
		if(ih.MenuList!=null) dialog.setAdapter(new SimpleAdapter(CommonActivity.this, ih.MenuList, R.layout.listview_menu_item ,new String[]{"MenuName","MenuPrice","MenuTitle"}, new int[]{R.id.MenuName,R.id.MenuPrice,R.id.MenuTitle}),null).show();
		else dialog.setMessage((ih.Detail=="")?"暂无详细信息，请留意新版本":(ih.Detail)).show();
	}
	//重置列表（不重置默认列表）
	public void initList(){
		list.clear();
		for(int i=0;i<defaultList.size();i++){
			list.add(defaultList.get(i));
		}
		ListView lv = (ListView) findViewById(R.id.listView);
		addFavoriteList();
		lv.setAdapter(new ItemAdapter(this,list));
	}
	//添加收藏列表到主列表
	private void addFavoriteList() {
		// TODO Auto-generated method stub
		SQLiteDatabase dbr = sqlite.getReadableDatabase();
		Cursor cursor = dbr.rawQuery("select * from CustomList order by sequence", null);
		while(cursor.moveToNext()){
			ItemHolder item = new ItemHolder();
			item.Name = cursor.getString(1);
			item.Number = cursor.getString(2).split(",");
			item.Detail = cursor.getString(3);
			item.isDefault = false;
			item.ID = cursor.getInt(0);
			list.add(item);
		}
		dbr.close();
	}
	//移动到列表指定位置
	public void setListPosition(int position){
		((ListView) findViewById(R.id.listView)).setSelection(position);
	}
	//初始化列表属性，点击、长按事件
	public void initListView(){
		ListView lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				showDetail(position);
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				lastPosition = arg2;
				arg0.showContextMenu();
				return true;
			}
			
		});
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){

			@Override
			public void onCreateContextMenu(ContextMenu arg0, View arg1,
					ContextMenuInfo arg2) {
				// TODO Auto-generated method stub
				ItemHolder ih = currentList.get(lastPosition);
				arg0.setHeaderTitle(ih.Name);
				arg0.add(0, 97, 0, (isFavorite(lastPosition, currentList))?"取消收藏":"收藏");
				if(!ih.isDefault){
					arg0.add(0, 98, 0, "编辑店铺");
					arg0.add(0, 99, 0, "删除店铺");
				}
				arg0.add(0, 100, 0, "查看详细信息");
				for(int i=0;i<ih.Number.length;i++)
					arg0.add(0, 101+i, 0, "拨打电话 "+ih.Number[i]);
				if(ih.SMS!=null)for(int i=0;i<ih.SMS.length;i++)
					arg0.add(0, 101+ih.Number.length+i, 0, "发送短信 "+ih.SMS[i]);
			}
			
		});
	}
	//初始化收藏界面
	public void initFavorite(){
		if(isMainPage)return;
		TextView tvf = (TextView) findViewById(R.id.FavoriteTip);
		Cursor cursor = sqlite.getReadableDatabase().rawQuery("select * from FavoriteList order by sequence", null);
		favoriteList.clear();
		if(cursor.getCount()>0){
			int count = 0;
			while(cursor.moveToNext()){
				Boolean b = (cursor.getInt(2)==1)?true:false;
				int list_id = cursor.getInt(1);
				Boolean isExist = false;
				for(int i=0;i<list.size();i++){
					if((b==list.get(i).isDefault) && (list_id==list.get(i).ID)){
						favoriteList.add(list.get(i));
						isExist = true;
						break;
					}
				}
				if(!isExist)removeFavorite(cursor.getInt(2),list_id);
				else count++;
			}
			if(count==0)tvf.setVisibility(0);
			else tvf.setVisibility(8);
		} else tvf.setVisibility(0);
		initListView();
		ListView lv = (ListView) findViewById(R.id.listView);
		ItemAdapter adapter = new ItemAdapter(this,favoriteList);
		lv.setAdapter(adapter);
	}
	//添加收藏
	public void addFavorite(int position, List<ItemHolder> list){
		int is_default=(list.get(position).isDefault)?1:0;
		int list_id=list.get(position).ID;
		SQLiteDatabase dbr = sqlite.getReadableDatabase();
		Cursor cursor = dbr.rawQuery("select * from FavoriteList where list_id=? and is_default=?", 
				new String[]{Integer.toString(list_id),Integer.toString(is_default)});
		if(cursor.getCount()==0){
			cursor = dbr.rawQuery("select * from FavoriteList", null);
			ContentValues values = new ContentValues();
			values.put("list_id", list_id);
			values.put("is_default", is_default);
			values.put("sequence", cursor.getCount());
			sqlite.getWritableDatabase().insert("FavoriteList", null, values);
		}
		dbr.close();
	}
	//移除收藏（直接）
	public void removeFavorite(int is_default, int list_id){
		Cursor cursor = sqlite.getReadableDatabase().rawQuery("select * from FavoriteList where list_id=? and is_default=?", 
				new String[]{Integer.toString(list_id),Integer.toString(is_default)});
		if(cursor.getCount()==1){
			cursor.moveToFirst();
			int sequence = cursor.getInt(3);
			SQLiteDatabase dbw = sqlite.getWritableDatabase();
			dbw.execSQL("delete from FavoriteList where list_id=? and is_default=?", 
				new String[]{Integer.toString(list_id),Integer.toString(is_default)});
			dbw.execSQL("update FavoriteList set sequence=sequence-1 where sequence>?", new String[]{Integer.toString(sequence)});
			dbw.close();
		}
	}
	//移除收藏
	public void removeFavorite(int position, List<ItemHolder> list){
		removeFavorite(((list.get(position).isDefault)?1:0),(list.get(position).ID));
	}
	//判断是否为收藏
	public boolean isFavorite(int position, List<ItemHolder> list){
		return sqlite.getReadableDatabase().rawQuery("select * from FavoriteList where list_id=? and is_default=?", 
				new String[]{Integer.toString(list.get(position).ID),Integer.toString((list.get(position).isDefault)?1:0)}).getCount()!=0;
	}
	//从文本更新默认列表
	public void updateList(String listXML){
		try {
			OutputStream os = openFileOutput("list.xml",MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(listXML);
			osw.close();
			os.close();
		} catch (Exception e) {}
	}
	//判断是否连接上网络
	private boolean isConnected(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)||(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED);
	}
	//重置所有列表（完整）
	public void initDefaultList(){
		defaultList.clear();
		list.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {}
        try {
        	NamedNodeMap attributes;
        	Document doc = builder.parse(this.openFileInput("list.xml"));
			ListVersionCode = Integer.parseInt(doc.getFirstChild().getAttributes().getNamedItem("c").getNodeValue());
			ListVersionName = doc.getFirstChild().getAttributes().getNamedItem("n").getNodeValue();
	        NodeList nl = doc.getElementsByTagName("e");
	        for(int i=0;i<nl.getLength();i++){
	        	ItemHolder item = new ItemHolder();
            	attributes = nl.item(i).getAttributes();
	        	item.ID = Integer.parseInt(attributes.getNamedItem("i").getNodeValue());
	        	item.Number = attributes.getNamedItem("m").getNodeValue().split(",");
	        	if(attributes.getNamedItem("s")!=null)item.SMS = attributes.getNamedItem("s").getNodeValue().split(",");
	        	item.Name = attributes.getNamedItem("n").getNodeValue();
	        	item.isDefault = true;
	        	if(attributes.getNamedItem("d")==null){
	        		List<HashMap<String,String>> MenuList = new ArrayList<HashMap<String,String>>();
	        		NodeList row = nl.item(i).getChildNodes();
	                for(int j=0;j<row.getLength();j++){
	                	if(row.item(j).getNodeType() != Node.ELEMENT_NODE)continue;
	                	attributes = row.item(j).getAttributes();
	                	HashMap<String,String> map = new HashMap<String,String>();
	                	map.put("MenuName", (attributes.getNamedItem("n")) != null?attributes.getNamedItem("n").getNodeValue():"");
	                	map.put("MenuPrice", (attributes.getNamedItem("p")) != null?attributes.getNamedItem("p").getNodeValue():"");
	                	map.put("MenuTitle", (attributes.getNamedItem("t")) != null?attributes.getNamedItem("t").getNodeValue():"");
	                	MenuList.add(map);
	                }
	                item.MenuList = MenuList;
	        	} else item.Detail = attributes.getNamedItem("d").getNodeValue();
	        	defaultList.add(item);
	        	list.add(item);
	    		editor.putInt("ListVersionCode", ListVersionCode).commit();
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ListView lv = (ListView) findViewById(R.id.listView);
		addFavoriteList();
		lv.setAdapter(new ItemAdapter(this,list));
	}
	//显示等待框（更新用）
	public void showProgressDialog(String title, String message){
		updateDialog = new ProgressDialog(CommonActivity.this);
		updateDialog.setTitle(title);
		updateDialog.setMessage(message);
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.show();
	}
}
