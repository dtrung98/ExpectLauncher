package com.teamll.expectlauncher.ui.main.appdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.MenuPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.teamll.expectlauncher.ExpectLauncher;
import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.teamll.expectlauncher.model.AppInstance;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperAdapter;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperViewHolder;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.util.Animation;
import com.teamll.expectlauncher.util.PreferencesUtility;
import com.teamll.expectlauncher.util.Tool;
import com.teamll.expectlauncher.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppDrawerAdapter extends RecyclerView.Adapter<AppDrawerAdapter.ViewHolder> implements ItemTouchHelperAdapter, Filterable {
    private static final String TAG="AppDrawerAdapter";

    private ArrayList<App> mData = new ArrayList<>();
    private ArrayList<App> mFilterData = new ArrayList<>();

    boolean isInSearchMode() {
        return mInSearchMode;
    }

    void setInSearchMode(boolean mInSearchMode) {
        this.mInSearchMode = mInSearchMode;
    }

    private boolean mInSearchMode = false;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;
    Random random = new Random();

    private ItemClickListener mClickListener;
    float mFontValue;
    float mIntFontValue;
    public void initFontValue() {
        mIntFontValue = ExpectLauncher.getInstance().getPreferencesUtility().getFontTitleSize(mContext);
        float one_sp = mContext.getResources().getDimensionPixelSize(R.dimen.one_sp);
        mFontValue = one_sp*mIntFontValue;
    }

    public void setFontValue(int oldVal, int newVal) {
        ExpectLauncher.getInstance().getPreferencesUtility().setFontTitleSize(newVal);
        float one_sp = mContext.getResources().getDimensionPixelSize(R.dimen.one_sp);
        mIntFontValue = newVal;
        mFontValue = one_sp*mIntFontValue;
        notifyDataSetChanged();
    }


    public enum APP_DRAWER_CONFIG_MODE {
        NORMAL,
        MOVABLE_APP_ICON,
        APP_ICON_EDITOR
    }

    private APP_DRAWER_CONFIG_MODE mConfigMode = APP_DRAWER_CONFIG_MODE.NORMAL;
    private boolean mOldConfigModeIsMovable = false;
    public void switchMode(APP_DRAWER_CONFIG_MODE newMode) {

        if(mConfigMode==APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON&&newMode!=APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON) {
            mOldConfigModeIsMovable = true;
        }
        else mOldConfigModeIsMovable = false;

        mConfigMode = newMode;
        notifyDataSetChanged();
    }
    public APP_DRAWER_CONFIG_MODE getMode() {
        return mConfigMode;
    }

    public AppDrawerAdapter(Context mContext, OnStartDragListener dragStartListener) {
        this.mContext = mContext;
        mDragStartListener = dragStartListener;
        initFontValue();
    }
    public void setData(List<App> data) {
      //  Log.d(TAG, "setData");
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
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_drawer_v2, parent, false);
        return new ViewHolder(view);
    }
    long savedCount = 0;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        long now = System.currentTimeMillis();
        Log.d(TAG, "onBindViewHolder: "+mData.get(position).getLabel()+", "+position+", deltaPrev = "+ (now - savedCount));
        savedCount = System.currentTimeMillis();
      //  Log.d(TAG, "onBindViewHolder: inSearchMode ="+mInSearchMode);
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
    //    Log.d(TAG, "onItemMove: from "+fromPosition +" to "+ toPosition);

      //  Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        mData.add(toPosition, mData.remove(fromPosition));
        mData.get(toPosition).getAppSavedInstance().setIndex(toPosition);
        mData.get(fromPosition).getAppSavedInstance().setIndex(fromPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, ItemTouchHelperViewHolder, View.OnAttachStateChangeListener {
        @BindView(R.id.icon) RoundedImageView mIcon;
        @BindView(R.id.text) TextView mTitle;
        @BindView(R.id.x_button) ImageView mXButton;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        @Override
        public boolean onLongClick(View view) {

            if(mConfigMode==APP_DRAWER_CONFIG_MODE.NORMAL)
            if (mClickListener != null) {
                int pos = getAdapterPosition();
                mClickListener.onItemLongPressed(view,mData.get(pos),pos);
            }
            return true;
        }
        @OnClick(R.id.x_button)
        void onClickXButton(View v) {
            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            String pkn;
            if(isInSearchMode()) pkn = mFilterData.get(getAdapterPosition()).getApplicationPackageName();
            else pkn = mData.get(getAdapterPosition()).getApplicationPackageName();
            intent.setData(Uri.parse(String.format("package:%s", pkn)));

            if(mContext!=null) {
               mContext.startActivity(intent);
           }
        }
        @Override
        public void onClick(View view) {
            if(mConfigMode==APP_DRAWER_CONFIG_MODE.NORMAL)
            if (mClickListener != null) {
                int pos = getAdapterPosition();
                if(isInSearchMode())
                    mClickListener.onItemClick(view,mFilterData.get(pos));
                else
                mClickListener.onItemClick(view,mData.get(pos));
            }
        }

        void bind(App app) {

            String custom = app.getAppSavedInstance().getCustomTitle();
            if(custom.isEmpty())
             mTitle.setText(app.getLabel());
           else mTitle.setText(custom);

           mIcon.setImageDrawable(app.getIcon());


            bindMovableIcon();
            bindAppSizeAndType(app);
            bindAppTitleTextView();

//            RequestOptions requestOptions = new RequestOptions().override(68,68);
//
//            Glide.with(mContext)
//                    .load(app.getIcon())
//                    .apply(requestOptions)
//                    // .transition(DrawableTransitionOptions.withCrossFade(650))
//                    .into(mIcon);

        }
        @SuppressLint("ClickableViewAccessibility")
        void setResponseOnTouch() {
            GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    onLongClick(itemView);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    itemView.performClick();
                    onClick(itemView);
                    return true;
                }

            });
            mIcon.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gd.onTouchEvent(event);
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            //overlay is black with transparency of 0x77 (119)
                            if(view.getDrawable() !=null)
                            view.getDrawable().setColorFilter(0x22000000,PorterDuff.Mode.SRC_ATOP);
                            view.getBackground().setColorFilter(0x22000000,PorterDuff.Mode.SRC_ATOP);
                            view.setAlpha(0.85f);
                            view.invalidate();
                            return true;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            //clear the overlay
                            if(view.getDrawable() !=null)
                            view.getDrawable().clearColorFilter();
                            view.getBackground().clearColorFilter();
                            view.setAlpha(1f);
                            view.invalidate();
                            break;
                        }
                    }

                    return false;
                }
            });
        }

        private void bindDeleteIcon(float appSize){

            if(mConfigMode!=APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON) {
                if(mXButton.getVisibility()==View.VISIBLE&&mOldConfigModeIsMovable&&!mData.get(getAdapterPosition()).getApplicationPackageName().equals(mContext.getPackageName())) {
                //    Log.d(TAG, "bindDeleteIcon: "+getAdapterPosition());
                    ScaleAnimation sa = new ScaleAnimation(1f,0f,1f,0f,ScaleAnimation.RELATIVE_TO_SELF,0.5f, ScaleAnimation.RELATIVE_TO_SELF,0.5f);
                    sa.setDuration(250);
                    // sa.setInterpolator(new OvershootInterpolator());
                    sa.setFillAfter(true);
                    sa.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(android.view.animation.Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(android.view.animation.Animation animation) {
                            mXButton.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(android.view.animation.Animation animation) {

                        }
                    });

                    mXButton.startAnimation(sa);
                } else mXButton.setVisibility(View.GONE);
                return;
            }
            if(mData.get(getAdapterPosition()).getApplicationPackageName().equals(mContext.getPackageName())) return;

            ViewGroup.LayoutParams params = mXButton.getLayoutParams();
            float h = appSize*20/62f;
            int padding = (int) (h*3/20f);
            params.width = params.height = (int) h;
            if(params instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) params;
                params1.topMargin = params1.leftMargin = padding;
            }
            mXButton.requestLayout();
            mXButton.setPadding(padding,padding,padding,padding);

            ScaleAnimation sa = new ScaleAnimation(0f,1f,0f,1f,ScaleAnimation.RELATIVE_TO_SELF,0.5f, ScaleAnimation.RELATIVE_TO_SELF,0.5f);
            sa.setDuration(250);
           // sa.setInterpolator(new OvershootInterpolator());
            sa.setFillAfter(true);

            mXButton.setVisibility(View.VISIBLE);
            mXButton.startAnimation(sa);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void bindMovableIcon() {

            if(mConfigMode!=APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON) {
               setResponseOnTouch();
               itemView.removeOnAttachStateChangeListener(this);
                itemView.clearAnimation();
                return;
            }
                mIcon.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(ViewHolder.this);
                        }
                        return false;
                    }
                });

            hang(itemView);
            itemView.addOnAttachStateChangeListener(this);

        }
        private void hang(View itemView) {
            RotateAnimation rotateAnimation = new RotateAnimation(-3,3,RotateAnimation.RELATIVE_TO_SELF,0.4f,RotateAnimation.RELATIVE_TO_SELF,0.45f);
            rotateAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
            rotateAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
            rotateAnimation.setDuration(120);
            int next = 3 - random.nextInt(6);

            rotateAnimation.setStartOffset(next);
            rotateAnimation.setInterpolator(Animation.getInterpolator(9));
            itemView.clearAnimation();
            itemView.startAnimation(rotateAnimation);
        }
        private void bindAppIconType(float appSize, App app) {
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
        private void bindAppSizeAndType(App app) {

            Resources resources = mContext.getResources();
            float w = resources.getDimension(R.dimen.app_width);
            float h = resources.getDimension(R.dimen.app_height);
            int marginText = (int) resources.getDimension(R.dimen.text_app_margin);

            float scale = ExpectLauncher.getInstance().getPreferencesUtility().getAppIconSize();

            int nw = (int) (w*scale);
            int nh = (int) (w*scale);

            ViewGroup.LayoutParams iconParams = mIcon.getLayoutParams();
           iconParams.width  = nw;
           iconParams.height = nh;

           ViewGroup.LayoutParams textParams = mTitle.getLayoutParams();
           textParams.width = nw-marginText;

            ViewGroup.LayoutParams rootParams = itemView.getLayoutParams();
            rootParams.width = nw;
            rootParams.height = nh + textParams.height;
            bindAppIconType(nw, app);
            bindDeleteIcon(nw);

          //  itemView.requestLayout();
          //  mIcon.requestLayout();
          //  mTitle.requestLayout();
        }
        private void bindAppTitleTextView() {
            if(ExpectLauncher.getInstance().getPreferencesUtility().isShowAppTitle()) {
                mTitle.setVisibility(View.VISIBLE);

                PreferencesUtility.IconEditorConfig iec = ExpectLauncher.getInstance().getPreferencesUtility().getIconConfig();
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
              mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,mIntFontValue);
            }
            else mTitle.setVisibility(View.GONE);
        }

        @Override
        public void onItemSelected() {
            itemView.clearAnimation();
            AnimationSet as = new AnimationSet(false);
            ScaleAnimation sa = new ScaleAnimation(1f,1.2f,1f,1.2f,50,50);
            AlphaAnimation aa = new AlphaAnimation(1f,0.85f);
            as.addAnimation(sa);
            as.addAnimation(aa);
            as.setDuration(85);
            as.setFillAfter(true);

            itemView.setAnimation(as);
           // itemView.setScaleX(1.1f);
          //  itemView.setScaleY(1.1f);
        }

        @Override
        public void onItemClear() {
//            itemView.setScaleX(1);
//            itemView.setScaleY(1);

            itemView.clearAnimation();
            AnimationSet as = new AnimationSet(false);
            ScaleAnimation sa = new ScaleAnimation(1.2f,1f,1.2f,1f,50,50);
            AlphaAnimation aa = new AlphaAnimation(0.85f,1f);
            as.addAnimation(sa);
            as.addAnimation(aa);
            as.setDuration(85);
            as.setFillAfter(true);


            RotateAnimation rotateAnimation = new RotateAnimation(-3,3,40f,50f);
            rotateAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
            rotateAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
            rotateAnimation.setDuration(85);
            int next = 3 - random.nextInt(6);

            rotateAnimation.setStartOffset(next);
            rotateAnimation.setInterpolator(Animation.getInterpolator(9));
           // itemView.clearAnimation();

         //   itemView.startAnimation(rotateAnimation);
            as.addAnimation(rotateAnimation);
            itemView.setAnimation(as);

        }

        @Override
        public void onViewAttachedToWindow(View v) {
            if(mConfigMode==APP_DRAWER_CONFIG_MODE.MOVABLE_APP_ICON) {
                itemView.clearAnimation();
                hang(itemView);
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {

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
                mFilterData= (ArrayList<App>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemClickListener {
        void onItemClick(View view, App app);
        void onItemLongPressed(View view, App app, int index);
    }

    public void clear() {
        mData.clear();
    }

    public void backupApps() {

        Log.d(TAG, "backupApps");
        ExpectLauncher.getInstance().getPreferencesUtility().saveAppInstance(mData);
    }

    public void restoreApps() {

        Log.d(TAG, "restoreApps");
        Collections.sort(mData,(o1, o2) -> o1.getAppSavedInstance().getIndex()-o2.getAppSavedInstance().getIndex());
        notifyDataSetChanged();
    }
}