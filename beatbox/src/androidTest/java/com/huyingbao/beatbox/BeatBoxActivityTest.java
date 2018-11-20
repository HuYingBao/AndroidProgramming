package com.huyingbao.beatbox;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

/**
 * 注解表明，这是一个Android工具测试
 * 需要activity和其他Android运行时环境支持
 * <p>
 * 整合测试场景中，虚拟对象显然不能用来隔离应用，
 * 相反我们用它把应用和外部交互对象隔离开来，如提供web service假数据和假反馈
 * 做整合测试时，最好避免使用想Mockito这样的自动虚拟测试框架
 * Created by liujunfeng on 2018/10/30.
 */
@RunWith(AndroidJUnit4.class)
public class BeatBoxActivityTest {
    /**
     * 该注解告诉JUnit,运行测试之前，要启动一个BeatBoxActivity实例
     */
    @Rule
    public ActivityTestRule<BeatBoxActivity> mActivityTestRule =
            new ActivityTestRule<>(BeatBoxActivity.class);

    /**
     * 断定屏幕上某个视图显示了第一个sample_sounds受测文件的文件名
     */
    @Test
    public void showFirstFileName() {
        //onView(withText("65_cjipie"))这行代码会找到显示对应文字的视图，然后对其执行测试
        //check(matches(anything)))用来判定有这样的视图
        //相较于JUnit的assertThat(...)断言方法，check(...)方法是Espresso版的断言方法
        onView(withText("65_cjipie")).check(matches(anything()));
        //点击某个视图，使用断言验证点击效果
        onView(withText("65_cjipie")).perform(click());
    }

}