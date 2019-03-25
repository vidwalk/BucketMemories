package com.example.bucketnotes.bucketmemories.entities;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;


public class Category implements Parcelable {

    private String name;
    private int color;
    private long id;

    public Category(String name){
        this.name = name;
        Random random = new Random();
        this.color = Color.argb(255, random.nextInt(256), random.nextInt(256),
                random.nextInt(256));
        this.id = System.nanoTime();
    }

    protected Category(Parcel in){
        this.name = in.readString();
        this.color = in.readInt();
        this.id = in.readLong();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.color);
        parcel.writeLong(this.id);
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if(obj instanceof Category){
            if(((Category) obj).getId() == this.id && ((Category) obj).getColor() == this.color
                    && ((Category) obj).getName().equals(this.name)){
                result = true;
            }
        }
        return result;
    }
}
