package com.teamll.expectlauncher.ui.main.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.widgets.fragmentnavigationcontroller.SupportFragment;
import com.teamll.expectlauncher.util.Tool;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutSetting extends SupportFragment {
    @BindView(R.id.back_button)
    ImageView mBackButton;
    RecyclerView mRecyclerView;

    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.name) TextView mName;
    @BindView(R.id.team) TextView mTeam;

    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    ArrayList<String[]> infor = new ArrayList<String[]>();

    public AboutSetting() {
        infor.add(new String[]{"Lê Đình Trung", "1612751", "Đại học KHTN, Tp. Hồ Chí Minh"});
        infor.add(new String[]{"Lê Văn Tư", "1612770", "Đại học KHTN, Tp. Hồ Chí Minh"});
        infor.add(new String[]{"Nguyễn Hữu Tứ", "1612772", "Đại học KHTN, Tp. Hồ Chí Minh"});
        infor.add(new String[]{"Nguyễn Anh Tuấn", "1612788", "Đại học KHTN, Tp. Hồ Chí Minh"});
        infor.add(new String[]{"Phạm Hữu Hoàng Việt", "1612810", "Đại học KHTN, Tp. Hồ Chí Minh"});
    }

    @BindView(R.id.background_toolbar)
    View mBackgroundToobar;

    @OnClick(R.id.back_button)
    void back() {
        getMainActivity().dismiss();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return  inflater.inflate(R.layout.about_setting,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TeamAdapter(infor);
        mRecyclerView.setAdapter(mAdapter);
        applyTheme();
    }
    private void applyTheme() {
        int color = Tool.getSurfaceColor();
        int heavy = Tool.getHeavyColor();
        mBackButton.setColorFilter(color);
        mTitle.setTextColor(color);

        mName.setTextColor(heavy);
        mTeam.setTextColor(color);

    }

    @Override
    public boolean isWhiteTheme() {
        return true;
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        if (mBackButton != null) {
            ((ViewGroup.MarginLayoutParams) mBackButton.getLayoutParams()).topMargin = value;
            mBackButton.requestLayout();
        }
    }

    public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {
        private ArrayList<String[]> mDataset;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;
            public TextView mStudentId;
            public TextView mSchool;
            public MyViewHolder(ConstraintLayout v) {
                super(v);
                mName = (TextView)v.findViewById(R.id.text_name);
                mStudentId = (TextView)v.findViewById(R.id.text_student_id);
                mSchool = (TextView)v.findViewById(R.id.text_school);
            }
        }

        public TeamAdapter(ArrayList<String[]> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public TeamAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
            // create a new view
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_team, parent, false);

            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mName.setText(mDataset.get(position)[0]);
            holder.mName.setTextColor(Tool.getHeavyColor());
            holder.mStudentId.setText(mDataset.get(position)[1]);
            holder.mSchool.setText(mDataset.get(position)[2]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}


