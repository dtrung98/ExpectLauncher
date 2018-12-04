package com.teamll.expectlauncher.ui.main.bottomsheet;

import android.app.Dialog;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.makeramen.roundedimageview.RoundedImageView;
import com.teamll.expectlauncher.R;

import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.OnRangeChangedListener;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class IconAppEditorBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {


    public interface AppEditorCallBack {
        AppDetail getAdaptiveApp();
        void onUpdate();
    }
    AppEditorCallBack listener;


    @BindView(R.id.type_none) RoundedImageView mTypeNoneButton;
    @BindView(R.id.type_white_square) RoundedImageView mTypeWhiteSquareButton;
    @BindView(R.id.type_color_square) RoundedImageView mTypeColorSquareButton;

    @BindView(R.id.corner_seek_bar) RangeSeekBar mCornerSeekBar;

    @BindView(R.id.auto_text) TextView mAutoTextView;
    @BindView(R.id.white) TextView mWhiteTextView;
    @BindView(R.id.black) TextView mBlackTextView;

    @BindView(R.id.show_if_normal_type) View mShowIfNormal;

    public static IconAppEditorBottomSheet newInstance( AppEditorCallBack listener) {


        IconAppEditorBottomSheet fragment = new IconAppEditorBottomSheet();
        if(listener!=null)
        fragment.listener = listener;
        return fragment;
    }

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
        return inflater.inflate(R.layout.icon_app_editor,container,false);
    }


    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.toggleButton) {
            this.dismiss();
            return;
        }
        if (listener != null) {
            switch (view.getId()) {
                case R.id.type_none: setTypeButton(0,false); break;
                case R.id.type_white_square: setTypeButton(1,false); break;
                case R.id.type_color_square: setTypeButton(2,false); break;
                case R.id.auto_text: setColorType(0); break;
                case R.id.white: setColorType(1); break;
                case R.id.black: setColorType(2); break;

            }
        }

    }
    /**
     private ArrayList<View> rippleViews = new ArrayList<>();
     private boolean first_time = true;
     public void addToBeRipple(int drawable,View... v) {
     if(first_time) {
     first_time = false;
     res = getResources();
     }
     int l = v.length;
     rippleViews.addAll(Arrays.asList(v));
     for(View view :v) {
     view.setBackground( (RippleDrawable) res.getDrawable(drawable));
     view.setClickable(true);
     }
     }
     Resources res;
     public void applyRippleColor(int color) {
     for( final View v : rippleViews)
     {
     ((RippleDrawable)v.getBackground()).setColor(ColorStateList.valueOf(color));
     }
     }

     */
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
                            IconAppEditorBottomSheet.this.dismiss();
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
        onViewCreated(view);
    }
    PreferencesUtility.IconEditorConfig mIconConfig;

    private void onViewCreated(View view) {
        ButterKnife.bind(this,view);
        setClick();
        mIconConfig = PreferencesUtility.getInstance(getContext()).getIconConfig();
        setImageForTypeButton();
        setTypeButton(mIconConfig.getShapedType(),true);
        setCornerSeekBarValue(mIconConfig.getCornerRadius());
        focusThisColorType(mIconConfig.getTitleColorType());
    }
    private void setClick() {
        mTypeNoneButton.setOnClickListener(this);
        mTypeWhiteSquareButton.setOnClickListener(this);
        mTypeColorSquareButton.setOnClickListener(this);
        mAutoTextView.setOnClickListener(this);
        mWhiteTextView.setOnClickListener(this);
        mBlackTextView.setOnClickListener(this);
        mCornerSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if(isFromUser) {
                    setCornerRadius(leftValue);
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
    }
    private void setImageForTypeButton() {
        Drawable bitmap = null;
        if(listener!=null) {
           AppDetail iv = listener.getAdaptiveApp();
           if(iv!=null) {
               bitmap = iv.getIcon();
               mTypeColorSquareButton.setBackgroundColor(iv.getDarkenAverageColor());
           }
        }
        if(bitmap!=null) {
            mTypeNoneButton.setImageDrawable(bitmap);
            mTypeWhiteSquareButton.setImageDrawable(bitmap);
            mTypeColorSquareButton.setImageDrawable(bitmap);

        }

    }
    private void setTypeButton(int value,boolean forced) {

        if(forced||mIconConfig.getShapedType()!=value) {

            switch (mIconConfig.getShapedType()) {
                case 0: mTypeNoneButton.setBorderWidth(R.dimen.dp_0);break;
                case 1: mTypeWhiteSquareButton.setBorderWidth(R.dimen.dp_0); break;
                case 2: mTypeColorSquareButton.setBorderWidth(R.dimen.dp_0); break;
            }
            switch (value) {
                case 0: mTypeNoneButton.setBorderWidth(R.dimen.dp_2); break;
                case 1: mTypeWhiteSquareButton.setBorderWidth(R.dimen.dp_2); break;
                case 2: mTypeColorSquareButton.setBorderWidth(R.dimen.dp_2); break;
            }
            mIconConfig.setShapedType(value).applyAll();
            if(value!=0) {
                mShowIfNormal.setVisibility(View.GONE);
            } else {
                mShowIfNormal.setVisibility(View.VISIBLE);
            }
            update();
        }
    }
    private void update() {
        if(listener!=null) listener.onUpdate();
    }

    // use for controller
    private void setCornerSeekBarValue(float value) {
        //   5  ->  10  ->  20
        // 1/31    6/31    1/2
        if(value<1/31f) value = 1/31f;
       else if(value >1/2f) value = 1/2f;

       if(value==6f/31) mCornerSeekBar.setValue(10);
       else if(value>4f/62) mCornerSeekBar.setValue(10 + 10*(value- 6f/31)/(1/2f - 6f/31));
       else mCornerSeekBar.setValue(10 - 5f*(6f/31- value)/( 6f/31 - 1f/31));
    }

    // use for seek bar
    private void setCornerRadius(float value) {
        //   5  ->  10  ->  20
        // 1/31    6/31    1/2
        float savedValue;
        if(value==10) savedValue = 6/31f;
        else if(value>10) savedValue = 6f/31 + (1/2f - 6/31f) * (value - 10f)/(10f);
        else savedValue = 6f/31 - (6f/31 - 1/31f) *(10 - value) /5f;
        mIconConfig.setCornerRadius(savedValue).applyAll();
       update();

    }
    // use for controller
    private void focusThisColorType(int colorType) {
        // 0 ~ Auto, 1 ~ White, 2 ~ Black
        TextView dest = null;
        switch (colorType) {
            case 0:
                dest = mAutoTextView;
                break;
            case 1:
                dest = mWhiteTextView;
                break;
            case 2:
                dest = mBlackTextView;
                break;
        }
        if(dest!=null) {
           // android:textColor="@color/FlatBlue"
           // android:textStyle="bold"
           // android:background="@drawable/count_round"
            dest.setTextColor(getResources().getColor(R.color.FlatBlue));
            dest.setTypeface(Typeface.DEFAULT_BOLD);
            dest.setBackground(getResources().getDrawable(R.drawable.count_round));

        }
    }
    // use in code
    private void normalAllColorType() {
        // 0 ~ Auto, 1 ~ White, 2 ~ Black
        TextView dest = null;
        switch (mIconConfig.getTitleColorType()) {
            case 0:
                dest = mAutoTextView;
                break;
            case 1:
                dest = mWhiteTextView;
                break;
            case 2:
                dest = mBlackTextView;
                break;
        }
        if(dest!=null) {
            // android:textColor="@color/FlatBlue"
            // android:textStyle="bold"
            // android:background="@drawable/count_round"
            dest.setTextColor(getResources().getColor(R.color.BackwardColorHeavy));
            dest.setTypeface(Typeface.DEFAULT);
            dest.setBackgroundColor(0);
        }
    }
    private void setColorType(int colorType) {
       normalAllColorType();
        mIconConfig.setTitleColorType(colorType).applyAll();
        focusThisColorType(colorType);
       update();
    }
}
