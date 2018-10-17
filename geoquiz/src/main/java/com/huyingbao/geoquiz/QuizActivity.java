package com.huyingbao.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * activity负责管理用户与应用界面的交互,任何时候只能有一个activity处于用户能交互的运行状态
 * <p>
 * Android包名遵循DNS反转约定,保证包名称的唯一性
 * <p>
 * Android依靠res子目录的配置修饰符定位最佳资源以匹配当前设备配置
 * <p>
 * 控制器对象包含有应用的逻辑单元,是视图对象与模型对象的联系纽带
 * <p>
 * 控制器对象响应视图对象触发的各类事件, 管理着模型对象与视图层间的数据流动
 * <p>
 * ActivityManager维护着一个非特定应用独享的回退栈
 */
public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    /**
     * 问题数组
     */
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setTitle(R.string.app_name);

        //根据传入的布局资源ID参数
        //该方法生成指定布局的视图并将其放置在屏幕上
        //布局视图生成后,布局文件包含的组件也随之以各自的属性定义完成实例化
        //布局是一种资源
        //资源是应用非代码形式的内容
        //使用资源ID在代码中获取对应的资源
        setContentView(R.layout.activity_quiz);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mQuestionTextView = findViewById(R.id.question_text_view);

        //检查存储的bundle信息
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        //初始化数据
        updateQuestion();

        //Android应用属于典型的事件驱动类型,
        //事件驱动类型应用启动后,即开始开始等待行为事件的发生
        //应用监听特定事件的发生
        //监听器使用匿名监听器的好处
        //1:可以相对集中地实现监听器方法,一眼可见
        //2:事件监听器一般只在一个地方使用,使用匿名内部类,就可以不用去创建繁琐的命名类
        mTrueButton.setOnClickListener(v -> {
            checkAnswer(true);
        });
        mFalseButton.setOnClickListener(v -> {
            checkAnswer(false);
        });
        mNextButton.setOnClickListener(v -> {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;//索引加1,防止数组越界
            mIsCheater = false;
            updateQuestion();
        });
        mCheatButton.setOnClickListener(v -> {
            //intent 对象是component用来与操作系统通信的一种媒介工具
            Intent intent = CheatActivity.newIntent(this, mQuestionBank[mCurrentIndex].isAnswerTrue());
            //调用请求发送给了系统的ActivityManager
            //ActivityManager负责创建Activity实例并调用其onCreate(Bundle)方法
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_CODE_CHEAT:
                if (data == null) return;
                mIsCheater = CheatActivity.wasAnswerShown(data);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    /**
     * 1:设备旋转
     * 2:系统需要回收内存时
     * <p>
     * 该方法通常在 onStop() 方法之前由系统调用,除非用户按后退键
     * <p>
     * 调用该方法时,用户数据随即被保存在 Bundle 对象中,然后操作系统将 Bundle 对象放入activity记录中
     * activity暂存之后,Activity对象不再存在,但操作系统会将activity记录对象保存起来,
     * 在需要恢复activity时,操作系统可以使用暂存的记录重新激活activity
     * <p>
     * 用户按了后退键后，系统会彻底销毁当前的activity。此时，暂存的activity记录同时被清除。
     * <p>
     * Android从不会为了回收内存,而去销毁可见的activity
     * <p>
     * 通过其他方式保存定制类对象,在bundle中保存标识对象的基本数据类型
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState() called");
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    /**
     * 只有在调用过 onStop() 并执行完成后,activity才会被标为可销毁
     * 覆盖 onStop() 方法,保存永久性数据,如用户编辑的文字等
     * onStop() 方法调用完,activity随时会被系统销毁,所以用它保存永久性数据
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    /**
     * activity进入暂存状态并不一定需要调用 onDestroy() 方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    /**
     * 更新问题
     * 记录栈跟踪的诊断性日志
     */
    private void updateQuestion() {
        //可以创建一个全新的 Exception ，把它作为不抛出的异常对象传入该方法。
        //借此，我们得到异常发生位置的记录报告。
        Log.d(TAG, "Updating question text ", new Exception());
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
    }

    /**
     * 查看答题对错
     *
     * @param userPressedTrue
     */
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = mIsCheater ? R.string.judgment_toast :
                userPressedTrue == answerIsTrue ? R.string.correct_toast : R.string.incorrect_toast;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
