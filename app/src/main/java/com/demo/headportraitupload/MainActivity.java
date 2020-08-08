package com.demo.headportraitupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.demo.headportraitupload.utils.GlideUtils;
import com.demo.headportraitupload.utils.PhotoSelectUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView userHead;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        userHead = findViewById(R.id.user_head);
        userHead.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        modifyTouXiang();
    }

    private void modifyTouXiang() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow, null);
        TextView btnCarema = (TextView) view.findViewById(R.id.btn_camera);
        TextView btnPhoto = (TextView) view.findViewById(R.id.btn_photo);
        TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //popupWindow在弹窗的时候背景半透明
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1.0f;
                getWindow().setAttributes(params);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btnCarema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if (i == PackageManager.PERMISSION_GRANTED) {
                    PhotoSelectUtil.get().goToCamera(MainActivity.this);
                } else {
                    //没有权限，申请权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
                }
                popupWindow.dismiss();
            }
        });
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoSelectUtil.get().goToPhoto(MainActivity.this);
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CAMERA_REQUEST_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //允许权限
                PhotoSelectUtil.get().goToCamera(MainActivity.this);
            }else {
                //拒绝权限

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoSelectUtil.get().onActivityResult(MainActivity.this, requestCode, resultCode, data, new PhotoSelectUtil.OnResultListener() {
            @Override////拍照回调
            public void takePhotoFinish(String path) {
                GlideUtils.loadImageViewCircleImg(MainActivity.this, path, userHead, R.mipmap.ic_launcher);
//                PhotoSelectUtil.get().cropPicture(MainActivity.this, path, 1, 1);
            }

            @Override////相册回调
            public void selectPictureFinish(String path) {
                GlideUtils.loadImageViewCircleImg(MainActivity.this, path, userHead, R.mipmap.ic_launcher);
//                PhotoSelectUtil.get().cropPicture(MainActivity.this, path, 1, 1);
            }

            @Override//裁图
            public void cropPictureFinish(String path) {
                GlideUtils.loadImageViewCircleImg(MainActivity.this, path, userHead, R.mipmap.ic_launcher);
            }
        });

    }
}
