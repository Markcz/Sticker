package com.example.chenzhen.sticker;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chenzhen on 2018/7/30.
 */

public class EditTextView extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    final static String TAG = "EditTextView";

    public int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    public static final int IDLE_MODE = 2;//正常
    public static final int MOVE_MODE = 3;//移动模式
    public static final int ROTATE_MODE = 4;//旋转模式
    public static final int DELETE_MODE = 5;//删除模式
    public static final int FLIP_MODE = 6;//删除模式


    Rect mBoxRect = new Rect();
    Rect mDeleteDstRect = new Rect();
    Rect mZoomDstRect = new Rect();
    Rect mFlipDstRect = new Rect();

    boolean isShowBox = false;


    float mRotateAngle = 0;
    float mScale = 1;

    public int layout_x = 0;
    public int layout_y = 0;


    public TextView mTextView;
    ImageView mDeleteIcon, mFlipIcon, mZoomIcon;

    public EditTextView(@NonNull Context context) {
        this(context, null);
    }

    public EditTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.edit_text_view, this);
        mTextView = view.findViewById(R.id.tv_content);
        mDeleteIcon = view.findViewById(R.id.iv_delete);
        mFlipIcon = view.findViewById(R.id.iv_flip);
        mZoomIcon = view.findViewById(R.id.iv_zoom);

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
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {
                    Log.e(TAG, "删除 - " + "(" + x + "," + y + ")");

                    mCurrentMode = DELETE_MODE;
                } else if (mZoomDstRect.contains(x, y)) {
                    Log.e(TAG, "旋转缩放 - " + "(" + x + "," + y + ")");
                    mCurrentMode = ROTATE_MODE;
                    last_x = x;
                    last_y = y;
                    result = true;
                } else if (mFlipDstRect.contains(x, y)) {
                    Log.e(TAG, "翻转 - " + "(" + x + "," + y + ")");
                    mCurrentMode = FLIP_MODE;
                    result = true;
                } else if (mBoxRect.contains(x, y)) {
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

                    int newLeft = getLeft() + layout_x;
                    int newTop = getTop() + layout_y;
                    layout(newLeft, newTop, newLeft + getWidth(), newTop + getHeight());

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

        float c_x = mBoxRect.centerX();
        float c_y = mBoxRect.centerY();

        float x = mZoomDstRect.centerX();
        float y = mZoomDstRect.centerY();

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
        float newWidth = mBoxRect.width() * mScale;

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

        mTextView.setScaleX(mScale);
        mTextView.setScaleY(mScale);
        mTextView.setRotation(mRotateAngle);


        mZoomIcon.setRotation(mRotateAngle);
        mDeleteIcon.setRotation(mRotateAngle);
        mFlipIcon.setRotation(mRotateAngle);

    }

    @Override
    public void onGlobalLayout() {
        mTextView.getHitRect(mBoxRect);
        mDeleteIcon.getHitRect(mDeleteDstRect);
        mFlipIcon.getHitRect(mFlipDstRect);
        mZoomIcon.getHitRect(mZoomDstRect);
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
}
