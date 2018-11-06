package com.huyingbao.beatbox;

import android.support.v4.app.Fragment;

/**
 * 在Android世界里，凡是要在屏幕上绘制的东西都可以叫作drawable，
 * 比如抽象图形、Drawable类的子类代码、位图图像等。
 *
 * XML drawable:与屏幕密度无关，无需考虑创建特定像素密度目录，直接放入drawable文件夹就可以了
 * 1:state list drawable
 * 2:shape drawable
 * 3:layer list drawable
 *
 */
public class BeatBoxActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}
