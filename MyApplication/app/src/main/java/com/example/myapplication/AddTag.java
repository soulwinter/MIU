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
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AddTag extends AppCompatActivity {

    private float pointX, pointY;
    private int areaId;
    private AddTag.MyHandler handler1;
    public static int ALBUM_RESULT_CODE = 0x999 ;
    private String imagePath;
    private int image_num = 0;  // 记录应该在第几个image添加图像

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
        pointX = get_intent.getFloatExtra("pointY", pointY);
        areaId = 0;

        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //向用户请求权限
        if (ActivityCompat.checkSelfPermission(AddTag.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddTag.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        // 更新标记点描述图片 TODO 上传到服务器
        add_image = (ImageView) findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSysAlbum();
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
        // TODO 把获取的相册照片放在view中
        add_image.setImageBitmap(bitmap);

        this.imagePath = imagePath;

    }


}