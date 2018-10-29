package com.huyingbao.beatbox;

/**
 * 管理资源文件名,用户应该看到的文件名以及其他一些相关信息
 * Created by liujunfeng on 2018/10/29.
 */
public class Sound {
    private String mAssetPath;
    private String mName;
    private Integer mSoundId;//SoundPool加载的音频文件都有自己的ID

    public Sound(String assetPath) {
        mAssetPath = assetPath;
        //分离文件名
        String[] components = assetPath.split("/");
        String fileName = components[components.length - 1];
        //删除.wav后缀
        mName = fileName.replace(".wav", "");
    }

    public String getAssetPath() {
        return mAssetPath;
    }

    public String getName() {
        return mName;
    }

    public Integer getSoundId() {
        return mSoundId;
    }

    public void setSoundId(Integer soundId) {
        mSoundId = soundId;
    }
}
