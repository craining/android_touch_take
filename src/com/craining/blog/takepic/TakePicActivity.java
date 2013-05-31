package com.craining.blog.takepic;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.craining.blog.takepic.db.DataBaseAdapter;

public class TakePicActivity extends Activity {

	private File getAlbumPath = null;
	private ArrayList<String> array_allAlbumPaths = null;
	private ArrayList<String> array_allWidgetId = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.layout_main);
		Bundle b = getIntent().getExtras();
		int getWidgetId = b.getInt(TakePicOpers.TAG_WIDGETID);

		// Log.e("getTouchedId", Integer.toString(getWidgetId));
		if (getWidgetId >= 0) {
			getDBInfo();
			for (int i = 0; i < array_allWidgetId.size(); i++) {
				if (Integer.toString(getWidgetId).equals(array_allWidgetId.get(i))) {
					getAlbumPath = new File(array_allAlbumPaths.get(i));
				}
			}
			if (getAlbumPath == null) {
				Toast.makeText(TakePicActivity.this, R.string.str_toast_error_null, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				jumpCamera();
			}

		} else {
			finish();
		}
	}

	protected void getDBInfo() {
		// 查询数据库，得到path
		DataBaseAdapter db = new DataBaseAdapter(TakePicActivity.this);
		db.open();
		array_allAlbumPaths = DataBaseAdapter.getColumnThingsInf(db, DataBaseAdapter.KEY_ALBUMPATH);
		array_allWidgetId = DataBaseAdapter.getColumnThingsInf(db, DataBaseAdapter.KEY_WIDGETID);
		db.close();

		for (int i = 0; i < array_allWidgetId.size(); i++) {
			Log.e(array_allWidgetId.get(i), array_allAlbumPaths.get(i));
		}
	}

	private void jumpCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri picPath = data.getData();
			if (selectMoveService(picPath)) {
				jumpCamera();// 继续拍照
			} else {
				Toast.makeText(TakePicActivity.this, R.string.toast_str_cannotmv, Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
//			Toast.makeText(TakePicActivity.this, R.string.toast_str_cannotmv, Toast.LENGTH_SHORT).show();
			finish();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 选择一后台服务进行转存，本程序设置了两个服务分担，如果一个正在转存中，另一个可以接替下一个任务
	 * 
	 * @param picPath
	 */
	private boolean  selectMoveService(Uri picPath) {
		if (!TakePicOpers.serviceIsRunning(TakePicActivity.this, TakePicOpers.SERVICE_NAME_ONE)) {
			Intent i = new Intent();
			i.setClass(TakePicActivity.this, TakePicServiceOne.class);
			Bundle b = new Bundle();
			b.putString(TakePicOpers.TAG_FILESCANNER_DES, getAlbumPath.toString());
			b.putString(TakePicOpers.TAG_FILESCANNER_SRC, getImageAbsolutePath(picPath));
			i.putExtras(b);
			startService( i );// 开启服务进行转存，
		} else if (!TakePicOpers.serviceIsRunning(TakePicActivity.this, TakePicOpers.SERVICE_NAME_TWO)) {
			Intent i = new Intent();
			i.setClass(TakePicActivity.this, TakePicServiceTwo.class);
			Bundle b = new Bundle();
			b.putString(TakePicOpers.TAG_FILESCANNER_DES, getAlbumPath.toString());
			b.putString(TakePicOpers.TAG_FILESCANNER_SRC, getImageAbsolutePath(picPath));
			i.putExtras(b);
			startService( i );// 开启服务进行转存，
		} else {
			return false;
		}
		return true;
	}

	protected String getImageAbsolutePath(Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}