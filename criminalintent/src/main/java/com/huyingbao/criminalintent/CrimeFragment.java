package com.huyingbao.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.huyingbao.criminalintent.model.Crime;
import com.huyingbao.criminalintent.model.CrimeLab;

import java.util.Date;
import java.util.UUID;


/**
 * 是一个可以复用的构建单元
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private CheckBox mSolvedCheckBox;
    private Crime mCrime;

    /**
     * 使用Arguments保存初始数据更直观有利于维护
     *
     * @param uuid
     * @return
     */
    public static CrimeFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID, uuid);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(crimeId);
    }

    /**
     * 创建和配置fragment视图
     *
     * @param inflater
     * @param container          父视图
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //false 告诉布局生成器不将生成的视图添加给父视图,以代码形式添加
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(v -> {
            //类似activity间的关联,通过setTargetFragment(...)方法可以将fragment之间关联起来
            //目标fragment和请求代码由FragmentManager负责跟踪管理
            //父activity接收到Activity.onActivityResult(...)方法调用命令后,
            //其FragmentManager会调用对应fragment的Fragment.onActivityResult(...)方法
            FragmentManager fragmentManager = getFragmentManager();
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
            datePickerFragment.setTargetFragment(this, REQUEST_DATE);
            datePickerFragment.show(fragmentManager, DIALOG_DATE);
        });

        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCrime.setSolved(isChecked);
        });

        //启动一个隐式Intent 发送消息
        mReportButton = view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            //EXTRA信息使用Intent类中定义的常量,任何响应该intent的activity都知道这些常量
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
            //创建一个选择器显示响应隐式intent的全部activity
            intent = Intent.createChooser(intent, getString(R.string.send_report));
            startActivity(intent);
        });
        //启动一个隐式Intent来选择联系人
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //过滤器验证代码
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(v -> {
            startActivityForResult(pickContact, REQUEST_CONTACT);
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        //检查是否存在联系人应用
        //Android设备上安装了哪些组件以及包括哪些activity,PackageManager类全都知道
        PackageManager packageManager = getActivity().getPackageManager();
        //flag标志MATCH_DEFAULT_ONLY限定只搜索带CATEGORY_DEFAULT标志的activity
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        //使用android.permission.READ_CONTACTS权限.可以查询到ContactsContract.Contacts._ID
        //然后使用联系人ID查询CommonDataKinds.Phone表
        //使用电话URI创建一个隐士intent:Uri number = Uri.parse("tel:5551234");
        //Intent.ACTION_DIAL拨号,等用户发起通话
        //Intent.ACTION_CALL直接调出手机应用并拨打来自intent的电话号码
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //更新数据
        CrimeLab.get(getContext()).updateCrime(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;
            case REQUEST_CONTACT:
                //获取联系人姓名
                //联系人应用有使用联系人数据库的全部权限
                //联系人应用返回包含在intent中的URI数据该父activity时,
                //会添加一个Intent.FLAG_GRANT_READ_URI_PERMISSION标志
                //该标志告诉Android应用的父activity可以使用联系人数据一次
                //我们不需要访问整个数据库,只需要访问其中的一条联系人信息
                //ACTION_PICK启动activity并要求返回,该intent包含数据URI
                //这个URI是数据定位符,指向用户所选的联系人
                Uri contactUri = data.getData();
                //Android提供了一个深度定制的API用户处理联系人信息:ContentProvider类
                //ContentProvider的实例封装了联系人数据库并提供给其他应用使用
                //可以通过ContentResolver访问ContentProvider
                //指定想查询的数据中的字段
                String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
                //执行查询
                Cursor cursor = getContext().getContentResolver()
                        .query(contactUri, queryFields, null, null, null);
                try {
                    if (cursor.getCount() == 0) return;
                    cursor.moveToFirst();
                    String suspect = cursor.getString(0);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                } finally {
                    cursor.close();
                }
                break;
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        //使用Android 提供的DateFormat 格式化工具类
        String dateString = (String) DateFormat.format(dateFormat, mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (TextUtils.isEmpty(suspect)) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    /**
     * 隐式Intent的主要组成部分
     * 1:action 要执行的操作,通常以Intent类中的常量来表示.
     *        要访问某个URL:Intent.ACTION_VIEW
     * 2:data 待访问数据的位置(网页URL,文件URI,ContentProviderURI)
     * 3:type 操作设计的数据类型 MIME形式的数据类型(text/html或者audio/mpeg3)
     *        如果一个intent包含数据位置,可以推测出数据的类型
     * 4:category:可选类别,用来描述打算何时,何地或者如何使用某个activity.
     *        android.intent.category.INFO表明activity向用户显示了包信息,但是它不应该出现在启动器中
     *
     * 操作系统在寻找适用的activity时,不会使用附加在隐式intent上的任何extra
     * 显示intent可以使用隐式intent的操作和数据部分,相当于要求特定activity去做特定的事.
     */
}
