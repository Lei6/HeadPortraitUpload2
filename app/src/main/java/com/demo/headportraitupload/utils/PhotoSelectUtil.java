package com.demo.headportraitupload.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.demo.headportraitupload.FileUtil;
import com.demo.headportraitupload.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoSelectUtil {

    private String TAG = PhotoSelectUtil.class.getSimpleName();

    private static PhotoSelectUtil instance;

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //相机拍照默认存储路径
    public String DATE = "";
    //截图图片名
    private String crop_image;

    public static final String APP_NAME = "fresh";
    private String imagePath;

    private Uri imgUri;

    private Boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

    public static PhotoSelectUtil get() {
        if (instance == null) {
            synchronized (PhotoSelectUtil.class) {
                if (instance == null) instance = new PhotoSelectUtil();
            }
        }
        return instance;
    }

    public void goToCamera(Activity context) {
        Log.e(TAG, "goToCamera: 打开系统相机" );
        //创建拍照储存的图片文件
        DATE = new SimpleDateFormat("yyyy_MMdd_hhmmss").format(new Date());
        if (isSdCardExist()) {
            Uri contentUri = null;
            imagePath = FileUtil.createImagePath(APP_NAME + DATE);
            File file = new File(imagePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //跳转到系统相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (isAndroidQ){
                //Android 10.0
                contentUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //通过FileProvider创建一个content类型的Uri
                 contentUri = FileUtil.getFileUri(context, file);
            } else {
                contentUri = Uri.fromFile(file);
            }
            try {
                imgUri = contentUri;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                context.startActivityForResult(intent, REQUEST_CAPTURE);
            } catch (ActivityNotFoundException anf) {
                ToastUtils.show(context, context.getResources().getString(R.string.camera_not_prepared));
            }
        } else {
            ToastUtils.show(context, context.getResources().getString(R.string.sdcard_no_exist));
        }

    }

    public void goToPhoto(Activity activity) {
        Log.e(TAG, "goToPhoto: 打开系统相册" );
        DATE = new SimpleDateFormat("yyyy_MMdd_hhmmss").format(new Date());
        if (isSdCardExist()) {
            Intent intent;
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
            } else {
                intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
            activity.startActivityForResult(intent, REQUEST_PICK);
        }else {
            ToastUtils.show(activity,activity.getResources().getString(R.string.sdcard_no_exist));
        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode
            , Intent data, OnResultListener listener) {

        switch (requestCode) {
            case REQUEST_CAPTURE://相机
              if (isAndroidQ){
               listener.takePhotoFinish(imgUri+"");
              }else {
                  if (!TextUtils.isEmpty(imagePath)) {
                      File file = new File(imagePath);
                      if (file.isFile() && listener != null)
                          listener.takePhotoFinish(imagePath);
                  }
              }
                break;
            case REQUEST_PICK://相册
                if (data!=null){
                    Uri uri = data.getData();
                    if (uri != null) {
                      if (isAndroidQ){
                          listener.selectPictureFinish(uri+"");
                      }else {
                          String cropImagePath = FileUtil.getRealFilePathFromUri(activity.getApplicationContext(), uri);
                          File file = new File(cropImagePath);
                          if (file.isFile() && listener != null){
                              listener.selectPictureFinish(cropImagePath);
                          }
                      }
                    }
                }
                break;
            case REQUEST_CROP_PHOTO:
                if (!TextUtils.isEmpty(crop_image)) {
                    File file = new File(crop_image);
                    if (file.isFile() && listener != null) {
                        listener.cropPictureFinish(crop_image);
                    }
                    // 最后通知图库更新
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,FileUtil.getFileUri(activity,file)));// Uri.fromFile(file)));
                }
                break;
        }

    }

    public interface OnResultListener {
        //拍照回调
        void takePhotoFinish(String path);

        //选择图片回调
        void selectPictureFinish(String path);

        //截图回调
        void cropPictureFinish(String path);
    }

    /**
     * 检查SD卡是否存在
     */
    public boolean isSdCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 调用系统剪裁功能
     */
    public void cropPicture(Activity activity, String path, int scaleX, int scaleY)
    {
        Log.e(TAG, "onActivityResult: .....裁图" );
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri;
        Uri outputUri;
        crop_image = FileUtil.createImagePath(APP_NAME + "_crop_" + DATE);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileUtil.getFileUri(activity,file);
//            imageUri = FileProvider.getUriForFile(activity, FILE_CONTENT_FILEPROVIDER, file);
            outputUri = Uri.fromFile(new File(crop_image));
            //TODO:outputUri不需要ContentUri,否则失败
            //outputUri = FileProvider.getUriForFile(activity, "com.solux.furniture.fileprovider", new File(crop_image));
        }else{
            imageUri = Uri.fromFile(file);
            outputUri = Uri.fromFile(new File(crop_image));
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        //设置宽高比例
        intent.putExtra("aspectX", scaleX);
        intent.putExtra("aspectY", scaleY);
        //设置裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }


}
