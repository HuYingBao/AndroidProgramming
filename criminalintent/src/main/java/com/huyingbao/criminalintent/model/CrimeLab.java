package com.huyingbao.criminalintent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 数据集中存储池
 * Created by liujunfeng on 2018/10/18.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    /**
     * Android开发常用到单例的一大原因是:它们比fragment或者activity活得久
     * 在屏幕旋转或者是在fragment和activity间跳转的场景下,单例不受影响.
     * 单例能方便地存储和控制模型对象.
     * 单例不利于单元测试,通过依赖注入的方式解决这个问题
     */
    private CrimeLab() {
        mCrimes = new ArrayList<>();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab();
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }

    public void addCrime(Crime crime){
        mCrimes.add(crime);
    }
}
