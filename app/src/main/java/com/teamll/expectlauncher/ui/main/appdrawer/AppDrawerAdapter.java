package com.teamll.expectlauncher.ui.main.appdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.makeramen.roundedimageview.RoundedImageView;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperAdapter;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperViewHolder;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.util.Animation;
import com.teamll.expectlauncher.util.BitmapEditor;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;
import com.teamll.expectlauncher.util.Util;

public class AppDrawerAdapter extends RecyclerView.Adapter<AppDrawerAdapter.ViewHolder> implements ItemTouchHelperAdapter, Filterable {
    private static final String TAG="AppDrawerAdapter";

    private ArrayList<AppDetail> mData = new ArrayList<>();
    private ArrayList<AppDetail> mFilterData = new ArrayList<>();

    public boolean isInSearchMode() {
        return mInSearchMode;
    }

    public void setInSearchMode(boolean mInSearchMode) {
        this.mInSearchMode = mInSearchMode;
    }

    private boolean mInSearchMode = false;
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

    AppDrawerAdapter(Context mContext, OnStartDragListener dragStartListener) {
        this.mContext = mContext;
        mDragStartListener = dragStartListener;
    }
    public void setData(List<App> data) {
        Log.d(TAG, "setData");
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(!mInSearchMode) {
            holder.bind(mData.get(position));
        } else {
            holder.bind(mFilterData.get(position));
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.mIcon.clearAnimation();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return (mInSearchMode) ? mFilterData.size() : mData.size();
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
       RoundedImageView mIcon;
       TextView mTitle;
       View mRoot;

        ViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView;
           mIcon = itemView.findViewById(R.id.icon);
           mTitle = itemView.findViewById(R.id.text);
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
            Log.d(TAG, "bind");
            mTitle.setText(appDetail.getLabel());
            mIcon.setImageDrawable(appDetail.getIcon());

            bindMovableIcon();
            bindAppSizeAndType(appDetail);
            bindAppTitleTextView();
        }

        @SuppressLint("ClickableViewAccessibility")
        private void bindMovableIcon() {
            if(mConfigMode!=APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON) return;
                mIcon.setOnTouchListener(new View.OnTouchListener() {
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
                mIcon.clearAnimation();
                mIcon.startAnimation(rotateAnimation);
        }
        private void bindAppIconType(float appSize, AppDetail app) {
            PreferencesUtility.IconEditorConfig iec = PreferencesUtility.getInstance(mContext).getIconConfig();
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
                    int pd = (int) (2f/31*appSize);
                    mIcon.setPadding(pd,pd,pd,pd);
                    break;
                case 2:
                   mIcon.setBackgroundColor(app.getDarkenAverageColor());
                    mIcon.setCornerRadius(iec.getCornerRadius()*appSize);
                    int pd2 = (int) (2f/31*appSize);
                    mIcon.setPadding(pd2,pd2,pd2,pd2);
                    break;
            }
        }
        private void bindAppSizeAndType(AppDetail app) {

            Resources resources = mContext.getResources();
            float w = resources.getDimension(R.dimen.app_width);
            float h = resources.getDimension(R.dimen.app_height);
            int marginText = (int) resources.getDimension(R.dimen.text_app_margin);

            float scale = PreferencesUtility.getInstance(mContext.getApplicationContext()).getAppIconSize();

            int nw = (int) (w*scale);
            int nh = (int) (w*scale);

            ViewGroup.LayoutParams iconParams = mIcon.getLayoutParams();
           iconParams.width  = nw;
           iconParams.height = nh;

           ViewGroup.LayoutParams textParams = mTitle.getLayoutParams();
           textParams.width = nw-marginText;

            ViewGroup.LayoutParams rootParams = mRoot.getLayoutParams();
            rootParams.width = nw;
            rootParams.height = nh + textParams.height;
            bindAppIconType(nw, app);
            mRoot.requestLayout();
            mIcon.requestLayout();
            mTitle.requestLayout();
        }
        private void bindAppTitleTextView() {
            if(PreferencesUtility.getInstance(mContext.getApplicationContext()).isShowAppTitle()) {
                mTitle.setVisibility(View.VISIBLE);

                PreferencesUtility.IconEditorConfig iec = PreferencesUtility.getInstance(mContext).getIconConfig();
              if(Tool.WHITE_TEXT_THEME)  switch (iec.getTitleColorType()) {
                    case 0:
                        boolean isDarkWallpaper = Tool.getInstance().isDarkWallpaper();
                        if(isDarkWallpaper) mTitle.setTextColor(0xFFEEEEEE);
                        else mTitle.setTextColor(0xFF111111);
                        //mTitle.setTextColor(Tool.getContrastVersionForColor(Tool.getInstance().getAverageColor()));
                            break;
                    case 1: mTitle.setTextColor(0xFFEEEEEE); break;
                    case 2: mTitle.setTextColor(0xFF111111); break;

                } else mTitle.setTextColor(0xFF333333);
            }
            else mTitle.setVisibility(View.GONE);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilterData.clear();
                } else {
                    mFilterData = Util.searchAppsFilter(mData, charString);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterData= (ArrayList<AppDetail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public interface ItemClickListener {
        void onItemClick(View view, AppDetail appDetail);
        void onItemLongPressed(View view, AppDetail appDetail);
    }
}