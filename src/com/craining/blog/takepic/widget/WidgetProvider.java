package com.craining.blog.takepic.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.craining.blog.takepic.R;
import com.craining.blog.takepic.TakePicActivity;
import com.craining.blog.takepic.TakePicOpers;
import com.craining.blog.takepic.db.DataBaseAdapter;

public class WidgetProvider extends AppWidgetProvider {
	/** Called when the activity is first created. */

	// public static int widgetClickedId = -1;

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			Log.i("deleteId", "this is [" + appWidgetId + "] onDelete!");
			// 从数据库中删除
			DataBaseAdapter db = new DataBaseAdapter((context));
			db.open();
			db.deleteOnePath(Integer.toString(appWidgetId));
			db.close();
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			Log.i("myLog", "this is [" + appWidgetId + "] onUpdate!");

		}
//		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	 
	/**
	 * 更新一个Widget
	 * 
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetId
	 */
	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId, String title) {
		Log.e("WidgetProvider", "UpdateAppWidget Methord is Running");
		/* 构建RemoteViews对象来对桌面部件进行更新 */
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.layout_widget);
		views.setTextViewText(R.id.text_widget, title);
		
		Intent intent0 = new Intent(context, TakePicActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TakePicOpers.TAG_WIDGETID, appWidgetId);
		intent0.putExtras(bundle);
		//此处切记！ 不要把requestCode设置为一个定值，否则，bundle传送的值总是最新添加的widgetID
		PendingIntent pendingIntent0 = PendingIntent.getActivity(context, appWidgetId,
				intent0, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.text_widget, pendingIntent0);
		views.setOnClickPendingIntent(R.id.img_widget, pendingIntent0);
		
		appWidgetManager.updateAppWidget(appWidgetId, views);
		
	}

}
