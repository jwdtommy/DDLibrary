/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.app.fragment.bean;

/**
 * 菜单项
 * @author yangzc on 15/8/20.
 */
public class MenuItem {

    public int type;
    public int icon;
    public String title;
    public String desc;

    public MenuItem(int type, String title){
        this.type = type;
        this.title = title;
    }

    public MenuItem(int type, String title, String desc) {
        this(type, title);
        this.desc = desc;
    }

    public MenuItem(int type, int icon, String title){
        this(type, title);
        this.icon = icon;
    }

    public MenuItem(int type, int icon, String title, String desc) {
        this(type, title, desc);
        this.icon = icon;
    }

}
