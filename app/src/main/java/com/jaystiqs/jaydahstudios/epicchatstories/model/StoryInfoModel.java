package com.jaystiqs.jaydahstudios.epicchatstories.model;

/**
 * Created by Jaystiqs on 8/7/2017.
 */

public class StoryInfoModel {

    private int count;
    private int id;
    private String storyName;
    private String storyDesc;
    private String storyImage;

    public StoryInfoModel(int count, int id, String storyName, String storyDesc,String storyImage) {
        this.count = count;
        this.id = id;
        this.storyName = storyName;
        this.storyDesc = storyDesc;
        this.storyImage = storyImage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public String getStoryDesc() {
        return storyDesc;
    }

    public void setStoryDesc(String storyDesc) {
        this.storyDesc = storyDesc;
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }
}
