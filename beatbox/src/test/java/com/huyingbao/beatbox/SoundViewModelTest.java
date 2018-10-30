package com.huyingbao.beatbox;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 写单元测试最方便的方式是使用测试框架
 * 可以集中编写和运行测试案例，并支持在studio中看到测试结果
 * <p>
 * 单元测试类，运行在本地开发机上，可以脱离Android运行时环境，速度快
 * Created by liujunfeng on 2018/10/30.
 */
public class SoundViewModelTest {
    private BeatBox mBeatBox;
    private Sound mSound;
    //该名字使用习惯约定
    //1:很清楚知道mSubject是要测试的对象（与其他对象区别开来）
    //2:省去测试方法复制时，重命名的麻烦
    private SoundViewModel mSubject;

    /**
     * 测试类也需要创建对象实例以及它依赖的其他对象
     * 以@Before注解的保函公共代码的方法会在所有测试之前运行一次
     * 按照约定，所有单元测试类都要有以@Before注解的setUp()方法
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        //BeatBox是复杂类，容易出问题，需要创建虚拟对象
        //要用Mockito创建虚拟对象，需要传入要虚拟的类，调用mock(Class)静态方法
        //虚拟对象假扮成其他不相干的组件，其作用就是隔离受测对象
        mBeatBox = mock(BeatBox.class);
        //Sound是简单的数据对象，不容易出问题，不需要虚拟它
        mSound = new Sound("assetPath");
        mSubject = new SoundViewModel(mBeatBox);
        mSubject.setSound(mSound);
    }

    @Test
    public void exposesSoundNameAsTitle() {
        //断定测试对象获取标题方法和sound的获取文件名方法返回相同的值
        //如果不同测试失败
        assertThat(mSubject.getTitle(), is(mSound.getName()));
    }

    @Test
    public void callsBeatBoxPlayOnButtonClicked() {
        mSubject.onButtonClicked();
        //使用流接口
        //verify(mBeatBox);我要验证mBeatBox对象的某个方法是否调用了
        //mBeatBox.play(mSound);验证这个方法是这样调用的
        //验证以mSound作为参数，调用了mBeatBox对象的play(...)方法
        verify(mBeatBox).play(mSound);
    }
}