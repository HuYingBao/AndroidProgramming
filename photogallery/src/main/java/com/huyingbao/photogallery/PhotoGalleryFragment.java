package com.huyingbao.photogallery;


import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;


    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //调用execute()方法会启动AsyncTask
        //进而触发后台线程并调用doInBackground(...)方法
        new FetchItemsTask().execute();

        //将主线程关联的Handler传递给ThumbnailDownloader
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        //设置监听器，监听并回调处理已下载图片
        mThumbnailDownloader.setThumbnailDownloadListener((target, thumbnail) -> {
            //使用新下载的Bitmap来设置PhotoHolder的Drawable
            Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
            target.bindDrawabe(drawable);
        });
        mThumbnailDownloader.start();
        //getLooper()方法必须在start()方法之后调用
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view);
        //在fragment的视图创建时就计算并设置好网格列数。但是这时RecyclerView还没有改变
        //可以实现ViewTreeObserver.OnGlobalLayoutListener监听器方法和计算列数的onGlobalLayout()方法
        //然后使用addOnGlobalLayoutListener()把监听器添加个RecyclerView视图。
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        setupAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //视图销毁时，调用清理方法
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //一定要终止HandlerThread否后会一直执行下去
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    /**
     * 设置适配器
     */
    private void setupAdapter() {
        //配置adapter前，应检查isAdded()的返回值为true。
        //该检查确认fragment已与目标activity想关联，从而保证getActivity()方法返回结果非空。
        //非网络接口数据，所有的方法调用都是由系统框架的回调方法驱动的，
        //fragment可脱离任何activity而独立存在。
        //如果fragment在接收回调指令，它必然关联着某个activity。

        //调用AsyncTask，寿命正在从后台进程触发回调指令。
        //因而不能确定fragment是否关联着activity。
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    /**
     * 线程是个单一执行序列，单个线程中的代码会逐步执行。
     * 所有Android应用的运行都是从主线程开始的。
     * 主线程处于一个无线循环的运行状态，等着用户或者系统触发事件。
     * 一旦有事件触发，主线程变执行代码做出响应。
     * <p>
     * 主线程运行着所有更新UI的代码，其中包括响应activity的启动、按钮的点击等不同UI相关事件的代码。
     * 事件处理循环让UI代码总是按顺序执行。
     * <p>
     * cancel(false)，只是简单地设置isCancel()的状态为true。
     * 随后，AsyncTask会检查isCanceled()状态，然后选择提前结束运行。
     * <p>
     * cancel(true)，会立刻终止doInBackground(...)方法当前所在的线程。
     * <p>
     * 第一个类型参数可指定将要转给execute(...)方法的输入参数的类型，
     * 进而确定doInBackground(...)方法输入参数的类型。
     * execute(...)方法接受一个或多个参数
     * 然后再把这些变量参数传递给doInBackground(...)方法。
     * <p>
     * 第二个类型参数可以指定发送进度更新需要的类型
     * 进度更新通常发生在后台进程执行中途。
     * AsyncTask提供了
     * 从doInBackground(...)方法中调用publishProgress(...)方法，
     * 这样onProgressUpdate(...)方法便能够在UI线程上调用。
     * <p>
     * AsyncTaskLoader是个抽象的Loader。它可以使用AsyncTask吧数据加载工作转移到其他线程上。
     * Loader用来从某些数据源加载数据。
     * 数据源可以是磁盘、数据库、ContentProvider、网络，甚至是另一进程。
     * 遇到类似设备旋转这样的场景时，LoaderManager会帮我们妥善处理loader及其加载的数据。
     * LoaderManager还负责启动和停止loader，以及管理loader的生命周期。
     * 设备配置改变后，如果初始化一个已经加载完数据的loader，它能立即提交数据，
     * 而不是再次尝试获取数据。无论fragment是否得到保留，它都会这样做。
     * <p>
     * AsyncTask是执行后台线程的最简单方式，但它不适用于重复且长时间运行的任务。
     */
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            return new FlickrFetcher().fetchItems();
        }

        /**
         * 1：在doInBackground(...)方法执行完毕后才会运行
         * 2：在主线程运行
         *
         * @param galleryItems
         */
        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawabe(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            GalleryItem galleryItem = mGalleryItems.get(i);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_background);
            photoHolder.bindDrawabe(drawable);
            //下载url中图片到viewHolder中的view中
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    /**
     * Android系统中，线程使用的收件箱叫作消息队列(message queue)。
     * 使用消息队列的线程叫作消息循环(message loop)。
     * 消息循环会循环检查消息队列上是否有新消息。
     * 消息循环由线程和looper组成。Looper对象管理着线程的消息队列。
     *
     * 主线程就是个消息循环，因此也拥有looper。
     * 主线程的所有工作都是由其looper完成的。
     * looper不断从消息队列中抓取消息，然后完成消息指定的任务。
     *
     * 消息是Message类的一个实例
     * 1 what：用户定义的int型消息代码，用来描述消息
     * 2 obj：用户指定，随消息发送的对象
     * 3 target：处理消息的Handler。
     *
     * Message的目标(target)是一个Handler类实例。
     * Handler可以看作message handler的简称。
     * 创建Message时，它会自动与一个Handler相关联。
     * Message待处理时，Handler对象负责触发消息处理事件。
     *
     * 要处理消息以及消息指定的任务，首先需要一个Handler实例。
     * Handler不仅仅是处理Message的目标(target),也是创建和发布Message的接口。
     *
     * Looper拥有Message对象的收件箱，所以Message必须在Looper上发布或处理。
     * 既然有这层关系，为协同工作，Handler总是引用着Looper.
     *
     * 一个Handler仅与一个Looper相关联，一个Message也仅与一个目标Handler相关联。
     * Looper拥有整个Message队列。多个Message可以引用同一目标Handler。
     * 多个Handler也可以与一个Looper相关联。
     * 一个Handler的Message可能与另一个Handler的Message存放在同一消息队列中。
     *
     * 不应手动设置消息的目标Handler。创建消息时，最好调用Handler.obtainMessage(...)方法。
     * 传入其他必要消息字段后，该方法会自动设置目标Handler。
     *
     * 为避免反复创建新的Message对象，Handler.obtainMessage(...)方法会从公共回收池里获取消息。
     *
     * 一旦取得Message，就可以调用sendToTarget()方法将其发送给它的Handler。
     * 然后，Handler会将这个Message放置在Looper消息队列的尾部。
     *
     *
     *
     */
}
