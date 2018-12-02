package com.teamll.expectlauncher.ui.main.bottomsheet;

import android.app.Dialog;

import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;


import com.makeramen.roundedimageview.RoundedImageView;
import com.teamll.expectlauncher.R;

import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.ui.widgets.rangeseekbar.RangeSeekBar;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class IconAppEditorBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {


    public interface AppEditorCallBack {
        AppDetail getAdaptiveApp();
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

    }
    private void setClick() {
        mTypeNoneButton.setOnClickListener(this);
        mTypeWhiteSquareButton.setOnClickListener(this);
        mTypeColorSquareButton.setOnClickListener(this);
    }
    private void setImageForTypeButton() {
        Drawable bitmap = null;
        if(listener!=null) {
           AppDetail iv = listener.getAdaptiveApp();
           if(iv!=null) bitmap = iv.getIcon();

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
        }
    }
    private void setCornerRadius(float value) {

    }
}
