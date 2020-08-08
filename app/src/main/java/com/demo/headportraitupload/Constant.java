package com.demo.headportraitupload;

import android.os.Environment;

import java.io.File;

public class Constant {

    public static final String PROJECT_NAME = "app";
    public static final String BASE_FILE_CACHE_URL = Environment.getExternalStorageDirectory().toString() + File.separator + PROJECT_NAME;
    public static final String IMAGE_CACHE = BASE_FILE_CACHE_URL + File.separator + "image" + File.separator + "temp";   //图片缓存路径

}
