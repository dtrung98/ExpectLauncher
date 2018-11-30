package com.teamll.expectlauncher.ui.main.search;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SearchView;

import com.teamll.expectlauncher.R;
import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.AppDetail;
import com.teamll.expectlauncher.ui.main.AppLoaderActivity;
import com.teamll.expectlauncher.ui.main.appdrawer.AppDrawerAdapter;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.ItemTouchHelperAdapter;
import com.teamll.expectlauncher.ui.widgets.itemtouchhelper.OnStartDragListener;

import java.util.ArrayList;

public class SearchAppFragment extends Fragment implements OnStartDragListener,AppDrawerAdapter.ItemClickListener,AppLoaderActivity.AppDetailReceiver, SearchView.OnQueryTextListener{
    ArrayList<AppDetail> apps = new ArrayList<>();
    SearchView searchView;
    RecyclerView recyclerView;
    AppDrawerAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_app_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerview);

        searchView.setQueryHint("Search here");
        //recyclerView.setHasFixedSize(true);
        mAdapter = new AppDrawerAdapter(getActivity(), apps);
        recyclerView.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4,GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

        AppLoaderActivity ac = (AppLoaderActivity)getActivity();
        ac.addAppDetailReceiver(this);
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //thay đổi text
        String text = newText;
        mAdapter.Filter(text,apps);

        return false;
    }

    @Override
    public void onLoadComplete(ArrayList<App> data) {
        apps.clear();
        apps.addAll(data);
        mAdapter.setData(data);

    }

    @Override
    public void onLoadReset() {

    }

    @Override
    public void onItemClick(View view, AppDetail appDetail) {
        openApp(view,appDetail);
    }

    private void openApp(View view, AppDetail appDetail) {
        ((AppLoaderActivity)getActivity()).openApp(view,appDetail);
    }

    @Override
    public void onItemLongPressed(View view, AppDetail appDetail) {

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }
}
