package com.allure.study.utils;

/**
 * Created by luomin on 16/7/16.
 */
public class Constants {

    /**
     * APP全局DEBUG开关, 控制调试日志和调试逻辑
     * 发布前需要关闭改开关, 可以在gradle脚本中自动设置
     */
    public static boolean IS_DEBUG = true;
    /**
     * 是否在调试启动时间耗时
     */
    public static boolean DEBUG_TIME = IS_DEBUG;

    /**
     * The width of the display.
     */
    public static int DISPLAY_WIDTH = 0;

    /**
     * The height of the display.
     */
    public static int DISPLAY_HEIGHT = 0;

    /**
     * The logical density of the display.
     */
    public static float DISPLAY_DENSITY = 0.0f;

    /**
     * The exact DPI.
     */
    public static float DISPLAY_DPI_EXACT = 0.0f;
}
