package com.example.fetchcodingproject;

public class ListItem {
    private int id;
    private int listId;
    private String name;

    public ListItem(int i, int l, String n) {
        id = i;
        listId = l;
        name = n;
    }


    public int getId() {
        return id;
    }

    public int getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nList ID: " + listId + "\nName: " + name;
    }
}
