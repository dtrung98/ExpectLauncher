package com.teamll.expectlauncher.ui.main.appdrawer;

import com.teamll.expectlauncher.model.App;
import com.teamll.expectlauncher.model.Rectangle;

import java.util.ArrayList;

public interface IAppDrawerView {
    void setPositionAndSize(Rectangle rectangle);
    void setAppData(ArrayList<App> data);
}
