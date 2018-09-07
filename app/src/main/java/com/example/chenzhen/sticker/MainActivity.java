package com.example.chenzhen.sticker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity -- ";

    FrameLayout root;

    List<StickerView> mViews = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.fl_root);
    }



    public void addSticker(View view) {
        addSticker();
    }



    void addSticker(){

        StickerView stickerView = new StickerView(this);
        stickerView.setEditing(true);

        mViews.add(stickerView);


        /*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);*/
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        root.addView(stickerView,params);

        stickerView.addSticker(BitmapFactory.decodeResource(getResources(),R.drawable.sticker_01));
        stickerView.setOnStickerListener(new StickerView.SimpleOnStickerListener() {
            @Override
            public void onDeleteClick(StickerView view) {
                root.removeView(view);
                if (mViews.contains(view)){
                    mViews.remove(view);
                }
            }

            @Override
            public void onTabClick(StickerView stickerView) {
                Toast.makeText(MainActivity.this,"--onTabClick--",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void removeSticker(View view) {
        if (mViews.size() > 0){
            StickerView stickerView = mViews.get(mViews.size() - 1);
            if (stickerView != null){
                root.removeView(stickerView);
            }
        }
    }

    public void saveStickers(View view) {
        if (mViews != null){
            if (mViews.size() > 0){
                for (StickerView v : mViews) {
                   v.setEditing(false);
                }
            }
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if (mViews != null){
                    if (mViews.size() > 0){
                        for (StickerView view : mViews) {
                            saveBitmapToSD(createViewBitmap(view));
                            Log.e(TAG, "saveBitmapToSD -");
                        }
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }
        });
    }

   /* Bitmap createViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap tmp = view.getDrawingCache();
        if (tmp == null){
            return null;
        }
        view.setDrawingCacheEnabled(false);
        return tmp;
    }*/

   /**
     * 将不规则Bitmap转为规则矩形
     * @param
     * @return
     * */
    Bitmap createViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        int corner = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap tmp = view.getDrawingCache();
        if (tmp == null){
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(tmp,0, corner, tmp.getWidth(), tmp.getHeight() - 2 * corner);
        view.setDrawingCacheEnabled(false);
        if (bitmap == null){
            return null;
        }
        return bitmap;
    }

    private String saveBitmapToSD(Bitmap bitmap) {
        Log.e(TAG, "saveToSD -");
        if (bitmap == null) {
            return null;
        }
        Log.e(TAG, "save -");
        String savePath = getExternalCacheDir().getPath() + File.separator + "sticker";
        File filePic;
        try {
            filePic = new File(savePath, System.currentTimeMillis() + ".png");
            Log.e(TAG, "new File -");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            Log.e(TAG, "createNewFile -");
            FileOutputStream fos = new FileOutputStream(filePic);
            Log.e(TAG, "FileOutputStream -");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.e(TAG, "compress -");
            fos.flush();
            fos.close();
            Log.e(TAG, "close -");
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return filePic.getPath();
        } catch (IOException e) {
            Log.e(TAG, "IOException -");
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return null;
        }
    }


    MyHandler mHandler;

    static class MyHandler extends Handler {

        public WeakReference<MainActivity> mWRef;

        public MyHandler(MainActivity activity) {
            super(Looper.getMainLooper());
            mWRef = new WeakReference<>(activity);
        }
    }

    public MyHandler getHandler() {
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
        return mHandler;
    }
}
