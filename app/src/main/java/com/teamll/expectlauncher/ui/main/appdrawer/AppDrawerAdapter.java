package com.teamll.expectlauncher.ui.main.appdrawer;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperAdapter;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperViewHolder;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;
import com.teamll.expectlauncher.utils.Tool;

public class AppDrawerAdapter extends RecyclerView.Adapter<AppDrawerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<AppDetail> mData = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;

    private ItemClickListener mClickListener;


    AppDrawerAdapter(OnStartDragListener dragStartListener) {
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
    public int getItemCount() {
        return (mData==null) ? 0 : mData.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
       ImageView icon;
       TextView text;
       View root;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
           icon = itemView.findViewById(R.id.icon);
           text = itemView.findViewById(R.id.text);
           itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                int pos = getAdapterPosition();
                mClickListener.onItemClick(view,mData.get(pos));
            }
        }
        void bind(AppDetail appDetail) {
            text.setText(appDetail.getLabel());
            if(Tool.WHITE_TEXT_THEME) text.setTextColor(Color.WHITE);
            else text.setTextColor(0xFF333333);
            icon.setImageDrawable(appDetail.getIcon());
            /*
            icon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(ViewHolder.this);
                    }
                    return false;
                }
            });
            */
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
    }
}