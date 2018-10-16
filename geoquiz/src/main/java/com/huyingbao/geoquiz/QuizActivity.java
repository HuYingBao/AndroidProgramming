package com.huyingbao.geoquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 答题测试界面
 * activity负责管理用户与应用界面的交互
 * Android包名遵循DNS反转约定,保证包名称的唯一性
 * MVC的架构模式
 * 控制器对象包含有应用的逻辑单元,是视图对象与模型对象的联系纽带.
 * 控制器对象响应视图对象触发的各类事件,
 * 管理着模型对象与视图层间的数据流动
 */
public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private TextView mQuestionTextView;

    private int mCurrentIndex = 0;

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
        //根据传入的布局资源ID参数
        //该方法生成指定布局的视图并将其放置在屏幕上
        //布局视图生成后,布局文件包含的组件也随之以各自的属性定义完成实例化
        setContentView(R.layout.activity_quiz);
        //布局是一种资源
        //资源是应用非代码形式的内容
        //使用资源ID在代码中获取对应的资源
        setTitle(R.string.app_name);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mQuestionTextView = findViewById(R.id.question_text_view);

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
            updateQuestion();
        });
    }

    /**
     * 更新问题
     */
    private void updateQuestion() {
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
    }

    /**
     * 查看答题对错
     *
     * @param userPressedTrue
     */
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = userPressedTrue == answerIsTrue ? R.string.correct_toast : R.string.incorrect_toast;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
