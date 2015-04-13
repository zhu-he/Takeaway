package com.hezhu.takeaway;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public DatabaseHelper(Context context) {
		super(context, "database.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS CustomList(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR," +
				"number VARCHAR," +
				"detail TEXT NOT NULL," +
				"sequence INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS FavoriteList(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"list_id INTEGER," +
				"is_default INTEGER," +
				"sequence INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS CustomList");
		db.execSQL("DROP TABLE IF EXISTS FavoriteList");
		onCreate(db);
	}

}
