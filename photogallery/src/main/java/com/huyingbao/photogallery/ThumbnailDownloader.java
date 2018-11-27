package com.huyingbao.photogallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by liujunfeng on 2018/11/26.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private boolean mHasQuit = false;

    public ThumbnailDownloader() {
        super(TAG);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    /**
     * @param target 标识具体哪次下载
     * @param url
     */
    public void queueThumnail(T target,String url){
        Log.i(TAG,"Got a URL: "+url);
    }
}
