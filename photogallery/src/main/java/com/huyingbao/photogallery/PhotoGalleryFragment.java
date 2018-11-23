package com.huyingbao.photogallery;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

}
