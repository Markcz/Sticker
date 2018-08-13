package com.example.chenzhen.sticker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by chenzhen on 2018/7/30.
 */

public class XTextView extends AppCompatTextView {

    final static String TAG = "XTextView";

    public int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    public static final int IDLE_MODE = 2;//正常
    public static final int MOVE_MODE = 3;//移动模式
    public static final int ROTATE_MODE = 4;//旋转模式
    public static final int DELETE_MODE = 5;//删除模式
    public static final int FLIP_MODE = 6;//删除模式


    public int layout_x = 0;
    public int layout_y = 0;

    boolean isShowBox = false;

    Context mContext;
    Paint mBoxPaint;  // 边框画笔
    Paint mTextPaint; // 文字画笔

    Bitmap mDeleteBitmap, mRotateBitmap, mFlipBitmap;  // 删除  旋转缩放  翻转


    int boxColor = Color.RED;
    float boxWidth = 5;

    int textColor = Color.RED;
    float textSize = 32;

    float mRotateAngle = 0;
    float mScale = 1;


    // 默认宽高
    final static int DEFAULT_WIDTH = 300;
    final static int DEFAULT_HEIGHT = 200;


    RectF mBoxRectF = new RectF();
    Rect mDeleteRect = new Rect();
    Rect mRotateRect = new Rect();
    Rect mFlipBitmapRect = new Rect();


    RectF mDeleteDstRect = new RectF(0, 0, 60, 60);
    RectF mRotateDstRect = new RectF(0, 0, 60, 60);
    RectF mFlipBitmapDstRect = new RectF(0, 0, 60, 60);


    public XTextView(Context context) {
        this(context,null);
    }

    public XTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public XTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    float last_x = 0;
    float last_y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isShowBox) {
            isShowBox = false;
        }
        boolean result = false;
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {
                    Log.e(TAG, "删除 - " + "(" + x + "," + y + ")");

                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {
                    Log.e(TAG, "旋转缩放 - " + "(" + x + "," + y + ")");
                    mCurrentMode = ROTATE_MODE;
                    last_x = x;
                    last_y = y;
                    result = true;
                } else if (mFlipBitmapDstRect.contains(x, y)) {
                    Log.e(TAG, "翻转 - " + "(" + x + "," + y + ")");
                    mCurrentMode = FLIP_MODE;

                    result = true;
                } else if (mBoxRectF.contains(x, y)) {
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

                    layout_x += dx;
                    layout_y += dy;

                    float newLeft = mBoxRectF.left + layout_x;
                    float newTop = mBoxRectF.top + layout_y;
                    float newRight = newLeft + mBoxRectF.width();
                    float newBottom = newTop + mBoxRectF.height();

                    mBoxRectF.set(newLeft,newTop,newRight,newBottom);
                    invalidate();

//                    int newLeft = getLeft() + layout_x;
//                    int newTop = getTop() + layout_y;
//                    layout(newLeft, newTop, newLeft + getWidth(), newTop + getHeight());

                    last_x = x;
                    last_y = y;

                } else if (mCurrentMode == ROTATE_MODE) {
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    updateRotateAndScale(dx, dy);

                    invalidate();

                    last_x = x;
                    last_y = y;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                result = false;
                mCurrentMode = IDLE_MODE;
                break;
        }

        return result;

    }

    void updateRotateAndScale(float dx, float dy) {

        float c_x = mBoxRectF.centerX();
        float c_y = mBoxRectF.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        mScale *= scale;
        float newWidth = mBoxRectF.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;
        mRotateAngle += angle;

//        setScaleX(mScale);
//        setScaleY(mScale);
//        setRotation(mRotateAngle);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    void prepare(Canvas canvas) {


        mBoxRectF.set(30, 30, DEFAULT_WIDTH - 30, DEFAULT_HEIGHT - 30);
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;



        mDeleteDstRect.offsetTo(mBoxRectF.left - offsetValue, mBoxRectF.top - offsetValue);
        mRotateDstRect.offsetTo(mBoxRectF.right - offsetValue, mBoxRectF.bottom - offsetValue);
        mFlipBitmapDstRect.offsetTo(mBoxRectF.right - offsetValue, mBoxRectF.top - offsetValue);


        mDeleteBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_close_red);
        mRotateBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_zoom);
        mFlipBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_rotate);

        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mRotateBitmap.getHeight());
        mFlipBitmapRect.set(0, 0, mFlipBitmap.getWidth(), mFlipBitmap.getHeight());

        canvas.save();
        canvas.scale(mScale, mScale, mBoxRectF.centerX(), mBoxRectF.centerY());
        canvas.rotate(mRotateAngle, mBoxRectF.centerX(), mBoxRectF.centerY());
        canvas.drawRoundRect(mBoxRectF, 10, 10, mBoxPaint);
        canvas.restore();


        canvas.save();
        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
        canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);
        canvas.drawBitmap(mFlipBitmap, mFlipBitmapRect, mFlipBitmapDstRect, null);
        canvas.restore();


    }

}
