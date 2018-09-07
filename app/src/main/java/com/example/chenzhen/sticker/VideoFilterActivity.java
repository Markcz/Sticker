//package com.example.chenzhen.sticker;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.FrameLayout;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
//import com.netpower.videocropped.MyStickerView;
//import com.netpower.videocropped.adapter.VideoAddStickerAdapter;
//import com.netpower.videocropped.adapter.VideoFilterAdapter;
//import com.netpower.videocropped.adapter.VideoStickerAdapter;
//import com.netpower.videocropped.model.Cmd;
//import com.netpower.videocropped.model.CmdPicture;
//import com.netpower.videocropped.utils.FileUtil;
//import com.xiaopo.flying.sticker.BitmapStickerIcon;
//import com.xiaopo.flying.sticker.DeleteIconEvent;
//import com.xiaopo.flying.sticker.StickerIconEvent;
//import com.xiaopo.flying.sticker.ZoomIconEvent;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executors;
//
//public class VideoFilterActivity extends EditVideoBasicActivity implements VideoFilterAdapter.OnItemClickListener, View.OnClickListener, AdapterView.OnItemClickListener {
//
//
//    private final static String TAG = "VideoFilterActivity -- ";
//
//    private String outFilePath;
//    private RecyclerView mTypeRecycler;
//    private RecyclerView mAddRecycler;
//    private VideoFilterAdapter mFilterAdapter;
//    private VideoAddStickerAdapter mAddStickerAdapter;
//    private VideoStickerAdapter mVideoStickerAdapter;
//    private List<Map<String, Object>> mList = new ArrayList<>();
//    private RadioGroup mRadioGroup;
//    private LinearLayout mTextLayout;
//    private LinearLayout mStickerLayout;
//    private RelativeLayout mSeekLayout;
//    private GridView mBubbleGrid;
//    private GridView mStickerGrid;
//
//    private StickerView mStickerView;
//    private ImageView mAddButton;
//    private boolean isFirst = true;
//
//
//    Cmd mCmd;
//    MyHandler mHandler;
//
//    static class MyHandler extends Handler {
//
//        public WeakReference<VideoFilterActivity> mWRef;
//
//        public MyHandler(VideoFilterActivity activity) {
//            super(Looper.getMainLooper());
//            mWRef = new WeakReference<>(activity);
//        }
//    }
//
//    public MyHandler getHandler() {
//        if (mHandler == null) {
//            mHandler = new MyHandler(this);
//        }
//        return mHandler;
//    }
//
//
//
//
//    FrameLayout container;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_filter);
//        initView(this);
//        mCmd = new Cmd(videoPath);
//
//        mVideoTrimmer.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap bitmap = mVideoTrimmer.getPositionBitmap(1000);
//
//
//                int swidth = bitmap.getWidth();
//                int sheight = bitmap.getHeight();
//
//                int width = mVideoTrimmer.getWidth();
//                int height = mVideoTrimmer.getHeight();
//
//
//
//                container = new FrameLayout(VideoFilterActivity.this);
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(swidth,sheight);
//                params.gravity = Gravity.CENTER;
//                container.setLayoutParams(params);
//
//
//                Log.e(TAG,"video size --" + swidth + " : " + sheight);
//                Log.e(TAG,"view size --" + width + " : " + height);
//
//            }
//        }, 1000);
//
//    }
//
//    @Override
//    public void initDate() {
//        outFilePath = FileUtil.getFileDir(VideoFilterActivity.this).getAbsolutePath() + "/" + "filter" + ".mp4";
//        File file = new File(outFilePath);
//        if (file.exists()) {
//            file.delete();
//        }
//    }
//
//    @Override
//    protected void addView() {
//        mVideoTrimmer.setVideoInformationVisibility(false);
//        mVideoTrimmer.setRangeSeekBarStyle(2);
//        mTypeRecycler = findViewById(R.id.type_recycler);
//        mAddRecycler = findViewById(R.id.add_sticker_recycler);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mTypeRecycler.setLayoutManager(layoutManager);
//        mAddRecycler.setLayoutManager(new LinearLayoutManager(this));
//        mAddStickerAdapter = new VideoAddStickerAdapter();
//        mAddRecycler.setAdapter(mAddStickerAdapter);
//        mFilterAdapter = new VideoFilterAdapter(getList());
//        mFilterAdapter.setOnItemClickListener(this);
//        mTypeRecycler.setAdapter(mFilterAdapter);
//        mRadioGroup = findViewById(R.id.radio_group);
//        mTextLayout = findViewById(R.id.text_layout);
//        mStickerLayout = findViewById(R.id.sticker_layout);
//        mStickerGrid = findViewById(R.id.sticker_grid);
//        mVideoStickerAdapter = new VideoStickerAdapter(2);
//        mStickerGrid.setAdapter(mVideoStickerAdapter);
//        mStickerGrid.setOnItemClickListener(this);
//
//        RadioButton bubble, color, stroke, shadow;
//        bubble = findViewById(R.id.bubble);
//        bubble.setOnClickListener(this);
//        color = findViewById(R.id.color);
//        color.setOnClickListener(this);
//        stroke = findViewById(R.id.stroke);
//        stroke.setOnClickListener(this);
//        shadow = findViewById(R.id.shadow);
//        shadow.setOnClickListener(this);
//        mBubbleGrid = findViewById(R.id.bubble_grid);
//        mVideoStickerAdapter = new VideoStickerAdapter(1);
//        mBubbleGrid.setAdapter(mVideoStickerAdapter);
//        mBubbleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Map<String, Object> map = mVideoStickerAdapter.getmList().get(i);
//                int res = (int) map.get("img");
//
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),res);
//
//                MyStickerView stickerView = new MyStickerView(VideoFilterActivity.this);
//                stickerView.addSticker(bitmap);
//                stickerView.setEditing(true);
//                mViews.add(stickerView);
//
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//                params.gravity = Gravity.CENTER;
//                container.addView(stickerView,params);
//
//
////                Drawable drawable = ContextCompat.getDrawable(VideoFilterActivity.this, res);
////                mStickerView.addSticker(new DrawableSticker(drawable));
////                mHashMap.put(drawable, res);
//            }
//        });
//        mSeekLayout = findViewById(R.id.seekbar_layout);
//        mStickerView = findViewById(R.id.sticker_view);
//
//        mAddButton = findViewById(R.id.add_sticker_button);
//        mAddButton.setOnClickListener(this);
//        loadSticker();
//    }
//
//    private List<Map<String, Object>> getList() {
//        int icno[] = {R.drawable.ic_qipao, R.drawable.ic_mine, R.drawable.ic_hot, R.drawable.ic_cool,
//                R.drawable.ic_xiaoqingxin, R.drawable.ic_animal, R.drawable.ic_food};
//        for (int i = 0; i < icno.length; i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("img", icno[i]);
//            map.put("pick", false);
//            mList.add(map);
//        }
//        return mList;
//    }
//
//    private void loadSticker() {
//        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
//                R.drawable.ic_close_white),
//                BitmapStickerIcon.LEFT_TOP);
//        deleteIcon.setIconEvent(new DeleteEvent());
//
//        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
//                R.drawable.ic_zoom),
//                BitmapStickerIcon.RIGHT_BOTOM);
//        zoomIcon.setIconEvent(new ZoomEvent());
//
//        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
//                R.drawable.ic_ok),
//                BitmapStickerIcon.RIGHT_TOP);
//        flipIcon.setIconEvent(new CommitIconEvent());
//
//        mStickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
//
//        mStickerView.setBackgroundColor(Color.TRANSPARENT);
//        mStickerView.setLocked(false);
//        mStickerView.setConstrained(true);
//
//        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
//            @Override
//            public void onStickerAdded(@NonNull Sticker sticker) {
//
//            }
//
//            @Override
//            public void onStickerClicked(@NonNull Sticker sticker) {
//
//            }
//
//            @Override
//            public void onStickerDeleted(@NonNull Sticker sticker) {
//            }
//
//            @Override
//            public void onStickerDragFinished(@NonNull Sticker sticker) {
//            }
//
//            @Override
//            public void onStickerZoomFinished(@NonNull Sticker sticker) {
//
//            }
//
//            @Override
//            public void onStickerFlipped(@NonNull Sticker sticker) {
//
//            }
//
//            @Override
//            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onItemClick(int position) {
//        if (position != 0) {
//            isFirst = false;
//            mTextLayout.setVisibility(View.GONE);
//            mRadioGroup.setVisibility(View.GONE);
//            mStickerLayout.setVisibility(View.VISIBLE);
//
//        } else {
//            isFirst = true;
//            mTextLayout.setVisibility(View.VISIBLE);
//            mRadioGroup.setVisibility(View.VISIBLE);
//            mStickerLayout.setVisibility(View.GONE);
//        }
//
//        for (int i = 0; i < mFilterAdapter.getList().size(); i++) {
//            if (position == i) {//当前选中的Item改变背景颜色
//                mFilterAdapter.getList().get(i).put("pick", true);
//            } else {
//                mFilterAdapter.getList().get(i).put("pick", false);
//            }
//        }
//        mFilterAdapter.notifyDataSetChanged();
//
//        switch (position) {
//            case 0:
//                mVideoStickerAdapter = new VideoStickerAdapter(0);
//                mBubbleGrid.setAdapter(mVideoStickerAdapter);
//                break;
//            case 1:
//                mVideoStickerAdapter = new VideoStickerAdapter(1);
//                mStickerGrid.setAdapter(mVideoStickerAdapter);
//                break;
//            case 2:
//                mVideoStickerAdapter = new VideoStickerAdapter(2);
//                mStickerGrid.setAdapter(mVideoStickerAdapter);
//                break;
//            case 3:
//                mVideoStickerAdapter = new VideoStickerAdapter(3);
//                mStickerGrid.setAdapter(mVideoStickerAdapter);
//                break;
//            case 4:
//                mVideoStickerAdapter = new VideoStickerAdapter(4);
//                mStickerGrid.setAdapter(mVideoStickerAdapter);
//                break;
//            case 5:
//                mVideoStickerAdapter = new VideoStickerAdapter(5);
//                mStickerGrid.setAdapter(mVideoStickerAdapter);
//                break;
//            case 6:
//                mVideoStickerAdapter = new VideoStickerAdapter(6);
//                mStickerGrid.setAdapter(mVideoStickerAdapter);
//                break;
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() != R.id.add_sticker_button) {
//            mTextLayout.setVisibility(View.VISIBLE);
//        }
//        switch (v.getId()) {
//            case R.id.bubble:
//                mBubbleGrid.setVisibility(View.VISIBLE);
//                mSeekLayout.setVisibility(View.GONE);
//                break;
//            case R.id.color:
//                mBubbleGrid.setVisibility(View.GONE);
//                mSeekLayout.setVisibility(View.VISIBLE);
//                break;
//            case R.id.stroke:
//                mBubbleGrid.setVisibility(View.GONE);
//                mSeekLayout.setVisibility(View.VISIBLE);
//                break;
//            case R.id.shadow:
//                mBubbleGrid.setVisibility(View.GONE);
//                mSeekLayout.setVisibility(View.VISIBLE);
//                break;
//            case R.id.add_sticker_button:
//                if (isFirst) {
//                    mTextLayout.setVisibility(View.VISIBLE);
//                } else {
//                    mStickerLayout.setVisibility(View.VISIBLE);
//                }
//                mTypeRecycler.setVisibility(View.VISIBLE);
//                mAddRecycler.setVisibility(View.GONE);
//                mAddButton.setVisibility(View.GONE);
//                break;
//        }
//    }
//
//
//    List<MyStickerView> mViews = new ArrayList<>();
//    //贴纸点击事件
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Map<String, Object> map = mVideoStickerAdapter.getmList().get(i);
//        Integer res = (int) map.get("img");
//
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),res);
//
//        MyStickerView stickerView = new MyStickerView(VideoFilterActivity.this);
//        stickerView.addSticker(bitmap);
//        stickerView.setEditing(true);
//        mViews.add(stickerView);
//
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        params.gravity = Gravity.CENTER;
//        container.addView(stickerView,params);
//
//
////        Drawable drawable = ContextCompat.getDrawable(this, res.intValue());
////        mStickerView.addSticker(new DrawableSticker(drawable));
////        mHashMap.put(drawable, res);
//    }
//
//
//    //确认添加按键事件
//    public class CommitIconEvent implements StickerIconEvent {
//        @Override
//        public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
//            float x = motionEvent.getX();
//            float y = motionEvent.getY();
//            Log.e(TAG, "p = (" + x + "," + y + ")");
//            Drawable drawable = stickerView.getCurrentSticker().getDrawable();
//            Integer integer = mHashMap.get(drawable);
//            mAddStickerAdapter.addItem(integer);
//            mTypeRecycler.setVisibility(View.GONE);
//            mStickerLayout.setVisibility(View.GONE);
//            mTextLayout.setVisibility(View.GONE);
//            mAddRecycler.setVisibility(View.VISIBLE);
//            mAddButton.setVisibility(View.VISIBLE);
//        }
//    }
//
//    Map<Drawable, Integer> mHashMap = new HashMap<>();
//
//    //删除按键事件
//    public class DeleteEvent extends DeleteIconEvent {
//
//        @Override
//        public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
//            try {
//                Drawable drawable = stickerView.getCurrentSticker().getDrawable();
//                if (drawable != null) {
//                    Integer res = mHashMap.get(drawable);
//                    mHashMap.remove(drawable);
//                    if (mAddStickerAdapter != null) {
//                        int size = mAddStickerAdapter.getList().size();
//                        if (size > 0) {
//                            mAddStickerAdapter.removeItem(res);
//                        }
//                    }
//                    stickerView.removeCurrentSticker();
//                }
//            } catch (Exception e) {
//
//            }
//        }
//    }
//
//    class ZoomEvent extends ZoomIconEvent {
//        @Override
//        public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
//            super.onActionMove(stickerView, motionEvent);
//        }
//
//        @Override
//        public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
//
//            super.onActionUp(stickerView, motionEvent);
//            currentScale = stickerView.getCurrentSticker().getCurrentScale();
//            currentAngle = stickerView.getCurrentSticker().getCurrentAngle();
//
//            currentWidth = stickerView.getCurrentSticker().getCurrentWidth();
//            currentHeight = stickerView.getCurrentSticker().getCurrentHeight();
//
//
//            drawables.add(stickerView.getCurrentSticker().getDrawable());
//
//
//            Log.e(TAG, " ZoomEvent ChildCount -- " + currentWidth + ":" + currentHeight);
//
//            Log.e(TAG, " ZoomEvent " + currentScale + ":" + currentAngle);
//        }
//    }
//
//    float currentScale;
//    float currentAngle;
//
//    float currentWidth;
//    float currentHeight;
//
//    List<Drawable> drawables = new ArrayList<>();
//    private final static String fontPath = "/storage/emulated/0/Android/data/com.example.root/cache/data/fonts/hk_f.ttf";
//
//    public void goNext(View view) {
//        Log.e(TAG, "goNext -");
//        processVideo();
//    }
//
//    void processVideo() {
//
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                if (mViews != null){
//                    if (mViews.size() > 0){
//                        for (MyStickerView view : mViews) {
//                            Log.e(TAG, "MyStickerView -");
//                            String path = saveBitmapToSD(createViewBitmap(view));
//                            mCmd.addPicture(new CmdPicture(
//                                path, 200, 200,
//                                500,
//                                500,
//                                4, 8)
//                        );
//                            Log.e(TAG, "saveBitmapToSD -");
//                        }
//                        final String[] cmd = getCmds(outFilePath);
//                        getHandler().post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                Toast.makeText(VideoFilterActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
//                                executeCmd(cmd);
//
//
//                            }
//                        });
//                    }
//                }
//            }
//        });
//
//
////        Executors.newSingleThreadExecutor().submit(new Runnable() {
////            @Override
////            public void run() {
////                Log.e(TAG, "processVideo - run");
////                int y = 100;
////                for (Drawable drawable : drawables) {
////                    String path = saveBitmap(drawable);
////                    if (path != null) {
////                        mCmd.addPicture(new CmdPicture(
////                                path, 200, y,
////                                currentWidth,
////                                currentHeight,
////                                4, 8)
////                        );
////                        y += 100;
////                    }
////                }
//////                Log.e(TAG,"processVideo - addText");
//////                mCmd.addText(new CmdText(100, 200, 64, CmdText.Color.Green, fontPath, "addText22222", new CmdText.Time(2, 5)));
//////                mCmd.addText(new CmdText(100, 300, 64, CmdText.Color.Black, fontPath, "addText22222", new CmdText.Time(5, 8)));
//////                mCmd.addText(new CmdText(100, 400, 64, CmdText.Color.Blue, fontPath, "addText22222", new CmdText.Time(8, 11)));
//////                mCmd.addText(new CmdText(100, 500, 64, CmdText.Color.Yellow, fontPath, "addText22222", new CmdText.Time(11, 14)));
//////                mCmd.addText(new CmdText(100, 600, 64, CmdText.Color.Cyan, fontPath, "addText22222", new CmdText.Time(14, 16)));
////
//////                Log.e(TAG,"processVideo - getCmds");
////                final String[] cmd = getCmds(outFilePath);
////                getHandler().post(new Runnable() {
////                    @Override
////                    public void run() {
////                        Log.e(TAG, "processVideo - excuteCmd");
////                        executeCmd(cmd);
////                    }
////                });
////            }
////        });
//    }
//
//    Bitmap createViewBitmap(View view) {
//        if (view == null) {
//            return null;
//        }
//        Log.e(TAG, "save -");
//        int corner = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap tmp = view.getDrawingCache();
//        if (tmp == null){
//            return null;
//        }
//        Bitmap bitmap = Bitmap.createBitmap(tmp,0, corner, tmp.getWidth(), tmp.getHeight() - 2 * corner);
//        view.setDrawingCacheEnabled(false);
//        if (bitmap == null){
//            return null;
//        }
//        return bitmap;
//    }
//    private String saveBitmapToSD(Bitmap bitmap) {
//        Log.e(TAG, "saveToSD -");
//        if (bitmap == null) {
//            return null;
//        }
//        Log.e(TAG, "save -");
//        String savePath = getExternalCacheDir().getPath() + File.separator + "sticker";
//        File filePic;
//        try {
//            filePic = new File(savePath, System.currentTimeMillis() + ".png");
//            Log.e(TAG, "new File -");
//            if (!filePic.exists()) {
//                filePic.getParentFile().mkdirs();
//                filePic.createNewFile();
//            }
//            Log.e(TAG, "createNewFile -");
//            FileOutputStream fos = new FileOutputStream(filePic);
//            Log.e(TAG, "FileOutputStream -");
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            Log.e(TAG, "compress -");
//            fos.flush();
//            fos.close();
//            Log.e(TAG, "close -");
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//            return filePic.getPath();
//        } catch (IOException e) {
//            Log.e(TAG, "IOException -");
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//            return null;
//        }
//    }
//
//    private String[] getCmds(String outPath) {
//        List<String> cmdList = new ArrayList<>();
//        List<CmdPicture> pictures = mCmd.getPictures();
//        cmdList.add("-y");
//        cmdList.add("-i");
//        cmdList.add(mCmd.getVideoPath());
//
//        if (pictures.size() > 0) {
//            // 添加多图以及多文字命令
//            for (int i = 0; i < pictures.size(); i++) {
//                cmdList.add("-i");
//                cmdList.add(pictures.get(i).getPicPath());
//            }
//            cmdList.add("-filter_complex");
//            StringBuilder filter_complex = new StringBuilder();
//            filter_complex
//                    .append("[0:v]")
//                    .append(mCmd.getTexts() != null ? mCmd.getTexts() + "," : "")
//                    .append("scale=")
//                    .append("iw").append(":")
//                    .append("ih")
//                    .append("[outv0];");
//            for (int i = 0; i < pictures.size(); i++) {
//                filter_complex.append("[").append(i + 1).append(":0]").append(pictures.get(i).getPicFilter()).append("scale=").append(pictures.get(i).getPicWidth()).append(":")
//                        .append(pictures.get(i).getPicHeight()).append("[outv").append(i + 1).append("];");
//            }
//            for (int i = 0; i < pictures.size(); i++) {
//                if (i == 0) {
//                    filter_complex.append("[outv").append(i).append("]").append("[outv").append(i + 1).append("]");
//                } else {
//                    filter_complex.append("[outo").append(i - 1).append("]").append("[outv").append(i + 1).append("]");
//                }
//                filter_complex.append("overlay=").append(pictures.get(i).getPicX()).append(":").append(pictures.get(i).getPicY())
//                        .append(pictures.get(i).getTime());
//                if (i < pictures.size() - 1) {
//                    filter_complex.append("[outo").append(i).append("];");
//                }
//            }
//            cmdList.add(filter_complex.toString());
//        } else {
//            StringBuilder filter_complex = new StringBuilder();
//            if (mCmd.getTexts() != null) {
//                cmdList.add("-filter_complex");
//                filter_complex.append(mCmd.getTexts());
//            }
//            if (!filter_complex.toString().equals("")) {
//                cmdList.add(filter_complex.toString());
//            }
//        }
//        cmdList.add("-preset");
//        cmdList.add("superfast");
//        cmdList.add(outPath);
//
//        String cmd = "";
//        for (String s : cmdList) {
//            cmd += s;
//        }
//        Log.e(TAG, "CMD: " + cmd);
//        return cmdList.toArray(new String[cmdList.size()]);
//    }
//
//    private void executeCmd(String[] cmd) {
//        try {
//            ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
//                @Override
//                public void onSuccess(String message) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse(outFilePath), "video/mp4");
//                    startActivity(intent);
//
////                    Intent intent = new Intent(VideoFilterActivity.this, SaveActionsActivity.class);
////                    intent.putExtra("selectedVideo", outFilePath);
////                    intent.putExtra("type", mType);
////                    CommonTool.openActivity(VideoFilterActivity.this, intent, CommonConstans.REQUEST_CODE_NULL);
////                    finish();
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    Log.e(TAG, message);
//                }
//
//                @Override
//                public void onFailure(String message) {
//
//                }
//
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onFinish() {
//
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//
//        }
//    }
//
//    private String saveBitmap(Drawable drawable) {
//        Log.e(TAG, "saveBitmap -");
//
//        Bitmap bitmap = Bitmap.createBitmap((int) currentWidth, (int) currentWidth, Bitmap.Config.ARGB_8888);
//        Log.e(TAG, "createBitmap -");
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawColor(Color.TRANSPARENT);
////        canvas.clipRect(0 ,0 , currentWidth , currentWidth);
//        canvas.scale(currentScale, currentScale);
//        canvas.rotate(currentAngle);
//        drawable.setBounds(0, 0, (int) currentWidth, (int) currentHeight);
//        drawable.draw(canvas);
//        return saveToSD(bitmap);
//    }
//
//    private String saveToSD(Bitmap bitmap) {
//        Log.e(TAG, "saveToSD -");
//        if (bitmap == null) {
//            return null;
//        }
//        Log.e(TAG, "save -");
//        String savePath = getExternalCacheDir().getPath() + File.separator + "sticker";
//        if (savePath == null) {
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//            return null;
//        }
//        File filePic;
//        try {
//            filePic = new File(savePath, System.currentTimeMillis() + ".png");
//            if (!filePic.exists()) {
//                filePic.getParentFile().mkdirs();
//                filePic.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(filePic);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//            return filePic.getPath();
//        } catch (IOException e) {
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//            return null;
//        }
//    }
//
//}
