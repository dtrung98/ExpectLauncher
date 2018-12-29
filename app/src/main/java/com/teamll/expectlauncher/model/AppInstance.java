package com.teamll.expectlauncher.model;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppInstance {
    /**
     * is_folder : false
     * index : 0
     * package_name :
     * padding : 0.555
     * custom_title :
     * custom_background : 0
     * background1 : 0
     * background2 : 0
     * hidden : false
     * lock : false
     * password : 0000
     * is_in_folder : false
     * apps : []
     */

    @SerializedName("is_folder")
    private boolean isFolder = false;
    @SerializedName("index")
    private int index = 0;
    @SerializedName("package_name")
    private String packageName = "";
    @SerializedName("padding")
    private double padding = 4/62f;
    @SerializedName("custom_title")
    private String customTitle ="";
    @SerializedName("custom_background")
    private int customBackground = Color.WHITE;
    @SerializedName("background1")
    private int background1 = Color.WHITE;
    @SerializedName("background2")
    private int background2 = Color.WHITE;
    @SerializedName("hidden")
    private boolean hidden = false;
    @SerializedName("lock")
    private boolean lock = false;
    @SerializedName("password")
    private String password = "";
    @SerializedName("is_in_folder")
    private boolean isInFolder = false;
    @SerializedName("apps")
    private List<AppInstance> apps;

    public boolean isIsFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getPadding() {
        return padding;
    }

    public void setPadding(double padding) {
        this.padding = padding;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public int getCustomBackground() {
        return customBackground;
    }

    public void setCustomBackground(int customBackground) {
        this.customBackground = customBackground;
    }

    public int getBackground1() {
        return background1;
    }

    public void setBackground1(int background1) {
        this.background1 = background1;
    }

    public int getBackground2() {
        return background2;
    }

    public void setBackground2(int background2) {
        this.background2 = background2;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsInFolder() {
        return isInFolder;
    }

    public void setIsInFolder(boolean isInFolder) {
        this.isInFolder = isInFolder;
    }

    public List<AppInstance> getApps() {
        return apps;
    }

    public void setApps(List<AppInstance> apps) {
        this.apps = apps;
    }
}
