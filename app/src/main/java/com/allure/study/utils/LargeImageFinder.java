package com.allure.study.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * 大图探测器, 仅用于测试和优化
 *
 * Created by luomin on 16/7/17.
 */
public class LargeImageFinder {

    private static final String TAG = "LargeImageFinder";

    /** 每次发版打开开关用于检测 */
    public static final boolean DEBUG_DRAW = false && Constants.IS_DEBUG;
    public static final boolean DEBUG_LOG = false && Constants.IS_DEBUG;
    public static final boolean DEBUG_LARGE = false && Constants.IS_DEBUG;

    private ImageView mImageView;
    private Resources mResources;
    private Paint mPaint;

    public LargeImageFinder(ImageView imageView) {
        mImageView = imageView;
        mResources = imageView.getResources();
    }

    public void drawDebug(Canvas canvas) {
        if (DEBUG_DRAW) {
            drawInfo(canvas, DEBUG_LARGE);
        }
    }

    private void drawInfo(Canvas canvas, boolean alertTwoLarge) {

        float textSize = 10 - (3 - mResources.getDisplayMetrics().density);
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                textSize, mResources.getDisplayMetrics());

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(textSize);
        }

        int line = 1;
        canvas.drawText("v:" + hashCode(), 0, textSize * line++, mPaint);
        String viewSize = "  " + mImageView.getWidth() + " x " + mImageView.getHeight();
        canvas.drawText(viewSize, 0, textSize * line++, mPaint);

        boolean redAlert = false;
        String drawableHash = "d:0";
        String drawableName = "  null";
        String drawableSize = "  0";
        String bitmapHash =   "b:0";
        String bitmapSize =   "  0";
        if (mImageView.getDrawable() != null) {
            Drawable realDrawable = getRealDrawable(mImageView.getDrawable());

            drawableHash = "d:" + realDrawable.hashCode();
            drawableName = "  " + realDrawable.getClass().getSimpleName();
            drawableSize = "  " + realDrawable.getIntrinsicWidth() + " x " + realDrawable.getIntrinsicHeight();

            if (realDrawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) realDrawable).getBitmap();
                if (bitmap != null) {
                    bitmapHash = "b:" + bitmap.hashCode();
                    bitmapSize = "  " + bitmap.getWidth() + "x" + bitmap.getHeight();
                }
            }

            if (alertTwoLarge && isDrawableTooLarge(realDrawable)) {
                redAlert = true;
            }
        }
        canvas.drawText(drawableHash, 0, textSize * line++, mPaint);
        canvas.drawText(drawableName, 0, textSize * line++, mPaint);
        canvas.drawText(drawableSize, 0, textSize * line++, mPaint);
        canvas.drawText(bitmapHash, 0, textSize * line++, mPaint);
        canvas.drawText(bitmapSize, 0, textSize * line++, mPaint);

        String time = "t:" + System.currentTimeMillis();
        canvas.drawText(time, 0, textSize * line++, mPaint);

        if (redAlert) {
            // alert
            canvas.drawColor(0x33ff0000);
        } else {
            canvas.drawColor(0x55000000);
        }
    }

    private Drawable getRealDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable lastDrawable = null;
        Drawable realDrawable = drawable;
        int i = 0;
        while (i ++ < 5) {
            if (realDrawable == null || lastDrawable == realDrawable) {
                return lastDrawable;
            }
            lastDrawable = realDrawable;

            if (realDrawable instanceof BitmapDrawable) {
                return realDrawable;

            }
            //FaceBook Fresco
           /* else if (realDrawable instanceof ForwardingDrawable) {
                ForwardingDrawable forwardingDrawable = (ForwardingDrawable) realDrawable;
                realDrawable = forwardingDrawable.getDrawable();

            } else if (realDrawable instanceof FadeDrawable) {
                FadeDrawable fadeDrawable = (FadeDrawable) realDrawable;
                // FadeDrawable has a multi-layers, last layer is actual drawable
                int count = fadeDrawable.getNumberOfLayers();
                for (int index = count - 1; index >= 0; index --) {
                    if (fadeDrawable.getDrawable(index) != null) {
                        realDrawable = fadeDrawable.getDrawable(index);
                        break;
                    }
                }
            }*/
        }

        return realDrawable;
    }

    private boolean isDrawableTooLarge(Drawable realDrawable) {
        if (realDrawable instanceof BitmapDrawable) {
            BitmapDrawable bmDrawable = (BitmapDrawable) realDrawable;
            Bitmap bitmap = bmDrawable.getBitmap();

            // 大于View的1.6倍警告
            int thresholdW = mImageView.getWidth() * 16 / 10;
            int thresholdH = mImageView.getHeight() * 16 / 10;

            if (bmDrawable.getIntrinsicWidth() > thresholdW
                    || bmDrawable.getIntrinsicHeight() > thresholdH) {

                Log.d(TAG, "drawable w=" + bmDrawable.getIntrinsicWidth()
                        + " h=" + bmDrawable.getIntrinsicHeight()
                        + " hc=" + bmDrawable.hashCode()
                        + "; bitmap w=" + bitmap.getWidth()
                        + " h=" + bitmap.getHeight()
                        + " s=" + ResourceUtils.getBitmapByteCount(bitmap)
                        + " hc=" + bitmap.hashCode()
                        + "; view w=" + mImageView.getWidth()
                        + " h=" + mImageView.getHeight()
                        + " hc=" + hashCode());
                return true;
            }
        }
        return false;
    }
}
