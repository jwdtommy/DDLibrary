package com.dd.news.entry;

import java.io.Serializable;

public class Tabs implements Serializable {
    public Tabs(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public String title;
    public String type;
}