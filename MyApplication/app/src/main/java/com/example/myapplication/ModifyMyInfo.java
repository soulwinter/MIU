package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.entity.User;
import com.example.myapplication.fragments.UserFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyMyInfo extends AppCompatActivity {

    private MyHandler handler1;
    private User user = null;
    private ImageView imageView;
    private String imagePath;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ImageView imageView = (ImageView)findViewById(R.id.head_image);
            imageView.setImageBitmap((Bitmap)msg.obj);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_my_info);

        //这一两行代码主要是向用户请求权限
        if (ActivityCompat.checkSelfPermission(ModifyMyInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ModifyMyInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        //初始化界面的值
        user = (User) getIntent().getSerializableExtra("user");

        TextView topView = (TextView) findViewById(R.id.top_view);
        topView.setText(user.getUsername());

        EditText username_edit = (EditText) findViewById(R.id.username_edit);
        username_edit.setText(user.getUsername());

        EditText description_edit = (EditText) findViewById(R.id.description_edit);
        description_edit.setText(user.getDescription());

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_group);
        if (user.getGender()!=null){
            if (user.getGender()){
                radioGroup.check(R.id.rb_option1);
            }else{
                radioGroup.check(R.id.rb_option2);
            }
        }
        CheckBox checkBox = (CheckBox) findViewById(R.id.ifShare_checkbox);
        if (user.getIfShare()!=null && user.getIfShare()){
            checkBox.setChecked(true);
        }

        handler1 = new MyHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String path = "http://114.116.234.63:8080/image" + user.getPhotoPath();
                    //2:把网址封装为一个URL对象
                    URL url = new URL(path);
                    //3:获取客户端和服务器的连接对象，此时还没有建立连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //4:初始化连接对象
                    conn.setRequestMethod("GET");
                    //设置连接超时
                    conn.setConnectTimeout(8000);
                    //设置读取超时
                    conn.setReadTimeout(8000);
                    //5:发生请求，与服务器建立连接
                    conn.connect();
                    //如果响应码为200，说明请求成功
                    if (conn.getResponseCode() == 200) {
                        //获取服务器响应头中的流
                        InputStream is = conn.getInputStream();
                        //读取流里的数据，构建成bitmap位图
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        msg.obj = bm;
                        handler1.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        bindClick();



    }

    private void bindClick(){
        imageView = (ImageView) findViewById(R.id.head_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get(view);
            }
        });

        Button submit = (Button) findViewById(R.id.submit_info_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int id = user.getId();
                        String username = "";
                        String description = "";
                        int gender = 0;
                        int ifShare = 0;

                        EditText username_edit = (EditText) findViewById(R.id.username_edit);
                        username = username_edit.getText().toString();

                        EditText description_edit = (EditText) findViewById(R.id.description_edit);
                        description = description_edit.getText().toString();

                        RadioButton radioButton = (RadioButton) findViewById(R.id.rb_option1);
                        if (radioButton.isChecked()){
                            gender = 1;
                        }

                        CheckBox checkBox = (CheckBox) findViewById(R.id.ifShare_checkbox);
                        if (checkBox.isChecked()){
                            ifShare = 1;
                        }

                        File file = null;

                        if (imagePath != null)
                            file = new File(imagePath);
                        // 请求参数
                        Map<String, Object> paramsMap = new HashMap<String, Object>();
                        paramsMap.put("image", file);
                        paramsMap.put("username", username);
                        paramsMap.put("gender", gender);
                        paramsMap.put("ifShare", ifShare);
                        paramsMap.put("description", description);
                        paramsMap.put("id", id);
                        httpMethod("http://114.116.234.63:8080/user/updateInfo", paramsMap);

                    }
                }).start();

            }
        });
    }


    //相应点击事件
    public void get(View v){
        openSysAlbum();
    }

    //重载onActivityResult方法，获取相应数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleImageOnKitKat(data);
    }

    public static int ALBUM_RESULT_CODE = 0x999 ;

    /**
     * 打开系统相册
     * 定义Intent跳转到特定图库的Uri下挑选，然后将挑选结果返回给Activity
     * */
    private void openSysAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_RESULT_CODE);
    }

    //这部分的代码目前没有理解，只知道作用是根据条件的不同去获取相册中图片的url
    //这一部分是从其他博客中查询的
    @TargetApi(value = 19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
        System.out.println(imagePath);
    }

    /**获取图片的路径*/
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){

            if(cursor.moveToFirst()){
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cursor.getString(index);
            }
            cursor.close();
        }
        return path;
    }


    /**展示图片*/
    private void displayImage(String imagePath) {

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        this.imagePath = imagePath;





    }

    public  void httpMethod(String url, Map<String, Object> paramsMap) {
        // 创建client对象 创建调用的工厂类 具备了访问http的能力
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS) // 设置超时时间
                .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(60, TimeUnit.SECONDS) // 设置写入超时时间
                .build();

        // 添加请求类型
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MediaType.parse("multipart/form-data"));

        //  创建请求的请求体
        for (String key : paramsMap.keySet()) {
            // 追加表单信息
            Object object = paramsMap.get(key);
            if (key.equals("image")) {
                if (object != null){
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(file, null));
                }
            } else {
                builder.addFormDataPart(key, object.toString());
            }
        }
        RequestBody body = builder.build();

        // 创建request, 表单提交
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 创建一个通信请求
        try (Response response = client.newCall(request).execute()) {
            // 尝试将返回值转换成字符串并返回
            if (response.code() == 200){
                Looper.prepare();
                Toast.makeText(ModifyMyInfo.this, "修改个人信息成功！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else{
                Looper.prepare();
                Toast.makeText(ModifyMyInfo.this, "修改个人信息失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}