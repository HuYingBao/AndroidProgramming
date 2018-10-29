package com.huyingbao.beatbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyingbao.beatbox.databinding.FragmentBeatBoxBinding;
import com.huyingbao.beatbox.databinding.ListItemSoundBinding;

import java.util.List;

/**
 * assets
 * 1:无需配置管理,随意命名,自己组织文件结构
 * 2:无法自动响应屏幕显示密度,语言等设备配置变更,无法在布局或其他资源里自动使用
 * <p>
 * 不管是哪种架构,都有:责任单一性原则,每个类应该只负责一件事情.
 * <p>
 * MVC是这样落实的:
 * 1:模型表明应用是如何工作的;
 * 2:控制器决定如何显示应用;
 * 3:视图显示你想看到的结果
 * <p>
 * MVVM
 * 从前控制器对象格式化视图数据的工作转给了视图模型对象.
 * 控制器对象(activity或fragment)开始负责初始化布局绑定类和视图模型对象,也是它们的纽带.
 * <p>
 * Created by liujunfeng on 2018/10/24.
 */
public class BeatBoxFragment extends Fragment {
    private BeatBox mBeatBox;

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeatBox = new BeatBox(getContext());
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
        beatBoxBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        beatBoxBinding.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));
        return beatBoxBinding.getRoot();
    }

    private class SoundHolder extends RecyclerView.ViewHolder {
        private ListItemSoundBinding mBinding;

        public SoundHolder(ListItemSoundBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            //关联使用视图模型
            mBinding.setViewModel(new SoundViewModel(mBeatBox));
        }

        public void bind(Sound sound) {
            mBinding.getViewModel().setSound(sound);
            //执行预定义的绑定
            mBinding.executePendingBindings();
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSoundList;

        public SoundAdapter(List<Sound> soundList) {
            mSoundList = soundList;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ListItemSoundBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.list_item_sound, viewGroup, false);
            return new SoundHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder soundHolder, int i) {
            Sound sound = mSoundList.get(i);
            soundHolder.bind(sound);
        }

        @Override
        public int getItemCount() {
            return mSoundList.size();
        }
    }
}
