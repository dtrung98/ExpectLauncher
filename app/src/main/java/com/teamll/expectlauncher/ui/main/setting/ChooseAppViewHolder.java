package com.teamll.expectlauncher.ui.main.setting;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamll.expectlauncher.R;

public class ChooseAppViewHolder extends RecyclerView.ViewHolder {
    public ImageView appIcon;
    public TextView appName;

    public ChooseAppViewHolder(@NonNull View itemView) {
        super(itemView);

        appIcon = itemView.findViewById(R.id.appIcon);
        appName = itemView.findViewById(R.id.appName);
    }
}

