package com.dcw.qiuzhi;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片加载器
 */

class PictureLoader {

    private ImageView mImageView;
    private String mImgUrl;
    private byte[] mPicByte;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                if (mPicByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray
                            (mPicByte, 0, mPicByte.length);
                    mImageView.setImageBitmap(bitmap);
                }
            }
        }
    };

    void load(ImageView imageView, String imgUrl) {
        this.mImageView = imageView;
        this.mImgUrl = imgUrl;
        Drawable drawable = mImageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(mRunnable).start();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(mImgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if (conn.getResponseCode() == 200) {
                    //请求成功
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    while ((length = is.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, length);
                    }
                    mPicByte = outputStream.toByteArray();
                    is.close();
                    outputStream.close();
                    mHandler.sendEmptyMessage(0x123);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
