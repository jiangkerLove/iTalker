package com.example.lang.utils;

import android.util.Log;

/**
 * @author power sky
 * @version 1.0
 * @createTime 2020年03月26日
 * @Description 打印日志记录信息
 */
public class Journal {

    private static final String TAG = "ITalker";

    //禁止实例化
    private Journal(){}

    public static void d(String str){
        Log.d(TAG, str);
    }

    public static void i(String str){
        Log.d(TAG, str);
    }

    public static void w(String str){
        Log.d(TAG, str);
    }
}
