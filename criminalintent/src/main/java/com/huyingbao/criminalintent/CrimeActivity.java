package com.huyingbao.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * 1:在其布局中为fragment的视图安排位置
 * 2:管理fragment实例的生命周期
 * Activity需要了解托管的Fragment的内部实现
 * Fragment不一定需要知道其托管activity的细节问题
 */
public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "crime_id";

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(uuid);
    }
}
