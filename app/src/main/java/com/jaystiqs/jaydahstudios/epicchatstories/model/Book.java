package com.jaystiqs.jaydahstudios.epicchatstories.model;

/**
 * Created by Jaystiqs on 8/7/2017.
 */

public class Book {

    private String name, description,writer;
    private int pic,rank;

    public Book(String name, String description, int rank, int pic){

        this.name = name;
        this.description = description;
        this.rank = rank;
        this.pic = pic;

    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}