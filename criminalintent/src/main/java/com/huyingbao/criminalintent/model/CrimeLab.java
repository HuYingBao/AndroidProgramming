package com.huyingbao.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        //CrimeLab是一个单例,它的生命周期同应用进程的生命周期
        //如果使用Activity作为上下文,会阻止垃圾回收器的清理
        mContext = context.getApplicationContext();
        //打开/data/data/[包名]/database/[数据库名].db数据库
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    /**
     * 数据库cursor之所以被称为cursor,是因为它内部就像有根手指似的,
     * 总是指向查询的某个地方.
     *
     * @return
     */
    public List<Crime> getCrimes() {
        ArrayList<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursorWrapper = queryCrimes(null, null);
        //移动cursor中的虚拟手指指向第一个元素
        cursorWrapper.moveToFirst();
        try {
            while (!cursorWrapper.isAfterLast()) {
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        } finally {
            //需要关闭cursor
            cursorWrapper.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursorWrapper = queryCrimes(
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        try {
            if (cursorWrapper.getColumnCount() == 0) return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        } finally {
            //return之后 finally 代码块也会被执行
            cursorWrapper.close();
        }
    }

    /**
     * 插入记录
     *
     * @param crime
     */
    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    /**
     * 更新
     *
     * @param crime
     */
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        //很多时候String本身包含SQL代码,如果将它直接放入query语句中
        //这些代码可能会改变query语句的含义,甚至会修改数据库资料
        //这就是SQL脚本注入
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    /**
     * 查询数据
     * Cursor是个神奇的表数据处理工具,其功能是封装数据表中的原始字段值.
     *
     * @param whereCause
     * @param whereArgs
     * @return
     */
    private CrimeCursorWrapper queryCrimes(String whereCause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null,
                whereCause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }


    /**
     * ContentValues 负责处理数据库写入和更新操作的类
     * 是一个键值存储类,类似Bundle
     * k
     *
     * @param crime
     * @return
     */
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }
}
