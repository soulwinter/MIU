package com.example.myapplication;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddTag extends AppCompatActivity {

    private float pointX, pointY;
    private int areaId, userId;
    private AddTag.MyHandler handler1;
    public static int ALBUM_RESULT_CODE = 0x999 ;
    private String imagePath;
    private int tag_num = 0; //记录tag总数，并根据数量生成默认tag名称


    ImageView add_image;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ImageView imageView = (ImageView)findViewById(R.id.head_image);
            imageView.setImageBitmap((Bitmap)msg.obj);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        // 初始化：获取传过来的x和y(和area)
        Intent get_intent = getIntent();
        pointX = get_intent.getFloatExtra("pointX", pointX);
        pointY = get_intent.getFloatExtra("pointY", pointY);
        userId = get_intent.getIntExtra("userId", userId);
        areaId = get_intent.getIntExtra("areaId", areaId);
        System.out.println("初始化：x="+pointX+" y="+pointY+" area="+areaId+" user="+userId);

        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //向用户请求权限
        if (ActivityCompat.checkSelfPermission(AddTag.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddTag.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        // 更新标记点描述图片,并记录新图片的path
        add_image = (ImageView) findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSysAlbum();
            }
        });

        handler1 = new MyHandler();
        // 提交按钮:向服务器上传tag的名字、描述信息、图片
        Button submit = (Button) findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String tag_name = "tag_"+tag_num;
                        tag_num++;
                        String tag_description = "";

                        EditText edit_tag_name = (EditText) findViewById(R.id.edit_tag_name);
                        String temp_tag_name = edit_tag_name.getText().toString();
                        if(temp_tag_name!="") tag_name = temp_tag_name;

                        EditText edit_tag_description = (EditText) findViewById(R.id.edit_description);
                        tag_description = edit_tag_description.getText().toString();

                        System.out.println("onClick: tag_name="+tag_name+" describ="+tag_description);

                        File image_file = null;

                        if (imagePath != null)
                            image_file = new File(imagePath);

                        System.out.println("imagepath="+image_file);

                        Map<String, Object> paramsMap = new HashMap<String, Object>();
                        paramsMap.put("x", pointX);
                        paramsMap.put("y", pointY);
                        paramsMap.put("userId", userId);
                        paramsMap.put("areaId", userId);
                        paramsMap.put("image", image_file);
                        paramsMap.put("tagName", tag_name);
                        paramsMap.put("tagDescription", tag_description);
                        httpMethod("http://114.116.234.63:8080/tag/addTag", paramsMap);

                    }
                }).start();

            }
        });
    }

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
        displayImageInView(imagePath);
        System.out.println(imagePath);
    }

    //重载onActivityResult方法，获取相应数据
    // 与handleImageOnKitKat相对应，处理其结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleImageOnKitKat(data);
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
    private void displayImageInView(String imagePath) {

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        add_image.setImageBitmap(bitmap);

        this.imagePath = imagePath;

    }


    public void httpMethod(String url, Map<String, Object> paramsMap) {
        // 创建client对象 创建调用的工厂类 具备了访问http的能力
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS) // 设置超时时间
                .readTimeout(60, TimeUnit.SECONDS) // 设置读取超时时间
                .writeTimeout(60, TimeUnit.SECONDS) // 设置写入超时时间
                .build();

        // 添加请求类型
        MultipartBody.Builder builder = new MultipartBody.Builder();
//        builder.setType(MediaType.parse("multipart/form-data"));
        builder.setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")));

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
                Toast.makeText(AddTag.this, "添加标记成功！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else{
                Looper.prepare();
                Toast.makeText(AddTag.this, "添加标记失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}