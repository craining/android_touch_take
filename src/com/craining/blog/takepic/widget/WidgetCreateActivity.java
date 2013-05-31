package com.craining.blog.takepic.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.craining.blog.takepic.ExplorerActivity;
import com.craining.blog.takepic.R;
import com.craining.blog.takepic.TakePicOpers;

public class WidgetCreateActivity extends Activity {

	private int mAppWidgetId;
	private String widgetTitle = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setResult(RESULT_CANCELED);
		if (!TakePicOpers.TAG_FILE.exists()) {
			// Find the widget id from the intent.
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}

			// If they gave us an intent without the widget id, just bail.
			if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
				finish();
			}

			// return OK
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					mAppWidgetId);

			setResult(RESULT_OK, resultValue);

			// bundle传值给Explorer
			Intent i = new Intent();
			i.setClass(WidgetCreateActivity.this, ExplorerActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString(TakePicOpers.TAG_WIDGETID,
					Integer.toString(mAppWidgetId));// 压入数据
			i.putExtras(mBundle);
			startActivityForResult(i, 0);
			finish();

		} else {
			boolean goWell = true;
			int getPremAppWidgetId = 0;
			TakePicOpers.TAG_FILE.delete();
			Bundle b = getIntent().getExtras();
			try {
				getPremAppWidgetId = Integer.parseInt(b
						.getString(TakePicOpers.TAG_WIDGETID));
				widgetTitle = b.getString(TakePicOpers.TAG_WIDGETTITLE);
			} catch (Exception e) {
				Log.e("", "error!!!!");
				goWell = false;
			}
			if (goWell) {
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(WidgetCreateActivity.this);
				WidgetProvider.updateAppWidget(WidgetCreateActivity.this,
						appWidgetManager, getPremAppWidgetId, widgetTitle);
			} else {
				Toast.makeText(WidgetCreateActivity.this,
						R.string.str_toast_error, Toast.LENGTH_SHORT).show();
			}

			finish();
		}

	}
}
