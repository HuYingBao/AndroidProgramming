package com.huyingbao.photogallery;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by liujunfeng on 2018/11/26.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;//标识下载请求消息
    private boolean mHasQuit = false;
    //负责在ThumbnailDownloader后台线程上管理下载请求消息队列。
    //负责从消息队列里取出并处理下载请求消息。
    private Handler mRequestHandler;
    //这是一种线程安全的HashMap，用一个标记下载请求的T类型对象（PhotoHolder）作为key，
    //可以存取和请求关联的URL下载链接。
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();


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
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
        if (TextUtils.isEmpty(url)) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.putIfAbsent(target, url);
            mRequestHandler
                    .obtainMessage(MESSAGE_DOWNLOAD, target)//获取一个新的Message对象，设置target当前
                    .sendToTarget();
        }
    }
}
