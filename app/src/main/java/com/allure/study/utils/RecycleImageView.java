package com.allure.study.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by luomin on 2016/5/26.
 */
public class RecycleImageView extends AppCompatImageView {

    /** 大图探测器, 仅用于测试和优化 */
    private LargeImageFinder mLargeImageFinder = new LargeImageFinder(this);

    public RecycleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            Log.d("RecycleImageView","RecycleImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // 探测是否存在图片过大
        mLargeImageFinder.drawDebug(canvas);
        Log.e("draw","RecycleView draw");
    }
}
