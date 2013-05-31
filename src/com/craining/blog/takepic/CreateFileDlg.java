package com.craining.blog.takepic;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.craining.blog.takepic.db.DataBaseAdapter;

public class CreateFileDlg extends Activity{
	
	private Button btn_createOk = null;
	private Button btn_createCancle = null;
	private EditText eidt_fileName = null;
	private CheckBox check_asAlbum = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_createfile);
		setTitle(getString(R.string.str_btn_createdir));
		btn_createOk = (Button) findViewById(R.id.btn_createok);
		btn_createCancle = (Button) findViewById(R.id.btn_creatcancle);
		eidt_fileName = (EditText) findViewById(R.id.edit_inputfile);
		check_asAlbum = (CheckBox) findViewById(R.id.check_asalbum);
		
		btn_createOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//创建文件夹
				String fileName = eidt_fileName.getText().toString();
				if(TextUtils.isEmpty(fileName)) {
					Toast.makeText(CreateFileDlg.this, R.string.str_dlg_edithint_inputfilename, Toast.LENGTH_SHORT);
				} else {
					String fileDir = ExplorerActivity.nowPath  + "/" + fileName;
					File toCreateDir = new File( fileDir );
					
					boolean createSuccess = true;
					try {
						toCreateDir.mkdir();
						Toast.makeText(CreateFileDlg.this, R.string.str_dlg_toast_createfilesuccess, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						createSuccess = false;
						Toast.makeText(CreateFileDlg.this, R.string.str_dlg_toast_createfilefail, Toast.LENGTH_SHORT).show();
						Log.e("", "File create fail");
					}finally {
						Intent resultValue = new Intent();
						if(createSuccess) {//如果添加成功
							if(check_asAlbum.isChecked()) {
								//添加此文件夹作为相册
								DataBaseAdapter dbAdapter = new DataBaseAdapter(CreateFileDlg.this);
								dbAdapter.open();
								dbAdapter.insertData(fileDir, ExplorerActivity.getWidgetId);
								dbAdapter.close();
								Log.e("insertData in createactivity", fileDir + "===" + ExplorerActivity.getWidgetId);
								Uri data = Uri.parse(fileName);   
								Intent result = new Intent(null, data);   
								setResult(RESULT_CANCELED, result);//返回为cancel
							} else {
								setResult(RESULT_OK, resultValue);//返回为ok
							}
						} else {
							setResult(2, resultValue);
						}
					
						finish();
					}
				}
			}
		});
		btn_createCancle.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//取消
				Intent resultValue = new Intent();
				setResult(2, resultValue);
				finish();
			}
		});
	}


}
