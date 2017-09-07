package com.allure.study.utils;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 资源工具，目前主要用来对纯图片资源BitmapDrawable进行缩放压缩处理，减小内存占用
 *
 * 因为目前的图片资源都放在drawable-hdpi，为了减少包大小，其他密度的目录没有放置重复的图片资源，
 * drawable-hdpi实际上放的图都是适应720的资源，Android系统对res目录下图片资源有自动缩放机制，
 * hdpi目录中图片资源应用在高分辨率机器上会经过scale放大，则图片大小会乘以scale倍，导致内存占用较高。
 * 因此，通过该类会做不放大处理，并进行弱引用缓存，减少获取开销和内存占用。
 *
 * 建议：不要全部资源堆到drawable-hdpi下, 要根据资源实际大小放到不同像素密度目录下, 再配合改工具减少内存大小
 *
 * Created by luomin on 16/6/21.
 */
public class ResourceUtils {

    private static final boolean DEBUG_SCALE = Constants.IS_DEBUG && false;
    private static final String TAG = "ResUtils";

    /**
     * Bitmap图片资源缓存
     *
     * 不应该缓存Drawable，Drawable会保存资源的使用状态
     */
    private static HashMap<String, WeakReference<Bitmap>> stringWeakReferenceBitmap =
            new HashMap<String, WeakReference<Bitmap>>();

    private static String makeKey(int resId, boolean useRgb565) {
        return new StringBuilder()
                .append(resId).append("-")
                .append(useRgb565 ? "1" : "0")
                .toString();
    }

    public static int getBitmapByteCount(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 12) {
            return bitmap.getByteCount();
        }
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private static Bitmap getScaledBitmap(Resources resource, int resId, boolean useRgb565) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 不读取像素数组到内存中，仅读取图片的信息
        options.inJustDecodeBounds = true;

        // 获取图片大小
        BitmapFactory.decodeResource(resource, resId, options);

        if (DEBUG_SCALE) {
            Log.d(TAG, "---- DISPLAY_WIDTH: " + Constants.DISPLAY_WIDTH
                    + " DISPLAY_HEIGHT: " + Constants.DISPLAY_HEIGHT);
            Log.d(TAG, "JustDecodeBounds - inDensity: " + options.inDensity
                    + " inTargetDensity: " + options.inTargetDensity
                    + " densityDpi: " + resource.getDisplayMetrics().densityDpi);
            Log.d(TAG, "outWidth: " + options.outWidth + " outHeight: " + options.outHeight);
        }
        // 从Options中获取图片的分辨率
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        /**
         * 该段代码作用原因：因为目前的图片资源都放在drawable-hdpi，为了减少包大小，其他密度的目录没有放置重复的图片资源，
         * drawable-hdpi实际上放的图都是适应720的资源，Android系统对res目录下图片资源有字段缩放机制，
         * hdpi目录中图片资源应用在高分辨率机器上会经过scale放大，则图片大小会乘以scale倍，导致内存占用较高。
         *
         * 因此，设备densityDpi大于图片inDensity时，图片不放大处理，减少内存占用。
         * 图片大小大于屏幕时，使用采样率inSampleSize进行解码
         * 图片的inDensity高于设备densityDpi时，交由Resources内部scale缩小处理
         *
         * decodeResource会比对Bitmap的inDensity和inTargetDensity是否一致，并做比例缩放处理
         * inDensity即是目录对应的dpi，inTargetDensity则是设备的dpi，因此把inDensity设置成一致即能不做缩放
         */
        boolean densityFaking = false;

        if (options.inDensity < resource.getDisplayMetrics().densityDpi) {
            // 相同的density不会scale放大
            options.inDensity = resource.getDisplayMetrics().densityDpi;
            densityFaking = true;

            if (DEBUG_SCALE) {
                Log.d(TAG, "set inDensity=" + resource.getDisplayMetrics().densityDpi);
            }
        } else {
            // 根据density计算scale缩小之后宽高
            srcWidth = scaleFromDensity(srcWidth, options.inDensity, options.inTargetDensity);
            srcHeight = scaleFromDensity(srcHeight, options.inDensity, options.inTargetDensity);

            if (DEBUG_SCALE) {
                Log.d(TAG, "scaleFromDensity srcWidth=" + srcWidth + " srcHeight=" + srcHeight);
            }
        }

