package com.sugan.qianwei.seeyouseeworld.bean;


/**
 * Created by QianWei on 2017/5/4.
 */

public class DiseaseResult {

    private int id;
    private String name;
    private String thumb;
    private String introdution;
    private String probability;
    private String imageUrl;

    public DiseaseResult(int id, String name, String thumb, String introdution, String probability, String imageUrl) {
        this.id = id;
        this.name = name;
        this.thumb = thumb;
        this.introdution = introdution;
        this.probability = probability;
        this.imageUrl = imageUrl;
    }

    public DiseaseResult(String name, String thumb, String introdution, String probability) {
        this.id = 0;
        this.name = name;
        this.thumb = thumb;
        this.introdution = introdution;
        this.probability = probability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getIntrodution() {
        return introdution;
    }

    public void setIntrodution(String introdution) {
        this.introdution = introdution;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return getName() + " " + getIntrodution();
    }
}
