package com.teamll.expectlauncher.ui.main.bottomsheet;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.teamll.expectlauncher.ExpectLauncher;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.OnRangeChangedListener;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class EditAppConfigBottomSheet extends BottomSheetDialogFragment implements OnRangeChangedListener, ColorPickerAdapter.OnColorChangedListener, Palette.PaletteAsyncListener {
    private static final String TAG ="EditAppConfig";

    @Override
    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
       setPadding(leftValue);
    }

    @Override
    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }

    @Override
    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }
    float[] seekValue = new float[]{0,15,20};
    float[] paddingValue = new float[]{-6/31f,2/31f,1/2f};
    // use for seek bar
    private void setPadding(float value) {
        //   0  ->  5  ->  20
        //   0     2/31    2/3
        float[] from = seekValue;
        float[] to = paddingValue;
        float savedValue;
        if(value==from[1]) savedValue = to[1];
        else if(value>from[1]) savedValue = to[1] + (to[2] - to[1]) * (value - from[1])/(from[2]-from[1]);
        else savedValue = to[1] - (to[1] - to[0]) *(from[1] - value) /(from[1]-from[0]);
        mApp.getAppSavedInstance().setPadding(savedValue);
        if(mListener!=null) mListener.onUpdateItem(mIndex);
        Log.d(TAG, "setPadding: savedValue = "+savedValue+", from "+ value);
    }
    private void setSeekBarValue(float value) {
        float[] from = paddingValue;
        float[] to = seekValue;
        float savedValue;
        if(value==from[1]) savedValue = to[1];
        else if(value>from[1]) savedValue = to[1] + (to[2] - to[1]) * (value - from[1])/(from[2]-from[1]);
        else savedValue = to[1] - (to[1] - to[0]) *(from[1] - value) /(from[1]-from[0]);
        mRangeSeekBar.setValue(savedValue);
        Log.d(TAG, "setSeekBar: savedValue = "+savedValue+", from "+value);
    }
    private void bindAppIconType(App app) {
        float appSize = mIcon.getLayoutParams().width;
        PreferencesUtility.IconEditorConfig iec = ExpectLauncher.getInstance().getPreferencesUtility().getIconConfig();
        switch (iec.getShapedType()) {
            case 0:
                mIcon.setBackgroundColor(0);
                mIcon.setCornerRadius(0);
                mIcon.setPadding(0,0,0,0);
                break;
            case 1:
                mIcon.setBackgroundColor(Color.WHITE);
                //    Log.d(TAG, "bindAppIconType: "+iec.getCornerRadius());
                mIcon.setCornerRadius(iec.getCornerRadius()*appSize);
                int pd = (int) (app.getAppSavedInstance().getPadding()*appSize);
                mIcon.setPadding(pd,pd,pd,pd);
                break;
            case 2:
                mIcon.setBackgroundColor(app.getAppSavedInstance().getCustomBackground());
                mIcon.setCornerRadius(iec.getCornerRadius()*appSize);
                int pd2 = (int) (app.getAppSavedInstance().getPadding()*appSize);
                mIcon.setPadding(pd2,pd2,pd2,pd2);
                break;
        }

    }

    @Override
    public void onColorChanged(int position, int newColor) {
        Log.d(TAG, "onColorChanged: "+position);
        mApp.getAppSavedInstance().setCustomBackground(newColor);
        if(mListener!=null) mListener.onUpdateItem(mIndex);
        mIcon.setBackgroundColor(newColor);
     //   mRecyclerView.smoothScrollToPosition(position);
        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView,null,position);
        applyTheme();
    }

    @Override
    public void onGenerated(@Nullable Palette palette) {

        if(palette==null) return;

       List<Palette.Swatch> swatches = palette.getSwatches();
       int most = 0;
       int color = -1;
        for (Palette.Swatch s:
                swatches) {
            if(mColorPickerAdapter!=null&&s!=null) {
                Log.d(TAG, "onGenerated: color "+s.getRgb()+", population = "+s.getPopulation());
                int cur = s.getPopulation();
                if(cur>most) {
                    color = s.getRgb();
                    most = cur;
                }
                mColorPickerAdapter.addData(s.getRgb());
            }
        }
//
      if(mColorPickerAdapter!=null) mColorPickerAdapter.addData(Color.WHITE);
//        if(mColorPickerAdapter!=null) {
//            mColorPickerAdapter.setSelectedColor(color);
//        }
    }

    public interface UpdateItemListener {
        void onUpdateItem(int item);
        void onShowOrHideItem(int item);
    }

    public static EditAppConfigBottomSheet newInstance(UpdateItemListener listener,App app,int index) {
        EditAppConfigBottomSheet a = new EditAppConfigBottomSheet();
        a.mListener = listener;
        a.mApp = app;
        a.mIndex = index;
        return a;
    }
    private UpdateItemListener mListener;
    private App mApp;
    private int mIndex;

    @BindView(R.id.icon) RoundedImageView mIcon;
    @BindView(R.id.title) EditText mEditText;
    @BindView(R.id.edit_button) ImageView mEditIcon;
    @BindView(R.id.app_info_icon) ImageView mInfoIcon;
    @BindView(R.id.app_info_panel) View mInfoPanel;
    @BindView(R.id.app_info_text) TextView mInfoText;
    @BindView(R.id.toggleButton) ImageView mToggle;
    @BindView(R.id.corner_seek_bar) RangeSeekBar mRangeSeekBar;
    @BindView(R.id.hide_switch) SwitchCompat mSwitch;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.recycler_view_label) TextView mRecyclerViewLabel;
    @BindView(R.id.recyclerview_panel) View mRecyclerViewPanel;
    ColorPickerAdapter mColorPickerAdapter;

    @OnTextChanged(R.id.title)
    void  onTitleChange(CharSequence s, int start, int before, int count) {
        if(s.toString().isEmpty()) {
            mApp.getAppSavedInstance().setCustomTitle("");
        }
        else if(s.toString().equals(mApp.getLabel())) {
            mApp.getAppSavedInstance().setCustomTitle("");
        } else mApp.getAppSavedInstance().setCustomTitle(s.toString());

        if(mListener!=null) mListener.onUpdateItem(mIndex);
    }

    @OnClick(R.id.app_info_panel)
    void goToAppInfoSetting() {
        if(mApp==null) return;
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + mApp.getApplicationPackageName()));
            startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            //e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);

        }
        dismiss();
    }
    @BindView(R.id.hide_alert_icon) ImageView mHideAlertIcon;
    @BindView(R.id.hide_alert_text) TextView mHideAlertText;

    @OnCheckedChanged(R.id.hide_switch)
    void HideOrNot(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            mHideAlertIcon.setVisibility(View.VISIBLE);
            mHideAlertText.setVisibility(View.VISIBLE);
            mSwitch.setTextColor(color);
            mApp.getAppSavedInstance().setHidden(true);
            if(mListener!=null) mListener.onShowOrHideItem(mIndex);

        } else {
            mHideAlertText.setVisibility(View.GONE);
            mHideAlertIcon.setVisibility(View.GONE);
            mSwitch.setTextColor(0xFF888888);
            mApp.getAppSavedInstance().setHidden(false);

            if(mListener!=null) mListener.onShowOrHideItem(mIndex);
        }
    }

    @OnClick(R.id.unistall_panel)
    void uninstall() {
        if(mApp==null) return;
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        String pkn = mApp.getApplicationPackageName();
        intent.setData(Uri.parse(String.format("package:%s", pkn)));
        getActivity().startActivity(intent);
        dismiss();
    }