        ImageSize srcSize = new ImageSize(srcWidth, srcHeight);
        ImageSize tarSize = new ImageSize(Constants.DISPLAY_WIDTH, Constants.DISPLAY_HEIGHT);

        // 根据density计算scale之后的宽高才是准确的采样源大小
        // 计算采样率，缩小图片
        int inSampleSize = ImageSizeUtils.computeImageSampleSize(srcSize, tarSize, ViewScaleType.FIT_INSIDE, true);

        if (useRgb565) {
            if (DEBUG_SCALE) {
                Log.d(TAG, "PreferredConfig use RGB565");
            }
            // 通常机型能根据图片是否有Alpha通道来决定是否真正使用RGB_565，但有的机型是强制应用，所以RGB_565还是得慎重使用
            options.inPreferredConfig = Bitmap.Config.RGB_565;

        } else if (!densityFaking && inSampleSize == 1) {
            // 不需要压缩，也不需要采样，直接返回null，由外部处理
            if (DEBUG_SCALE) {
                Log.d(TAG, "No scaling and no sampling, just return");
            }
            return null;
        }

        options.inSampleSize = inSampleSize;
        // 读取图片像素数组到内存中,设定的采样率
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(resource, resId, options);

        if (DEBUG_SCALE) {
            Log.d(TAG, "DecodeBitmap - inDensity: " + options.inDensity
                    + " inTargetDensity: " + options.inTargetDensity
                    + " densityDpi: " + resource.getDisplayMetrics().densityDpi);
            Log.d(TAG, "inSampleSize: " + options.inSampleSize
                    + "  outWidth: " +  options.outWidth + " outHeight: " + options.outHeight);
            Log.d(TAG, "bitmap byte: " + getBitmapByteCount(bitmap) + " density=" + bitmap.getDensity()
                    + " width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        }

        return bitmap;
    }

    /**
     * 获取经过缩放压缩的Drawable
     *
     * @param resource      资源对象
     * @param resId         资源id
     * @param useRgb565     是否使用RGB565来解码图像，在百分百确定资源没有Alpha通道时可以使用
     */
    public static Drawable getScaledDrawable(Resources resource, int resId, boolean useRgb565) {
        Bitmap bitmap = null;
        try {
            // 获取缩放后的Bitmap
            bitmap = getScaledBitmap(resource, resId, useRgb565);

            if (bitmap == null) {
                // 为空，则走默认的Resources API，会做scale缩小，并且有Android框架的Drawable缓存机制来保证复用
                Drawable drawable = resource.getDrawable(resId);

                if (DEBUG_SCALE) {
                    Log.d(TAG, "getDrawable getIntrinsicWidth: " + drawable.getIntrinsicWidth()
                            + " getIntrinsicHeight: " + drawable.getIntrinsicHeight());

                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
                        Log.d(TAG, "bitmap byte: " + getBitmapByteCount(bm) + " density=" + bm.getDensity()
                                + " width: " + bm.getWidth() + " height: " + bm.getHeight());
                    }
                }
                return drawable;
            }

            // 重建Drawable对象，并且densityDpi要一致，否则BitmapDrawable会对再做一次scale
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            drawable.setTargetDensity(resource.getDisplayMetrics().densityDpi);

            if (DEBUG_SCALE) {
                Log.d(TAG, "drawable getIntrinsicWidth: " + drawable.getIntrinsicWidth()
                        + " getIntrinsicHeight: " + drawable.getIntrinsicHeight());
            }

            return drawable;
        } catch (Exception e) {
            e.printStackTrace();

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            return resource.getDrawable(resId);
        }
    }

    /**
     * 获取经过缩放压缩的Drawable
     *
     * @param resource      资源对象
     * @param resId         资源id
     */
    public static Drawable getScaledDrawable(Resources resource, int resId) {
        return getScaledDrawable(resource, resId, false);
    }

