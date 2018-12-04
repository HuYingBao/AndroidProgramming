package com.huyingbao.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 在线程上使用handler和消息，把消息放入自己的收件箱。
 *
 * AsyncTask应用于短暂且较少重复的任务
 * AsyncTask不是每一个AsyncTask实例单独创建线程，
 * 它使用一个Executor在单一的后台线程上运行所有AsyncTask后台任务，是需要排队执行的
 * Created by liujunfeng on 2018/11/26.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;//标识下载请求消息
    private boolean mHasQuit = false;
    //负责在ThumbnailDownloader后台线程上管理下载请求消息队列。
    //负责从消息队列里取出并处理下载请求消息。
    //使用该Handler，可以从主线程安排后台线程任务
    private Handler mRequestHandler;
    //这是一种线程安全的HashMap，用一个标记下载请求的T类型对象（PhotoHolder）作为key，
    //可以存取和请求关联的URL下载链接。
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    //主线程的Handler
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    /**
     * 监听器响应下载完成请求
     * 使用监听器把处理已下载图片的任务委托给另一个类（PhotoGalleryFragment）
     * 这样，ThumbnailDownloader就可以把下载结果传给其他视图对象。
     *
     * @param <T>
     */
    public interface ThumbnailDownloadListener<T> {
        /**
         * 主线程发请求，响应结果是下载的图片
         * 图片下载完成，可以交给UI去显示时，该方法就会被调用。
         *
         * @param target
         * @param thumbnail
         */
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    /**
     * ThumbnailDownloader可以使用与主线程Looper绑定的Handler
     *
     * @param responseHandler
     */
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    /**
     * 该方法实在Looper首次检查消息队列之前调用，是创建Handler的好地方。
     */
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            /**
             * 队列中的下载消息取出并可以处理时，就会触发调用该方法
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> thumbnailDownloadListener) {
        mThumbnailDownloadListener = thumbnailDownloadListener;
    }

    /**
     * 创建下载消息，并通知线程中的handler处理下载任务。
     *
     * @param target 标识具体哪次下载
     * @param url
     */
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
        if (TextUtils.isEmpty(url)) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.putIfAbsent(target, url);
            //消息自身包含URL
            mRequestHandler
                    .obtainMessage(MESSAGE_DOWNLOAD, target)//获取一个新的Message对象，设置target当前
                    .sendToTarget();
        }
    }

    /**
     * 添加清理方法，防止用户旋转屏幕，因PhotoHolder视图失效，导致异常
     */
    public void clearQueue() {
        mResponseHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    /**
     * target下载对应的url图片
     *
     * @param target
     */
    private void handleRequest(T target) {
        try {
            final String url = mRequestMap.get(target);
            if (TextUtils.isEmpty(url)) return;
            byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
            //Message设有回调方法属性后，取出队列的消息是不会发给target Handler的。
            //相反，存储在回调方法中的Runnable的run()方法会直接执行
            //mResponseHandler与主线程的Looper相关联，所以UI更新代码会在主线程中完成。
            mResponseHandler.post(() -> {
                //RecyclerView会循环使用其视图
                //如果已经退出，任何回调方法可能都不太安全
                if (mRequestMap.get(target) != url || mHasQuit) return;
                mRequestMap.remove(target);
                mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
            });
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
    }

    /**
     * 主线程是一个拥有handler和Looper的消息循环。
     * 主线程上创建的Handler会自动与它的Looper相关联。
     * 主线程上创建的这个Handler也可以传递给另一线程。
     * 传递出去的Handler与创建它的线程Looper始终保持着联系。
     * 已传出Handler负责处理的所有消息都将在主线程的消息队列中处理。
     */
}
