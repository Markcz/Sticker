package com.example.chenzhen.sticker;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    FrameLayout root;

    StickerView stickerView;

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

        stickerView = new StickerView(this);
        stickerView.setFocusable(true);
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
            }

            @Override
            public void onTabClick(StickerView stickerView) {
                Toast.makeText(MainActivity.this,"--onTabClick--",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void removeSticker(View view) {
        root.removeView(stickerView);
    }
}
