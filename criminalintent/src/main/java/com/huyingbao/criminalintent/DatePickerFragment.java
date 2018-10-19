package com.huyingbao.criminalintent;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 1:使用FragmentManager管理对话框,可以更灵活地显示对话框.
 * 2:如果旋转屏幕,单独使用的AlertDialog会消失,
 * 而封装在fragment中的AlertDialog不会消失,会被重建恢复
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "extra_date";

    private static final String ARG_DATE = "date";
    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateDialog:一般用户创建替代传统的Dialog对话框的场景,UI简单,功能单一
     * onCreateView:一般用户创建复杂内容弹框或全屏展示效果场景,一般有网络请求等异步操作
     * onCreateView创建的DialogFragment,在对话框界面上看不到标题区域和按钮区域,需要在布局文件中创建
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //calendarViewShow新版本中会忽略该属性
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_date_picker, null);
        mDatePicker = view.findViewById(R.id.crime_date_picker);
        mDatePicker.init(year, month, day, null);
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.date_picker_title)
                .setView(view)
                //positive,negative,neutral三种可用按钮
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    int yearResult = mDatePicker.getYear();
                    int monthResult = mDatePicker.getMonth();
                    int dayResult = mDatePicker.getDayOfMonth();
                    Date dateResult = new GregorianCalendar(yearResult, monthResult, dayResult).getTime();
                    sendResult(Activity.RESULT_OK, dateResult);
                })
                .create();
    }

    /**
     * 通知关联的fragment返回信息
     *
     * @param resultCode 结果码
     * @param date       返回信息
     */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
