package qianfeng.a9_3camera;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    private File file;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = ((ImageView) findViewById(R.id.iv));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bitmap);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            iv.setImageBitmap(bitmap);
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            // 注意路径，和DESC前面的空格
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

            if(cursor.moveToFirst())
            {
                // 数据库里面的_data字段就是 图片的路径，在定义中，是用DATA常量记录了这个字段,所以
                Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                iv.setImageBitmap(bitmap);
            }

            cursor.close();

        }
    }

    public void takePhoto1(View view) {
        // 拍照方式一,基本不使用，只是返回一张缩略图，没什么用的
        //  拍摄照片时如果不指定照片存储位置，则系统在onActivityResult方法中返回一张照片的缩略图
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);

    }

    public void takePhoto2(View view) {
        // 拍照方式二
        // 自己指定Uri，指定存储到SD卡的哪个目录，但是指定Uri的方式只能有一种，就是Uri.fromFile()，用Uri.parse()会报错。
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dateFormat.format(new Date()) + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 这里指定存储位置，在Extra中是固定的字符串EXTRA_OUTPUT, 用这种方式的话，在外部存储的download目录下，会有新拍的照片.然后应用再从这个路径中，把那张照片读出来。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        startActivityForResult(intent, 2);
    }

    public void takePhoto3(View view) {
        // 拍照方式三,适用范围最广。
        // 第三种是最常用的，并且是能用于任何安卓机型的！
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, dateFormat.format(new Date()) + ".jpg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 3);
    }
}
