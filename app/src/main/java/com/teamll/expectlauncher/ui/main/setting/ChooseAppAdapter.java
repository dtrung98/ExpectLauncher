package com.teamll.expectlauncher.ui.main.setting;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;

import java.util.List;

public class ChooseAppAdapter extends RecyclerView.Adapter<ChooseAppViewHolder> {
    private List<App> appsList;

    public ChooseAppAdapter(List<App> appsList) {
        this.appsList = appsList;
    }
    @NonNull
    @Override
    public ChooseAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.choose_app_dialog_app_item, viewGroup, false);
        return new ChooseAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseAppViewHolder chooseAppViewHolder, int position) {
        chooseAppViewHolder.appIcon.setImageDrawable(this.appsList.get(position).getIcon());
        chooseAppViewHolder.appName.setText(this.appsList.get(position).getLabel());
    }

    @Override
    public int getItemCount() {
        return this.appsList.size();
    }
}
