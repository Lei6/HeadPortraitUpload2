package com.demo.headportraitupload.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToastUtils {
    public static Toast mToast;
    /**
     * 传入文字
     * */
    public static void show(Context context , String text){

        if (mToast == null){
            mToast = Toast.makeText( context, text , Toast.LENGTH_SHORT);
        }else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast.setText(text);
        }
        mToast.show();
    }

}