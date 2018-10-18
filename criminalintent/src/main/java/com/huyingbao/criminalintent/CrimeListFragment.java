package com.huyingbao.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyingbao.criminalintent.model.Crime;
import com.huyingbao.criminalintent.model.CrimeLab;

import java.util.List;


/**
 * Fragment有自己的startActivityForResult(...)方法和onActivityResult(...)方法
 * 没有setResult(...)方法
 */
public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        //RecyclerView的任务仅限于回收和定位屏幕上的View
        //RecyclerView自身不会创建视图
        //RecyclerView视图创建完成后,立即转交给LayoutManager对象
        //RecyclerView类不会亲自摆放屏幕上的列表项
        //LayoutManager任务:1在屏幕上摆放列表项,2负责定义屏幕滚动行为
        //RecyclerView能转发触摸事件,也可以通过ViewHolder监听用户触摸事件
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getContext());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyDataSetChanged();
        }
    }


    /**
     * ViewHolder只是负责容纳View视图
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        /**
         * ViewHolder为itemView而生,它引用着传给super(view)的整个View视图
         *
         * @param inflater
         * @param parent
         */
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimeActivity.newIntent(getContext(), mCrime.getId());
            startActivity(intent);
        }
    }

    /**
     * Adapter是一个控制器对象,从模型层获取数据,然后提供给RecyclerView显示,是沟通的桥梁
     * 1:创建必要的ViewHolder
     * 2:绑定ViewHolder至模型层数据
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        /**
         * 第一个被RecyclerView调用
         * 只调用一次
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }


        /**
         * 第二个被RecyclerView调用
         * 屏幕能显示多少个itemView调用多少次
         * 当屏幕有了够用的ViewHolder,RecyclerView会停止调用onCreateViewHolder(...)
         *
         * @param viewGroup
         * @param i
         * @return
         */
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new CrimeHolder(layoutInflater, viewGroup);
        }

        /**
         * 第三个被RecyclerView调用
         * adapter会找到目标位置的数据并将其绑定到ViewHolder的视图上
         * 使用模型数据填充视图
         *
         * @param crimeHolder
         * @param i
         */
        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime crime = mCrimes.get(i);
            crimeHolder.bind(crime);
        }
    }
}