//    @OnClick(R.id.edit_button)
//    void editOrSaveTitle() {
//        if(mEditText.isFocused()) {
//            mInfoPanel.requestFocus();
//            mEditText.clearFocus();
//            mEditIcon.setImageResource(R.drawable.ic_save_black_24dp);
//        }else {
//            mEditIcon.requestLayout();
//            mEditIcon.setImageResource(R.drawable.ic_edit_black_24dp);
//        }
//    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bsd = new BottomSheetDialog(requireContext(), getTheme());

        return bsd;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_app_config_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet =  dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
                behavior.setHideable(false);
                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == STATE_COLLAPSED)
                            EditAppConfigBottomSheet.this.dismiss();
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
        onViewCreated(view);
    }
    private void onViewCreated(View view) {
        ButterKnife.bind(this, view);

        applyTheme();

        if(mApp!=null) {
            mIcon.setImageDrawable(mApp.getIcon());
            String customTitle = mApp.getAppSavedInstance().getCustomTitle();
            mEditText.setHint(mApp.getLabel());
            if (customTitle.isEmpty())
                mEditText.setText(mApp.getLabel());
            else mEditText.setText(customTitle);
        }
        mEditText.setSelection(mEditText.getText().length());
        bindAppIconType(mApp);
        setSeekBarValue((float) mApp.getAppSavedInstance().getPadding());
        mRangeSeekBar.setOnRangeChangedListener(this);

        mColorPickerAdapter = new ColorPickerAdapter(this);
        mRecyclerView.setAdapter(mColorPickerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        mColorPickerAdapter.addData(mApp.getAppSavedInstance().getBackground1(),mApp.getAppSavedInstance().getBackground2());
        mColorPickerAdapter.setSelectedColor(mApp.getAppSavedInstance().getCustomBackground());
        if(mApp.getIcon() instanceof BitmapDrawable)
        Palette.from(((BitmapDrawable) mApp.getIcon()).getBitmap()).generate(this);
        else {
            Palette.from(drawableToBitmap(mApp.getIcon())).generate(this);
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    int color;
    private void applyTheme() {
        if(mApp==null)
        this.color = Tool.getSurfaceColor();
        else this.color = mApp.getAppSavedInstance().getBackground1();
        mToggle.setColorFilter(color);
        mInfoIcon.setColorFilter(color);
        int alpha_color = Color.argb(0x22,Color.red(color),Color.green(color),Color.blue(color));
        mEditIcon.setColorFilter(color);
        mEditIcon.setAlpha(0.6f);
      //  mInfoPanel.setBackgroundColor(alpha_color);
        mInfoPanel.getBackground().setColorFilter(alpha_color, PorterDuff.Mode.SRC_OVER);
        mInfoText.setTextColor(color);
        mHideAlertText.setTextColor(color);
        mHideAlertIcon.setColorFilter(color);
        mRangeSeekBar.setProgressColor(color);
        mEditText.setTextColor(color);
        mEditText.setHintTextColor(alpha_color);
      // mSwitch.setThumbTintList(ColorStateList.valueOf(color));


        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                0xFF888888,
                color,
        };

        int[] trackColors = new int[] {
               0x22000000,
                alpha_color,
        };

      //  checkBox.setSupportButtonTintList(new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(mSwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(mSwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
    }
}
