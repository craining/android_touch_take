package com.craining.blog.takepic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class TakePicOpers {

	public static final String[] AUTHOR_EMAIL_ADDRESS = {"craining@163.com"};
	public static final String AUTHOR_EMAIL_SUB = "TouchTake-1.0";
	
	public static File TAG_FILE = new File("/data/data/com.craining.blog.takepic/tag");
	public static String TAG_WIDGETID = "wigetid";
	public static String TAG_WIDGETTITLE = "widgettitle";

	public static String TAG_FILESCANNER_SRC = "srcfile";
	public static String TAG_FILESCANNER_DES = "desfile";

	public static final String SERVICE_NAME_ONE = "com.craining.blog.takepic.TakePicServiceOne";
	public static final String SERVICE_NAME_TWO = "com.craining.blog.takepic.TakePicServiceTwo";

	/**
	 * 判断存储卡是否可用
	 * 
	 * @return
	 */
	public static boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 拷贝一个文件,srcFile源文件，destFile目标文件
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFileTo(File srcFile, File destFile) throws IOException {
		if (srcFile.isDirectory() || destFile.isDirectory()) {
			return false;// 判断是否是文件
		}
		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		int readLen = 0;
		byte[] buf = new byte[1024];
		while ((readLen = fis.read(buf)) != -1) {
			fos.write(buf, 0, readLen);
		}
		fos.flush();
		fos.close();
		fis.close();

		return true;
	}

	public static boolean mvToFile(File getImgFile, File mvtoFile) throws IOException {
		if (getImgFile != null && getImgFile.exists()) {
			if (!mvtoFile.exists()) {
				mvtoFile.mkdir();
			}
			if (copyFileTo(getImgFile, new File(mvtoFile.toString() + "/" + getImgFile.getName()))) {
				getImgFile.delete();
			}

		} else {
			return false;
		}

		return true;
	}
	
	/**
	 * 通过Service的类名来判断是否启动某个服务
	 * 
	 * @param mServiceList
	 * @param className
	 * @return
	 */
	public static boolean serviceIsRunning(Context context, String serviceName) {

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = activityManager.getRunningServices(100);

		for (int i = 0; i < mServiceList.size(); i++) {
			if (serviceName.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
}
