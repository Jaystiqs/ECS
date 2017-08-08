package com.jaystiqs.jaydahstudios.epicchatstories.model;

/**
 * Created by Jaystiqs on 8/7/2017.
 */

public class ListCount {

    private int count;
    private String storyName;

    public ListCount(int count, String storyName) {
        this.count = count;
        this.storyName = storyName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }
}
