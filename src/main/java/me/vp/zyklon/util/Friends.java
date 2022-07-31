package me.vp.zyklon.util;

import java.util.ArrayList;

public class Friends {
    private static Friends instance = null;

    public final ArrayList<String> friends;

    private Friends() {
        friends = new ArrayList<>();
    }

    public static Friends getInstance() {
        if (instance == null)
            instance = new Friends();
        return instance;
    }

    public void addFriend(String name) {
        friends.add(name);
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public boolean isFriend(String name) {
        return friends.contains(name);
    }

    public String getFriend(String name) {
        return name;
    }

}