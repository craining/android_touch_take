package com.craining.blog.takepic;

/**
 * 文件浏览
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.craining.blog.takepic.db.DataBaseAdapter;
import com.craining.blog.takepic.widget.WidgetCreateActivity;

public class ExplorerActivity extends Activity {
	private ArrayList<String> allFiles = null;
	private String rootPath = "/sdcard";// 将根目录设置为sd卡
	private String parentPath = "/sdcard";
	public static String nowPath = "/sdcard";
	public static String getWidgetId = "0";
	private Button btn_createNew;
	private Button btn_returnRoot;
	private Button btn_returnBack;
	private Button btn_help;
	private ListView listview_file;
	private TextView text_showNowPath;
	private TextView text_showtip;

	@Override
	protected void onCreate(Bundle save) {
		super.onCreate(save);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.file_explorer);
		// 获得传过来的widgetid 用于保存到数据库
		Bundle bundle = this.getIntent().getExtras();
		getWidgetId = bundle.getString(TakePicOpers.TAG_WIDGETID);

		btn_returnRoot = (Button) findViewById(R.id.btn_returnroot);
		btn_returnBack = (Button) findViewById(R.id.btn_returnback);
		btn_help = (Button) findViewById(R.id.btn_help);
		btn_createNew = (Button) findViewById(R.id.btn_createnew);
		text_showNowPath = (TextView) findViewById(R.id.text_nowpath);
		text_showtip = (TextView) findViewById(R.id.text_showtip);
		listview_file = (ListView) findViewById(R.id.file_list);

		TakePicOpers.TAG_FILE.mkdir();

		if (TakePicOpers.isSdPresent()) {
			getFileDir(nowPath);
		} else {
			TakePicOpers.TAG_FILE.delete();
			Toast.makeText(this, R.string.str_toast_sdcardunpresent, Toast.LENGTH_SHORT).show();
			finish();
		}

		listview_file.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				/* 单击此条目，判断文件还是目录并进入 */
				// 是目录
				String prePath = "";
				Log.e("getFileDir", nowPath + "/" + allFiles.get(arg2));
				try {
					prePath = nowPath;
					getFileDir(nowPath + "/" + allFiles.get(arg2));
				} catch (Exception e) {
					nowPath = prePath;
					getFileDir(nowPath);
				}

			}
		});

		listview_file.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				/* 长按此条目进行添加 */
				insertData(nowPath + "/" + allFiles.get(arg2), getWidgetId);
				onExit(arg2);
				return true;
			}

		});
		btn_createNew.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				/* 新建文件夹 */
				startActivityForResult(new Intent(ExplorerActivity.this, CreateFileDlg.class), 0);
			}
		});
		btn_help.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(ExplorerActivity.this, TakePicHelperDlg.class));
			}
		});
		btn_returnRoot.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				getFileDir(rootPath);
			}
		});
		btn_returnBack.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				getFileDir(parentPath);
			}
		});

	}

	/**
	 * 进入目录
	 * 
	 * @param filePath
	 */
	private void getFileDir(String filePath) {
		setProgressBarIndeterminate(true);
		/* 设置目前所在路径 */
		nowPath = filePath;
		text_showNowPath.setText(nowPath);
		File f = new File(filePath);
		File[] files = f.listFiles();

		if (!filePath.equals(rootPath)) {
			btn_returnBack.setEnabled(true);
			btn_returnRoot.setEnabled(true);
			parentPath = f.getParent();
		} else {
			btn_returnRoot.setEnabled(false);
			btn_returnBack.setEnabled(false);
		}
		if (files == null || files.length == 0) {// 空目录
			SimpleAdapter listItemAdapter = new SimpleAdapter(this, new ArrayList<HashMap<String, Object>>(), R.layout.file_row, new String[] { "FileTitle", "FileIcon" }, new int[] { R.id.file_text,
					R.id.file_icon });
			listview_file.setAdapter(listItemAdapter);
		} else {
			allFiles = new ArrayList<String>();
			ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

			/* 将所有文件添加ArrayList中 */
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					allFiles.add(file.getName());
				}
			}

			/* 排序 + 显示 */
			String[] aa = new String[allFiles.size()];
			if (aa.length == 0) {
				text_showtip.setText(getString(R.string.str_text_explorertipempty));
			} else {
				text_showtip.setText(getString(R.string.str_text_explorertipadd));
			}
			allFiles.toArray(aa);
			Arrays.sort(aa);
			allFiles = new ArrayList<String>();
			for (String ee : aa) {
				allFiles.add(ee);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("FileTitle", ee);
				/* 判断是否为空目录， 并添加相应图标 */
				File a = new File(nowPath + "/" + ee);
				File[] childfiles = a.listFiles();
				if (childfiles == null || childfiles.length == 0) {
					map.put("FileIcon", R.drawable.folder_empty);
				} else {
					map.put("FileIcon", R.drawable.folder_full);
				}

				listItem.add(map);
			}

			/* 生成适配器的Item和动态数组对应的元素 */
			SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.file_row, new String[] { "FileTitle", "FileIcon" }, new int[] { R.id.file_text, R.id.file_icon });
			listview_file.setAdapter(listItemAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_menu_help:
			startActivity(new Intent(ExplorerActivity.this, TakePicHelperDlg.class));
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void insertData(String path, String id) {
		DataBaseAdapter dbAdapter = new DataBaseAdapter(ExplorerActivity.this);
		dbAdapter.open();
		dbAdapter.insertData(path, id);
		dbAdapter.close();
		Log.e(path, getWidgetId);
	}

	protected void onExit(int arg2) {

		// 返回到创建widget类中
		Intent i = new Intent();
		i.setClass(ExplorerActivity.this, WidgetCreateActivity.class);
		Bundle b = new Bundle();
		b.putString(TakePicOpers.TAG_WIDGETID, getWidgetId);
		if (arg2 == -1) {
			insertData("/mnt/sdcard/" + getString(R.string.app_name), getWidgetId);
			new File("/mnt/sdcard/" + getString(R.string.app_name)).mkdir();
			b.putString(TakePicOpers.TAG_WIDGETTITLE, getString(R.string.app_name));
		} else {
			b.putString(TakePicOpers.TAG_WIDGETTITLE, allFiles.get(arg2));
		}

		i.putExtras(b);
		startActivityForResult(i, 0);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			getFileDir(nowPath);
		} else if (resultCode == RESULT_CANCELED) {
			Intent i = new Intent();
			i.setClass(ExplorerActivity.this, WidgetCreateActivity.class);
			Bundle b = new Bundle();
			b.putString(TakePicOpers.TAG_WIDGETID, getWidgetId);
			b.putString(TakePicOpers.TAG_WIDGETTITLE, data.getData().toString());
			Log.e("", data.getData().toString());
			i.putExtras(b);
			startActivityForResult(i, 0);
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (nowPath.equals(rootPath)) {
				onExit(-1);
			} else {
				getFileDir(parentPath);
			}
			return false;
		}
		return false;
	}
}
