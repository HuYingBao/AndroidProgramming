package com.huyingbao.geoquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 答题测试界面
 * activity负责管理用户与应用界面的交互
 * Android包名遵循DNS反转约定,保证包名称的唯一性
 */
public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
    }
}
