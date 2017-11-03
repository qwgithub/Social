package com.sugan.qianwei.seeyouseeworld.bean;

/**
 * Created by QianWei on 2017/8/22.
 */

public class SettingsItem {
    private int imageID;
    private String description;
    private String explain;

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public SettingsItem(int imageID, String description) {
        this.imageID = imageID;
        this.description = description;
    }

    public SettingsItem(String description) {
        this.description = description;
    }

    public SettingsItem(String description, String explain) {
        this.description = description;
        this.explain = explain;
    }
}