    /**
     * 获取经过缩小压缩的Drawable，如果弱引用缓存存在则获取，不存在则decode出来
     *
     * @param resource      resource
     * @param resId         drawableId
     * @param useRgb565     是否使用RGB565来解码图像，在百分百确定资源没有Alpha通道时可以使用
     * @return
     */
    public static Drawable getCachedScaledDrawable(Resources resource, int resId, boolean useRgb565) {
        Bitmap bitmap = null;
        String key = makeKey(resId, useRgb565);

        // 从弱引用缓存中获取
        WeakReference<Bitmap> ref = stringWeakReferenceBitmap.get(key);
        if (ref != null) {
            bitmap = ref.get();
            if (bitmap != null) {
                if (Constants.IS_DEBUG) {
                    Log.d(TAG, "---- getCachedScaledDrawable cache hit, key:" + key);
                }

                // 重建Drawable对象，并且densityDpi要一致，否则BitmapDrawable会对再做一次scale
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                drawable.setTargetDensity(resource.getDisplayMetrics().densityDpi);

                if (DEBUG_SCALE) {
                    Log.d(TAG, "drawable getIntrinsicWidth: " + drawable.getIntrinsicWidth()
                            + " getIntrinsicHeight: " + drawable.getIntrinsicHeight());
                }
                return drawable;
            }
        }

        try {
            // 获取缩放后的Bitmap
            bitmap = getScaledBitmap(resource, resId, useRgb565);

            if (bitmap == null) {
                // 为空，则走默认的Resources API，会做scale缩小，并且有Android框架的Drawable缓存机制来保证复用
                Drawable drawable = resource.getDrawable(resId);

                if (DEBUG_SCALE) {
                    Log.d(TAG, "getDrawable getIntrinsicWidth: " + drawable.getIntrinsicWidth()
                            + " getIntrinsicHeight: " + drawable.getIntrinsicHeight());

                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
                        Log.d(TAG, "bitmap byte: " + getBitmapByteCount(bm) + " density=" + bm.getDensity()
                                + " width: " + bm.getWidth() + " height: " + bm.getHeight());
                    }
                }
                return drawable;
            }

            // 重建Drawable对象，并且densityDpi要一致，否则BitmapDrawable会对再做一次scale
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            drawable.setTargetDensity(resource.getDisplayMetrics().densityDpi);

            if (DEBUG_SCALE) {
                Log.d(TAG, "drawable getIntrinsicWidth: " + drawable.getIntrinsicWidth()
                        + " getIntrinsicHeight: " + drawable.getIntrinsicHeight());
            }

            // 缓存Bitmap对象
            stringWeakReferenceBitmap.put(key, new WeakReference<Bitmap>(bitmap));

            return drawable;
        } catch (Exception e) {
            e.printStackTrace();

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            // decode failed, maybe a ColorDrawable..
            // just return System decode result
            return resource.getDrawable(resId);
        }
    }

    /**
     * 获取经过缩小压缩的Drawable，如果弱引用缓存存在则获取，不存在则decode出来
     *
     * @param resource      resource
     * @param resId         drawableId
     * @return
     */
    public static Drawable getCachedScaledDrawable(Resources resource, int resId) {
        return getCachedScaledDrawable(resource, resId, false);
    }

    /**
     * 获取没有缩放的BitmapDrawable， 并且使用RGB565来解码图片, 在xxhdpi的机器上，可以将内存占用由1M缩小到130k
     *
     * @param resources
     * @param resId
     * @return
     */
    public static Drawable getCachedScaledDrawableRGB565(Resources resources, int resId) {
        return getCachedScaledDrawable(resources, resId, true);
    }

    /**
     * 给控件背景设置BitmapDrawable
     *
     * @param view      设置view
     * @param resId     资源id
     */
    public static void setBackgroundScaledDrawable(View view, int resId) {
        if (view != null) {
            view.setBackgroundDrawable(getCachedScaledDrawable(view.getResources(), resId, false));
        }
    }

    /**
     * 给控件背景设置BitmapDrawable
     *
     * @param view      设置view
     * @param resId     资源id
     * @param useRgb565     是否使用RGB565来解码图像，在百分百确定资源没有Alpha通道时可以使用
     */
    public static void setBackgroundScaledDrawable(View view, int resId, boolean useRgb565) {
        if (view != null) {
            view.setBackgroundDrawable(getCachedScaledDrawable(view.getResources(), resId, useRgb565));
        }
    }

    /**
     * 根据源density和目标density计算scale
     *
     * @param size
     * @param sdensity
     * @param tdensity
     * @return
     */
    public static int scaleFromDensity(int size, int sdensity, int tdensity) {
        if (sdensity == Bitmap.DENSITY_NONE || tdensity == Bitmap.DENSITY_NONE || sdensity == tdensity) {
            return size;
        }
        return ((size * tdensity) + (sdensity >> 1)) / sdensity;
    }
}
