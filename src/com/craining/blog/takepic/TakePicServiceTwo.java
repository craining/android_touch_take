package com.craining.blog.takepic;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TakePicServiceTwo extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e("", "tow start");
		Bundle extras = intent.getExtras();
		String srcfile = extras.getString(TakePicOpers.TAG_FILESCANNER_SRC);
		String desfile = extras.getString(TakePicOpers.TAG_FILESCANNER_DES);
		if (!mvPhotoToAlbumAndUpdate(new File(srcfile), new File(desfile))) {
			Toast.makeText(getBaseContext(), R.string.toast_str_cannotmvinservice, Toast.LENGTH_SHORT).show();
		} else {
			stopSelf();
		}

		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.e("", "two destroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean mvPhotoToAlbumAndUpdate(File getImgFile, File mvtoFile) {
		/* 将照片移动到预先目录 */
		try {
//			Log.e("", getImgFile.toString() + "===" + mvtoFile.toString());
			if (TakePicOpers.mvToFile(getImgFile, mvtoFile)) {
				scanSdCard();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * 调用系统api扫描sd卡
	 */

	private void scanSdCard() {
		String sdcardPath = Environment.getExternalStorageDirectory().toString();
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + sdcardPath)));
	}

}
