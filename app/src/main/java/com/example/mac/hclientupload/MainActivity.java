package com.example.mac.hclientupload;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<File> files;
    private Map<String, String> params1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                HClientPostFile();
//                    okPostFile();
                    Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                    MainActivity.this.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"IO异常",Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }

            }
        });
    }
    private void okPostFile() {
        new AsyncTask<Map<String,String>, Void, String>() {

            @Override
            protected String doInBackground(Map<String,String>... params) {
                String result = null;
                if (params1 == null){
                    params1 = new HashMap<String, String>();
                }
                params1.put("method", "upload");
                try {
                    File file=new File(Environment.getExternalStorageDirectory(),"qyqm.jpg");
                    File file2=new File(Environment.getExternalStorageDirectory(),"gmovie.txt");
                    OkHttpUtils.post()//
                            .addFile("mFile", "qyqm.jpg", file)//
                            .addFile("mFile", "gmovie.txt", file2)//
                            .url(FileUpload.RequestURL1)
                            .params(params1)//
//                                        .headers(headers)//
                            .build()//
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                if (FileUpload.SUCCESS == s)
                    Toast.makeText(MainActivity.this, "上传成功!", Toast.LENGTH_SHORT)
                            .show();
                else
                    Toast.makeText(MainActivity.this,"上传失败!",Toast.LENGTH_SHORT)
                            .show();
                super.onPostExecute(s);
            }
        }.execute(params1);
    }

    private void HClientPostFile() {
        files=new ArrayList<File>();
        params1=new HashMap<String, String>();
        files.clear();
        params1.clear();
        File file=new File(Environment.getExternalStorageDirectory(),"qyqm.jpg");
        File file2=new File(Environment.getExternalStorageDirectory(),"gmovie.txt");
        File file3=new File(Environment.getExternalStorageDirectory(),"wxcj.jpg");
        files.add(file);
        files.add(file2);
        files.add(file3);
        StringBuffer sbFileTypes=new StringBuffer();
        for (File tempFile:files) {
            String fileName=tempFile.getName();
            sbFileTypes.append(getFileType(fileName));
        }
        params1.put("fileTypes",sbFileTypes.toString());
        params1.put("method", "upload");
    }

    /**
     * 获取文件的类型
     * @param fileName ：文件名
     * @return 文件类型
     */
    private String getFileType(String fileName) {
        // TODO Auto-generated method stub
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
