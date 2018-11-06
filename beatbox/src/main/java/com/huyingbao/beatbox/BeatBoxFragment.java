package com.huyingbao.beatbox;

import android.content.res.Resources;
import android.content.res.TypedArray;
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
 * 可以销毁和重建fragment的视图，但fragment自身可以不被销毁
 * 设备配置发生改变时，FragmentManager首先销毁队列中fragment的视图，
 * 理由：新的配置可能需要新的资源来匹配；当有更合适的资源可用时，则应重建视图。
 * <p>
 * fragment进入保留状态的条件
 * 1:已调用fragment的setRetainInstance(true)方法
 * 2:因设备配置改变(通常为设备旋转)，托管activity正在被销毁
 * <p>
 * fragment只能保留非常短的时间，即从fragment脱离旧activity到重新附加给快速新建的activity之间的时间
 * <p>
 * 除非不得已，不要使用保留fragment
 * 1:已保留fragment用起来更复杂，出问题，排查耗时
 * 2:保留的fragment只能应付activity因设备旋转而被销毁的情况，不能适应因系统回收销毁activity的情况
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
        //该方法可保留fragment，已保留的fragment不会随activity一起被销毁
        //它会一直保留，并在需要时原封不动转给新的activity
        //对于已保留的fragment实例，其全部实例变量的值也会保持不变
        setRetainInstance(true);
        mBeatBox = new BeatBox(getContext());

        //获取主题属性
        Resources.Theme theme = getActivity().getTheme();
        int[] attrsToFetch = {R.attr.colorAccent};
        TypedArray a = theme.obtainStyledAttributes(R.style.AppTheme, attrsToFetch);
        int accentColor = a.getInt(0, 0);
        a.recycle();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
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
