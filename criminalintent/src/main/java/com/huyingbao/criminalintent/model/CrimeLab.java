package com.huyingbao.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.huyingbao.criminalintent.database.CrimeBaseHelper;
import com.huyingbao.criminalintent.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 数据集中存储池
 * Android上都有一个沙盒目录,数据存储其中,其他应用无法查看
 * 应用的沙盒目录是/data/data/[应用包名]
 * Created by liujunfeng on 2018/10/18.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * Android开发常用到单例的一大原因是:它们比fragment或者activity活得久
     * 在屏幕旋转或者是在fragment和activity间跳转的场景下,单例不受影响.
     * 单例能方便地存储和控制模型对象.
     * 单例不利于单元测试,通过依赖注入的方式解决这个问题
     */
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        //打开/data/data/[包名]/database/[数据库名].db数据库
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id) {
        return null;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * ContentValues 负责处理数据库写入和更新操作的类
     * 是一个键值存储类,类似Bundle
     *k
     * @param crime
     * @return
     */
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return values;
    }
}
