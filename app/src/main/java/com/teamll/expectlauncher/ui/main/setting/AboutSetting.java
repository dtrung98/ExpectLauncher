package com.teamll.expectlauncher.ui.main.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
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
    CarouselView carouselView;
    int NUMBER_OF_PAGES = 5;
    private int[] mIConID = {};

    ArrayList<String[]> infor = new ArrayList<String[]>();
    int[] drawable;

    public AboutSetting() {
        infor.add(new String[]{"Lê Văn Tư", "1612770", "Đại học KHTN, Tp. Hồ Chí Minh"});
        infor.add(new String[]{"Nguyễn Hữu Tứ", "1612772", "Đại học KHTN, Tp. Hồ Chí Minh"});
        infor.add(new String[]{"Lê Đình Trung", "1612751", "Đại học KHTN, Tp. Hồ Chí Minh"});
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
        View v = inflater.inflate(R.layout.about_setting,container,false);

        ViewListener viewListener = new ViewListener() {

            @Override
            public View setViewForPosition(int position) {
                View customView = getLayoutInflater().inflate(R.layout.item_carousel, null);

                TextView text_name = (TextView)customView.findViewById(R.id.text_name);
                text_name.setText(infor.get(position)[0]);

                TextView text_student_id = (TextView)customView.findViewById(R.id.text_student_id);
                text_student_id.setText(infor.get(position)[1]);

                TextView text_school = (TextView)customView.findViewById(R.id.text_school);
                text_school.setText(infor.get(position)[2]);

                return customView;
            }
        };

        carouselView = (CarouselView) v.findViewById(R.id.carouselView);
        carouselView.setPageCount(NUMBER_OF_PAGES);
        carouselView.setViewListener(viewListener);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        int color = Tool.getSurfaceColor();
        mBackgroundToobar.setBackgroundColor(color);
        for (int id :
                mIConID) {
            ((ImageView)view.findViewById(id)).setColorFilter(color);
        }

    }

    @Override
    public boolean isWhiteTheme() {
        return false;
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        if (mBackButton != null) {
            ((ViewGroup.MarginLayoutParams) mBackButton.getLayoutParams()).topMargin = value;
            mBackButton.requestLayout();
        }
    }
}
