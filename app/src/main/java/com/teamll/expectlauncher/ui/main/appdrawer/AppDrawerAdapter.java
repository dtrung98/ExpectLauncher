package com.teamll.expectlauncher.ui.main.appdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperAdapter;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperViewHolder;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.utils.Animation;
import com.teamll.expectlauncher.utils.PreferencesUtility;
import com.teamll.expectlauncher.utils.Tool;

public class AppDrawerAdapter extends RecyclerView.Adapter<AppDrawerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG="AppDrawerAdapter";

    private ArrayList<AppDetail> mData = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private Context mContext;
    Random random = new Random();

    private ItemClickListener mClickListener;


    public enum APP_DRAWER_CONFIG_MODE {
        NORMAL,
        MOVABLE_APP_ICON,
        APP_ICON_EDITOR
    }

    public APP_DRAWER_CONFIG_MODE mConfigMode = APP_DRAWER_CONFIG_MODE.NORMAL;
    public void switchMode(APP_DRAWER_CONFIG_MODE newMode) {
        mConfigMode = newMode;
        notifyDataSetChanged();
    }



    public AppDrawerAdapter(Context mContext, OnStartDragListener dragStartListener) {
        this.mContext = mContext;
        mDragStartListener = dragStartListener;
    }

    public void setData(List<App> data) {
        mData.clear();
        if (data!=null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }
    public void addData(List<App> data) {
        if(data!=null) {
            int posBefore = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(posBefore,data.size());
        }
    }
    /**phhViet - filter này dùng trong searchView - khi text trong searchView thay đổi thì hiện
    app tương ứng*/
    public void Filter(String name,List<App>data) {
        ArrayList<AppDetail> tmp = new ArrayList<>();
        name = name.toLowerCase(Locale.getDefault());
        tmp.clear();
        if (name.length() == 0) {
            tmp.addAll(data);

        }
        else {
            for(AppDetail i: data){
                if(i.getLabel().toLowerCase(Locale.getDefault()).contains(name)){
                    tmp.add(i);
                }
            }

        }
        mData.addAll(tmp);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.icon.clearAnimation();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return (mData==null) ? 0 : mData.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMove: from "+fromPosition +" to "+ toPosition);

      //  Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        mData.add(toPosition, mData.remove(fromPosition));
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, ItemTouchHelperViewHolder {
       ImageView icon;
       TextView text;
       View root;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
           icon = itemView.findViewById(R.id.icon);
           text = itemView.findViewById(R.id.text);
           itemView.setOnClickListener(this);
           itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if(mConfigMode==APP_DRAWER_CONFIG_MODE.NORMAL)
            if (mClickListener != null) {
                int pos = getAdapterPosition();
                mClickListener.onItemLongPressed(view,mData.get(pos));
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            if(mConfigMode==APP_DRAWER_CONFIG_MODE.NORMAL)
            if (mClickListener != null) {
                int pos = getAdapterPosition();
                mClickListener.onItemClick(view,mData.get(pos));
            }
        }
        void bind(AppDetail appDetail) {
            text.setText(appDetail.getLabel());
            icon.setImageDrawable(appDetail.getIcon());

            bindMovableIcon();
            bindAppSize();
            bindAppTitleTextView();
        }

        @SuppressLint("ClickableViewAccessibility")
        private void bindMovableIcon() {
            if(mConfigMode!=APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON) return;
                icon.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(ViewHolder.this);
                        }
                        return false;
                    }
                });

                RotateAnimation rotateAnimation = new RotateAnimation(-5,5,40f,50f);
                rotateAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
                rotateAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
                rotateAnimation.setDuration(85);
                int next = 3 - random.nextInt(6);

                rotateAnimation.setStartOffset(next);
                rotateAnimation.setInterpolator(Animation.getInterpolator(9));
                icon.clearAnimation();
                icon.startAnimation(rotateAnimation);
        }
        private void bindAppSize() {

            Resources resources = mContext.getResources();
            float w = resources.getDimension(R.dimen.app_width);
            float h = resources.getDimension(R.dimen.app_height);
            int marginText = (int) resources.getDimension(R.dimen.text_app_margin);

            float scale = PreferencesUtility.getInstance(mContext.getApplicationContext()).getAppIconSize();

            int nw = (int) (w*scale);
            int nh = (int) (w*scale);

            ViewGroup.LayoutParams iconParams = icon.getLayoutParams();
           iconParams.width  = nw;
           iconParams.height = nh;

           ViewGroup.LayoutParams textParams = text.getLayoutParams();
           textParams.width = nw-marginText;

            ViewGroup.LayoutParams rootParams = root.getLayoutParams();
            rootParams.width = nw;
            rootParams.height = nh + textParams.height;

            root.requestLayout();
            icon.requestLayout();
            text.requestLayout();
        }
        private void bindAppTitleTextView() {
            if(PreferencesUtility.getInstance(mContext.getApplicationContext()).isShowAppTitle()) {
                text.setVisibility(View.VISIBLE);
                if(Tool.WHITE_TEXT_THEME) text.setTextColor(0xFFEEEEEE);
                else text.setTextColor(0xFF333333);

            }
            else text.setVisibility(View.GONE);
        }

        @Override
        public void onItemSelected() {
            itemView.setScaleX(1.1f);
            itemView.setScaleY(1.1f);
        }

        @Override
        public void onItemClear() {
            itemView.setScaleX(1);
            itemView.setScaleY(1);
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, AppDetail appDetail);
        void onItemLongPressed(View view, AppDetail appDetail);
    }
}