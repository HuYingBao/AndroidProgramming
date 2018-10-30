package com.huyingbao.beatbox;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * 视图模型类
 * 分离模型层与视图层
 * BaseObservable 可以让绑定类在视图模型上设置监听器
 * 这样只要视图模型有变化,绑定类立即会接到回调.
 * Created by liujunfeng on 2018/10/29.
 */
public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    public Sound getSound() {
        return mSound;
    }

    /**
     * 每次可绑定的属性值改变时,调用该方法
     *
     * @param sound
     */
    public void setSound(Sound sound) {
        mSound = sound;
        //调用notifyChange()方法,就是通知绑定类,视图模型对象上所有可绑定属性都已更新
        notifyChange();
        //notifyPropertyChanged(int)方法，相当于说：只有getTitle()方法的值有变化
    }

    /**
     * 按钮显示文件名调用该方法
     * 使用@Bindable注解视图模型里可绑定的属性
     *
     * @return
     */
    @Bindable
    public String getTitle() {
        return mSound.getName();
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }
}
