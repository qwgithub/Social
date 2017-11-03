package com.sugan.qianwei.seeyouseeworld.bean;

/**
 * Created by QianWei on 2017/9/6.
 */

public class PersonalDataItem {
    private String title;
    private String description;
    private boolean iconVisible;

    public PersonalDataItem(String title, String description, boolean iconVisible) {
        this.title = title;
        this.description = description;
        this.iconVisible = iconVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIconVisible() {
        return iconVisible;
    }

    public void setIconVisible(boolean iconVisible) {
        this.iconVisible = iconVisible;
    }
}
