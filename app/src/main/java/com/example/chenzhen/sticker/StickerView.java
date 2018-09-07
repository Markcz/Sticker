package com.example.chenzhen.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by sam on 14-8-14.
 */
public class StickerView extends View implements ViewTreeObserver.OnGlobalLayoutListener{


    final static String TAG = "StickerView";

    private float mScaleSize = 1.0f;

    public static final float MAX_SCALE_SIZE = 2.6f;
    public static final float MIN_SCALE_SIZE = 0.3f;
    static final int width = 300;
    static final int height = 300;


    public int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    public static final int IDLE_MODE = 2;//正常
    public static final int MOVE_MODE = 3;//移动模式
    public static final int ROTATE_MODE = 4;//旋转模式
    public static final int DELETE_MODE = 5;//删除模式
    public static final int FLIP_MODE = 6;//翻转模式


    private float[] mOriginPoints;
    private float[] mPoints;
    private RectF mOriginContentRect;
    private RectF mContentRect;

    private Bitmap mBitmap;
    private Bitmap mZoomBitmap, mDeleteBitmap, mFlipBitmap;
    private Matrix mMatrix;
    private Paint mBorderPaint;


    Rect mDeleteRect = new Rect();
    Rect mRotateRect = new Rect();
    Rect mFlipBitmapRect = new Rect();


    RectF mDeleteDstRect = new RectF(0, 0, 60, 60);
    RectF mRotateDstRect = new RectF(0, 0, 60, 60);
    RectF mFlipBitmapDstRect = new RectF(0, 0, 60, 60);


    int CONTROLLER_BOUND = 60;
    static final int padding = 80;


    private boolean isEditing = false;


    private OnStickerListener mOnStickerListener;
    private Paint mPaint;




    GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            isEditing = !isEditing;
            setEditing(isEditing);
            if (isEditing){
                setFocusable(true);
            }
            if (mOnStickerListener != null){
                mOnStickerListener.onTabClick(StickerView.this);
                return true;
            }
            return false;
        }
    };

    GestureDetector detector;
    private Context mContext;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        mContext = context;
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        mBorderPaint.setStrokeWidth(5);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        mBorderPaint.setColor(Color.parseColor("#FA6580"));

        mZoomBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_zoom);

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_close_red);

        mFlipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_rotate);

        CONTROLLER_BOUND = mZoomBitmap.getWidth();

    }


    public Bitmap getSticker(){
        return mBitmap;
    }



    public void addSticker(@NonNull Bitmap bitmap) {
        if (bitmap == null){
            throw new NullPointerException("传入的bitmap为null");
        }
        mBitmap = bitmap;
        detector = new GestureDetector(mContext,listener);
        setFocusable(true);
        try {

            float px = mBitmap.getWidth();
            float py = mBitmap.getHeight();

            mOriginPoints = new float[]{
                    -padding, -padding,
                    px + padding, -padding,
                    px + padding, py + padding,
                    -padding, py + padding,
                    px / 2, py / 2
            };
            mOriginContentRect = new RectF(-padding, -padding, px + padding, py + padding);
            mPoints = new float[10];
            mContentRect = new RectF();

            mMatrix = new Matrix();
            mMatrix.postTranslate(width / 2, height / 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidate();

    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        postInvalidate();
    }

    public void setEditing(boolean drawController) {
        isEditing = drawController;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null || mMatrix == null) {
            return;
        }

        if (mCurrentMode != FLIP_MODE) {
            mMatrix.mapPoints(mPoints, mOriginPoints);
            mMatrix.mapRect(mContentRect, mOriginContentRect);
        }

        canvas.drawBitmap(mBitmap, mMatrix, mPaint);

        if (isEditing) {

            canvas.drawLine(mPoints[0], mPoints[1], mPoints[2], mPoints[3], mBorderPaint);
            canvas.drawLine(mPoints[2], mPoints[3], mPoints[4], mPoints[5], mBorderPaint);
            canvas.drawLine(mPoints[4], mPoints[5], mPoints[6], mPoints[7], mBorderPaint);
            canvas.drawLine(mPoints[6], mPoints[7], mPoints[0], mPoints[1], mBorderPaint);

            mDeleteDstRect.offsetTo(mPoints[0] - 30, mPoints[1] - 30);
            mRotateDstRect.offsetTo(mPoints[4] - 30, mPoints[5] - 30);
            mFlipBitmapDstRect.offsetTo(mPoints[2] - 30, mPoints[3] - 30);


            mDeleteRect.set(0, 0, CONTROLLER_BOUND, CONTROLLER_BOUND);
            mRotateRect.set(0, 0, CONTROLLER_BOUND, CONTROLLER_BOUND);
            mFlipBitmapRect.set(0, 0, CONTROLLER_BOUND, CONTROLLER_BOUND);

            canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
            canvas.drawBitmap(mZoomBitmap, mRotateRect, mRotateDstRect, null);
            canvas.drawBitmap(mFlipBitmap, mFlipBitmapRect, mFlipBitmapDstRect, null);

        }

    }


    float last_x = 0;
    float last_y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1){
            return true;
        }
        boolean result = false;
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {
                    Log.e(TAG, "删除 - " + "(" + x + "," + y + ")");
                    mCurrentMode = DELETE_MODE;
                    if (mOnStickerListener != null){
                        Log.e(TAG, "ACTION_UP - 删除 -" + "(" + x + "," + y + ")");
                        mOnStickerListener.onDeleteClick(this);
                        if (mBitmap != null){
                            if (!mBitmap.isRecycled()){
                                mBitmap.recycle();
                            }
                            mBitmap = null;
                        }
                    }
                    result = false;
                } else if (mRotateDstRect.contains(x, y)) {
                    Log.e(TAG, "旋转缩放 - " + "(" + x + "," + y + ")");
                    mCurrentMode = ROTATE_MODE;
                    last_x = x;
                    last_y = y;
                    result = true;
                } else if (mFlipBitmapDstRect.contains(x, y)) {
                    Log.e(TAG, "翻转 - " + "(" + x + "," + y + ")");
                    mCurrentMode = FLIP_MODE;
                    // 翻转
                    Log.e(TAG, "ACTION_UP - 翻转 -" + "(" + x + "," + y + ")");
                    mMatrix.postScale(-1,1,mContentRect.centerX(),mContentRect.centerY());
                    invalidate();
                    result = false;
                } else if (mContentRect.contains(x, y)) {
                    Log.e(TAG, "移动 - " + "(" + x + "," + y + ")");
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    result = true;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                result = true;
                if (mCurrentMode == MOVE_MODE) {
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;
                    mMatrix.postTranslate(dx, dy);
                    postInvalidate();
                    last_x = x;
                    last_y = y;

                } else if (mCurrentMode == ROTATE_MODE) {
                    mCurrentMode = ROTATE_MODE;
                    mMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
                    float nowLength = calculateLength(mPoints[0], mPoints[1]);
                    float touchLength = calculateLength(event.getX(), event.getY());

                    float scale = touchLength / nowLength;
                    float nowScale = mScaleSize * scale;
                    if (nowScale >= MIN_SCALE_SIZE && nowScale <= MAX_SCALE_SIZE) {
                        mMatrix.postScale(scale, scale, mPoints[8], mPoints[9]);
                        mScaleSize = nowScale;
                    }
                    invalidate();
                    last_x = x;
                    last_y = y;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                result = false;
                mCurrentMode = IDLE_MODE;
                last_x = 0;
                last_y = 0;
                break;
        }

        return result;

    }


    private float calculateLength(float x, float y) {
        float ex = x - mPoints[8];
        float ey = y - mPoints[9];
        return (float) Math.sqrt(ex * ex + ey * ey);
    }


    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(last_x, last_y);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    private float calculateDegree(float x, float y) {
        double delta_x = x - mPoints[8];
        double delta_y = y - mPoints[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    @Override
    public void onGlobalLayout() {

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public interface OnStickerListener {
        void onDeleteClick(StickerView stickerView);
        void onTabClick(StickerView stickerView);
    }

    public static class SimpleOnStickerListener implements OnStickerListener{

        @Override
        public void onDeleteClick(StickerView stickerView) {

        }

        @Override
        public void onTabClick(StickerView stickerView) {

        }
    }

    public void setOnStickerListener(OnStickerListener listener) {
        mOnStickerListener = listener;
    }
}
