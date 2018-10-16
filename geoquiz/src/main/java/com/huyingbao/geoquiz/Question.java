package com.huyingbao.geoquiz;

/**
 * 模型对象存储着应用的数据和业务逻辑
 * 模型类通常用来映射与应用相关的一切事物
 * 不关心用户界面,为存储和管理应用数据而生
 * Created by liujunfeng on 2018/10/16.
 */
public class Question {
    /**
     * 问题文本
     */
    private int mTextResId;
    /**
     * 问题答案
     */
    private boolean mAnswerTrue;

    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
