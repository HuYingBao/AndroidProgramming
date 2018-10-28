package com.huyingbao.beatbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyingbao.beatbox.databinding.FragmentBeatBoxBinding;

/**
 * Created by liujunfeng on 2018/10/24.
 */
public class BeatBoxFragment extends Fragment {
    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //实例化绑定类
        FragmentBeatBoxBinding beatBoxBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_beat_box, container, false);
        //不用findViewById(...),使用数据绑定获取视图
        //配置RecyclerView
        beatBoxBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        return beatBoxBinding.getRoot();
    }
}
