package com.huyingbao.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.huyingbao.criminalintent.model.Crime;
import com.huyingbao.criminalintent.model.CrimeLab;

import java.util.List;
import java.util.UUID;

/**
 * 后退键导航:又称为临时性导 只能返回到上一次浏览过的用户界面 回退栈activity之间跳转
 * 层级式导航:可以在应用内逐级向上导航
 * 内部实现机制不一样
 * <p>
 * 点击向上按钮会调用如下代码
 * Intent intent = new Intent(this,CrimeListActivity.class);
 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 * startActivity(intent);
 * finish();
 * <p>
 * FLAG_ACTIVITY_CLEAR_TOP指示Android在回退栈中寻找指定的activity实例,
 * <p>
 * 如果实例存在,则弹出栈内所有其他activity,让启动的目标activity显示在栈顶
 * 导航回退的目标activity会被先销毁再完全重建
 * <p>
 * 向上导航的机制需要被覆盖
 */
public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "crime_id";

    /**
     * 当ViewPager托管非fragment视图时,需要实现原生PagerAdapter接口
     * <p>
     * RecyclerView需要处理大量内部实现,Adapter需要我们及时提供View
     * 而FragmentManager决定fragment的视图何时创建
     * 我们无法立即创建fragment并提供视图
     */
    private ViewPager mCrimeViewPager;
    private List<Crime> mCrimes;


    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    /**
     * FragmentStatePageAdapter会销毁不需要的fragment.
     * 事务提交后,activity的FragmentManager中的fragment会被彻底移除.
     * 'state'表明:在销毁fragment时,可在onSaveInstanceState(Bundle)方法中保存fragment的bundle信息.
     * 用户切换回来时,保存的实例状态可用来生成新的fragment
     * 大量不固定fragment需要显示时适用,更节约内存
     * <p>
     * FragmentPageAdapter对于不再需要的fragment,
     * FragmentPageAdapter会选择调用事务的detach(Fragment)方法来处理它,非remove(Fragment)方法
     * 只是销毁了fragment的视图,fragment实例还保留在FragmentManager中.
     * 创建的fragment永远不会被销毁
     * 少量固定的fragment时更安全
     * <p>
     * 1:布局文件能很好地分离控制器层和视图层对象,代码变更管理相对容易
     * 2:能使用Android的资源适配系统,实现按设备属性自动调用合适的布局文件
     * <p>
     * ViewPager的布局参数不支持边距设置
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        //ViewPager类似RecyclerView 都需要Adapter来支持
        mCrimeViewPager = findViewById(R.id.crime_view_pager);
        //定制预加载相邻页面的数目
        mCrimeViewPager.setOffscreenPageLimit(5);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //PagerAdapter将返回的fragment添加给托管Activity
        //并帮助ViewPager找到fragment的视图并一一对应
        mCrimeViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            /**
             * 1:获取数据集中指定位置的Crime实例
             * 2:利用该实例的ID创建并返回一个经过有效配置的CrimeFragment
             * @param i
             * @return
             */
            @Override
            public Fragment getItem(int i) {
                Crime crime = mCrimes.get(i);
                return CrimeFragment.newInstance(crime.getId());
            }

            /**
             * 返回数组列表中包含的列表项数目
             * @return
             */
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        //设置初始分页显示项
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(uuid)) {
                mCrimeViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    /**
     * PagerAdapter 不使用可返回视图的 onBindViewHolder(...) 方法，而是使用下列方法：
     *
     * public Object instantiateItem(ViewGroup container, int position)
     * public void destroyItem(ViewGroup container, int position, Object object)
     * public abstract boolean isViewFromObject(View view, Object object)
     *
     * PagerAdapter.instantiateItem(ViewGroup, int) 方法告诉pager adapter创建指定位置
     * 的列表项视图，然后将其添加给 ViewGroup 视图容器
     * 注意， instantiateItem(ViewGroup, int) 方法并不要求立即创建视图。
     * 因此， PagerAdapter 可自行决定何时创建视图。
     * 视图创建完成后， ViewPager 会在某个时间点看到它。
     *
     * PagerAdapter.destroyItem(ViewGroup, int, Object) 方法则告诉pager adapter销毁已建视图。
     *
     * 为确定该视图所属的对象， ViewPager 会 调 用 isViewFromObject(View, Object) 方 法 。
     * 这里 ， Object 参 数 是 instantiateItem (ViewGroup,int) 方法返回的对象。
     * 因此，假设 ViewPager 调用 instantiateItem(ViewGroup, 5) 方法返回A对象，
     * 那么只要传入的 View 参数是第5个对象的视图， isViewFromObject(View, A) 方法就应返回 true 值，
     * 否则返回 false 值。
     *
     * PagerAdapter 只要能够创建、销毁视图以及识别视图来自哪个对象即可
     */
}
