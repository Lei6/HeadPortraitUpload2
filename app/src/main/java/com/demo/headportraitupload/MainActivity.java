package com.demo.headportraitupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
                PhotoSelectUtil.get().goToCamera(MainActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoSelectUtil.get().onActivityResult(MainActivity.this, requestCode, resultCode, data, new PhotoSelectUtil.OnResultListener() {
            @Override////拍照回调
            public void takePhotoFinish(String path) {
                PhotoSelectUtil.get().cropPicture(MainActivity.this, path, 1, 1);
            }

            @Override////相册回调
            public void selectPictureFinish(String path) {
                PhotoSelectUtil.get().cropPicture(MainActivity.this, path, 1, 1);
            }

            @Override//裁图
            public void cropPictureFinish(String path) {
                Log.e("TAG", "cropPictureFinish: "+path );
                GlideUtils.loadImageViewCircleImg(MainActivity.this, path, userHead, R.mipmap.ic_launcher);
            }
        });

    }
}
