package com.teamll.expectlauncher.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.others.AppModel;
import com.teamll.expectlauncher.helper.ItemTouchHelperAdapter;
import com.teamll.expectlauncher.helper.ItemTouchHelperViewHolder;
import com.teamll.expectlauncher.helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class AppListAdapterForRecyclerView extends RecyclerView.Adapter<AppListAdapterForRecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private Context mContext;
    private List<AppModel> mData = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;

    private ItemClickListener mClickListener;


    public AppListAdapterForRecyclerView(Context context, List<AppModel> data, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
        if(data!=null)
        this.mData = data;
    }
    public void setData(ArrayList<AppModel> data) {
        mData.clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void addAll(Collection<? extends AppModel> items) {
        //If the platform supports it, use addAll, otherwise add in loop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
           mData.addAll(items);
        }else{
            for(AppModel item: items){
               mData.addAll(items);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_icon_text, parent, false);
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
        void bind(AppModel appModel) {
            text.setText(appModel.getLabel());
            icon.setImageDrawable(appModel.getIcon());
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, AppModel appModel);
    }
}