package app.com.arresto.arresto_connect.data.models;

import java.util.ArrayList;

public class ExpandedMenuModel {

    String iconName = "";
    int iconImg = -1; // menu icon resource id
    boolean active = true;
    String tag;
    ArrayList<Child> childs;

    public ArrayList<Child> getChilds() {
        return childs;
    }

    public void addChild(Child child) {
        if (childs == null)
            this.childs = new ArrayList<>();
        this.childs.add(child);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getIconImg() {
        return iconImg;
    }

    public void setIconImg(int iconImg) {
        this.iconImg = iconImg;
    }

    public static class Child {
        String name = "";
        boolean active;
        String tag;

        public Child(String name, String tag, boolean active) {
            this.name = name;
            this.tag = tag;
            this.active = active;
        }

        public Child(String name, String tag) {
            this.name = name;
            this.tag = tag;
            this.active = true;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

    }

}