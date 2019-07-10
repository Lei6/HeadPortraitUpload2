package com.demo.headportraitupload.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;


public class GlideUtils {


    /**
     * 加载位圆形图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void loadImageViewCircleImg(final Context context, String url, final ImageView imageView, int defaultImg) {
//        Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop()).placeholder(defaultImg).error(defaultImg).centerCrop()).into(imageView);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .placeholder(defaultImg)
                .error(defaultImg);
        Glide.with(context).asBitmap().load(url).apply(requestOptions).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }


}
