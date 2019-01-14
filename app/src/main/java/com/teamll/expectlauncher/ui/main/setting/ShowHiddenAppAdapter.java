package com.teamll.expectlauncher.ui.main.setting;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowHiddenAppAdapter extends RecyclerView.Adapter<ShowHiddenAppAdapter.ViewHolder>  {
    private static final String TAG ="ShowHiddenAppAdapter";

    private ArrayList<App> mData = new ArrayList<>();

    public void setData(List<App> data) {
        //  Log.d(TAG, "setData");
        mData.clear();
        if (data!=null) {
            for (App app:
                    data) {
                if (app.getAppSavedInstance().isHidden()) {
                    Log.d(TAG, "setData: "+app.getLabel());
                    mData.add(app);
                }
            }

        }
        if(mData.size()==0&&mListener!=null) mListener.onNoHiddenApp();
        notifyDataSetChanged();
    }
    public interface  onItemClick {
        void onItemClick(App app, int pos);
        void onNoHiddenApp();

    }
    private onItemClick mListener;
    public void setOnItemClick(onItemClick onItemClick) {
        mListener = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_app_dialog_app_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.appIcon)
        ImageView mAppIcon;

        @BindView(R.id.appName)
        TextView mAppName;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        void bind(App app) {
            mAppIcon.setImageDrawable(app.getIcon());
            if(app.getAppSavedInstance().getCustomTitle().isEmpty())
                mAppName.setText(app.getLabel());
            else mAppName.setText(app.getAppSavedInstance().getCustomTitle());
        }

        @Override
        public void onClick(View v) {
            if(mListener!=null) mListener.onItemClick(mData.get(getAdapterPosition()),getAdapterPosition());
        }
    }
}
