package com.example.common.app;

import android.app.Application;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.example.lang.utils.Journal;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

public class BaseApplication extends Application {

    private static Application instance;

    @Override
    public void onCreate() {
        Journal.i("Application onCreate");
        super.onCreate();
        instance = this;
    }

    /**
     * 获取缓存文件夹地址
     * @return 当前App的缓存文件夹地址
     */
    public static File getCacheDirFile(){
        return instance.getCacheDir();
    }

    /**
     * 获取头像的临时储存文件地址
     * @return
     */
    public static File getPortraitTmpFile(){
        //得到头像目录的缓存地址
        File dir = new File(getCacheDirFile(),"portrait");
        // 创建所有的对应的文件夹
        dir.mkdirs();
        //删除一些旧的缓存文件
        File[] files = dir.listFiles();
        if(files != null && files.length > 0){
            for (File file : files) {
                file.delete();
            }
        }

        //返回一个当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis()+".jpg");
        return path.getAbsoluteFile();
    }


    /**
     * 获取声音文件的本地地址
     * @param isTmp 是否为缓存文件  true:每次返回的文件地址是一样的
     * @return 录音文件的地址
     */
    public static File getAudioTmpFile(boolean isTmp){
        File dir = new File(getCacheDirFile(),"audio");
        dir.mkdirs();
        File[] files = dir.listFiles();
        if(files != null && files.length > 0){
            for (File file : files) {
                file.delete();
            }
        }

        File path = new File(getCacheDirFile(), isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        return path.getAbsoluteFile();
    }

    /**
     * 显示Toast
     * @param msg 字符串
     * Toast只能在UI线程中显示
     */
    public static void showToast(final String msg){
        //handler的实现
        Run.onUiSync(()->Toast.makeText(instance,msg,Toast.LENGTH_SHORT).show());
    }

    /**
     * @param msgId 字符串资源
     */
    public static void showToast(@StringRes int msgId){
        showToast(instance.getString(msgId));
    }
}
