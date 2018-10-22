package com.huyingbao.criminalintent.model;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.huyingbao.criminalintent.database.CrimeDbSchema;

import java.util.Date;
import java.util.UUID;

/**
 * Cursor封装
 * 目的是定制新方法,以方便操作内部Cursor
 * Created by liujunfeng on 2018/10/22.
 */
public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * 将查询返回的cursor封装到CrimeCursorWrapper类中
     * 调用该方法遍历取出crime
     *
     * @return
     */
    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        return crime;
    }
}
