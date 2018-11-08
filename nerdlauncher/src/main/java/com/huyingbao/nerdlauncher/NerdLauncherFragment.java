package com.huyingbao.nerdlauncher;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NerdLauncherFragment extends Fragment {
    private static final String TAG = "NerdLauncher";
    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = view.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter();
        return view;
    }

    /**
     * startActivity(Intent)方法意味着“启动匹配隐式Intent的默认activity”，
     * 而不是“启动匹配隐式intent的activity”
     * 调用该方法，系统会为目标intent添加Intent.CATEGORY_DEFAULT类别。
     * <p>
     * 定义了
     * <action android:name="android.intent.action.MAIN" />
     * <category android:name="android.intent.category.LAUNCHER" />
     * intent-filter的activity是应用的主要入口点，可以不包含CATEGORY_DEFAULT类别
     */
    private void setAdapter() {
        Intent startUpIntent = new Intent(Intent.ACTION_MAIN);
        startUpIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(startUpIntent, 0);

        //对activity标签排序
        Collections.sort(resolveInfoList, (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(
                o1.loadLabel(packageManager).toString(),
                o2.loadLabel(packageManager).toString()));
        Log.i(TAG, "Found " + resolveInfoList.size() + " activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(resolveInfoList));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder {
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;

        public ActivityHolder(@NonNull View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView;
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager packageManager = getContext().getPackageManager();
            String appName = mResolveInfo.loadLabel(packageManager).toString();
            mNameTextView.setText(appName);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> mResolveInfoList;

        private ActivityAdapter(List<ResolveInfo> resolveInfoList) {
            mResolveInfoList = resolveInfoList;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder activityHolder, int i) {
            ResolveInfo resolveInfo = mResolveInfoList.get(i);
            activityHolder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mResolveInfoList.size();
        }
    }

}
