package com.huyingbao.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * 1:在其布局中为fragment的视图安排位置
 * 2:管理fragment实例的生命周期
 */
public class CrimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);
        //1:管理fragment队列
        //2:管理fragment事务回退栈
        FragmentManager fragmentManager = getSupportFragmentManager();

        //从fragment队列中获取资源ID标识的fragment
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new CrimeFragment();
            //fragment事务被用来添加,移除,附加,分离或替换fragment队列中的fragment
            //资源ID标识UI fragment是FragmentManager的一种内部实现机制
            //添加fragment供FragmentManager管理时
            //onAttach(Context),onCreate(Bundle)和onCreateView(...)方法会被调用
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
