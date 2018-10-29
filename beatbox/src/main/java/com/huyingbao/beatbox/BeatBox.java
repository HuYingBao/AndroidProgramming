package com.huyingbao.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
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

    private AssetManager mAssetManager;
    private List<Sound> mSounds = new ArrayList<>();

    public BeatBox(Context context) {
        //所有Context中的AssetManager都管理着同一套assets资源
        mAssetManager = context.getAssets();
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
                mSounds.add(sound);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not list assets", e);
            e.printStackTrace();
        }
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
