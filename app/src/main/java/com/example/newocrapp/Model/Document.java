package com.example.newocrapp.Model;

import android.graphics.Bitmap;

public class Document {

    private String date;
    private float size;
    private byte[] image;
    private String name;
    private String text;

    public Document(String name, String date, float size, byte[] image, String text) {
        this.name = name;
        this.text = text;
        this.date = date;
        this.size = size;
        this.image = image;
    }


    public Document(String name, String date, float size, byte[] image) {
        this.name = name;
        this.date = date;
        this.size = size;
        this.image = image;
    }

    public Document(String name, String date, float size, Bitmap blob, String string) {
        this.name = name;
        this.date = date;
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
