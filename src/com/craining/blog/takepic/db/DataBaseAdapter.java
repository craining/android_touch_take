package com.craining.blog.takepic.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseAdapter {

	public static final String KEY_ID = "_id";
	public static final String KEY_WIDGETID = "widgetid";
	public static final String KEY_ALBUMPATH = "paths";
	private static final String DB_NAME = "albumpaths.db";
	private static final String DB_TABLE = "table_album";
	private static final int DB_VERSION = 1;
	private Context mContext = null;
	
	private static final String DB_CREATE = "CREATE TABLE " + DB_TABLE + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY," 
			+ KEY_ALBUMPATH + " TEXT," + KEY_WIDGETID + " TEXT )";
	
	private static SQLiteDatabase mSQLiteDatabase = null;
	private DatabaseHelper mDatabaseHelper = null;

	public DataBaseAdapter(Context context) {
		mContext = context;
	}

	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	public void close() {
		mDatabaseHelper.close();
	}

	/**
	 * 插入一条数据
	 * @param path
	 * @param fileorpath
	 * @return
	 */
	public long insertData(String path, String widgetid) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ALBUMPATH, path);
		initialValues.put(KEY_WIDGETID, widgetid);

		return mSQLiteDatabase.insert(DB_TABLE, KEY_ID, initialValues);
	}

	/**
	 * 根据条目内容，删除一个条目
	 * @param path
	 */
	public void deleteOnePath(String widgetid) {
		String toDelete = "DELETE FROM " + DB_TABLE + " WHERE " + KEY_WIDGETID + " ='" + widgetid + "'";
		mSQLiteDatabase.execSQL(toDelete);
	}

	/**
	 * 读取表格中 column 列的所有数据放到一个List中
	 * 
	 * @param column
	 * @return
	 */
	public static ArrayList<String> getColumnThingsInf(DataBaseAdapter dbadapter, String column) {
		ArrayList<String> getlist = new ArrayList<String>();
		dbadapter.open();
		Cursor findColumDate = mSQLiteDatabase.query(DB_TABLE, new String[] { column },
				null, null, null, null, null);
		findColumDate.moveToFirst();
		final int Index = findColumDate.getColumnIndexOrThrow(column);
		for (findColumDate.moveToFirst(); !findColumDate.isAfterLast(); findColumDate.moveToNext()) {
			String getOneItem = findColumDate.getString(Index);
			getlist.add(getOneItem);
		}
		dbadapter.close();
		return getlist;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			/* 当调用getWritableDatabase()或 getReadableDatabase()方法时 则创建一个数据库*/
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			/* 数据库没有表时创建一个 */
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
}
