package com.allure.study.quote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Allure on 2017/9/6.
 */

public class SoftReferenceTest {

    //保存软引用对象
    private Map<String,SoftReference> softReferenceMap=new HashMap<>();


    /**
     *添加软引用
     * @param path
     */
    public  void addBitmap(String path){
        // 强引用的Bitmap对象
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        // 软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
        // 添加softBitmap到Map中使其缓存
        softReferenceMap.put(path, softBitmap);
    }

    /**
     *得到软引用
     * @param path
     * @return
     */
    public Bitmap getBitmap(String path) {
        // 从缓存中取软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = softReferenceMap.get(path);
        // 判断是否存在软引用
        if (softBitmap == null) {
            return null;
        }
        // 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
        Bitmap bitmap = softBitmap.get();
        return bitmap;
    }


}
