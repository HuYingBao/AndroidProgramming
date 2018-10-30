package com.huyingbao.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取assets资源
 * Created by liujunfeng on 2018/10/29.
 * 为什么使用assets
 * BeatBox应用要用到很多声效，20多个文件
 * 如果使用Android资源系统一个个去处理，效率会很低
 * 资源系统不能全放在一个目录下管理
 * assets可以看作随应用打包的微型文件系统，支持任意层次的文件目录结构。
 * 常用来加载大量图片和声音资源，游戏应用中就是这样使用的
 */
public class BeatBox {
    //记录日志
    private static final String TAG = "BeatBox";
    //存储声音资源文件目录名
    private static final String SOUNDS_FOLODERS = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssetManager;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    /**
     * Sound类中的一切可以保存，SoundPool无法保存
     * 不可保存性有向外传递的倾向、
     * 如果一个对象重度依赖另一个不可保存的对象，
     * 那么这个对象很可能也无法保存
     * @param context
     */
    public BeatBox(Context context) {
        //所有Context中的AssetManager都管理着同一套assets资源
        mAssetManager = context.getAssets();
        //创建SoundPool
        mSoundPool = new SoundPool(
                MAX_SOUNDS,//指定同时播放多少个音频
                AudioManager.STREAM_MUSIC,//音频流类型，STREAM_MUSIC是音乐和游戏常用的音量控制常量
                0);
        loadSounds();
    }

    /**
     * 查看assets资源
     * 生成文件名列表
     * Sound对象定义了assets文件路径，使用File对象无法打开资源文件
     * 应该使用AssetManager
     */
    private void loadSounds() {
        String[] soundNames;
        try {
            //列出指定目录下的所有文件名
            soundNames = mAssetManager.list(SOUNDS_FOLODERS);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
            //生成文件名列表
            for (String filename : soundNames) {
                String assetPath = SOUNDS_FOLODERS + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);//载入全部音频文件
                mSounds.add(sound);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not list assets", e);
            e.printStackTrace();
        }
    }

    /**
     * 加载音频文件
     *
     * @param sound
     * @throws IOException
     */
    private void load(Sound sound) throws IOException {
        //Asset文件修饰符
        AssetFileDescriptor assetFileDescriptor = mAssetManager.openFd(sound.getAssetPath());
        //load()方法可以把文件载入SoundPool待播。
        //为了方便管理、重播或卸载音频文件，load()方法会返回一个int型的ID
        //这实际就是存储在mSoundId中的ID
        int soundId = mSoundPool.load(assetFileDescriptor, 1);
        sound.setSoundId(soundId);
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) return;
        //音频ID、左音量、右音量、优先级、是否循环、播放速率
        mSoundPool.play(soundId, 0.4f, 0.4f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
