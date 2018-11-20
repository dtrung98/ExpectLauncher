package com.teamll.expectlauncher.ui.main.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.ui.main.LayoutSwitcher;
import com.teamll.expectlauncher.utils.Tool;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class RoundedBottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    public interface BottomSheetListener {
        boolean onClickButtonInsideBottomSheet(int id);
    }
    BottomSheetListener listener;
    LayoutSwitcher.MODE mode;
    public void setListener(BottomSheetListener listener) {
        this.listener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    public static RoundedBottomSheetDialogFragment newInstance(LayoutSwitcher.MODE mode) {
        RoundedBottomSheetDialogFragment fragment = new RoundedBottomSheetDialogFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bsd =  new BottomSheetDialog(requireContext(),getTheme());

        return bsd;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        int[] ids;
        if(mode==LayoutSwitcher.MODE.IN_MAIN_SCREEN) {
           view = inflater.inflate(R.layout.mainscreen_bottom_sheet_layout, container,
                    false);

           ids = new int[] {R.id.add_widget,R.id.choose_wallpaper,R.id.editor};
        } else {
           view = inflater.inflate(R.layout.app_drawer_bottom_sheet_layout, container,
                    false);
           ids = new int[]{};
        }
        // get the views and attach the listener
        view.findViewById(R.id.toggleButton).setOnClickListener(this);
        for (int i :
                ids) {
            view.findViewById(i).setOnClickListener(this);
        }
        return view;
    }


    @Override
    public void onClick(final View view) {
        if(view.getId()==R.id.toggleButton) {
            this.dismiss();
            return;
        }
        if(listener!=null) {

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listener.onClickButtonInsideBottomSheet(view.getId());
                }
            },100);
        }
        this.dismiss();
    }
    /*
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
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
                behavior.setHideable(false);
                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if(newState==STATE_COLLAPSED)
                            RoundedBottomSheetDialogFragment.this.dismiss();
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
    }
}