package com.example.bucketnotes.bucketmemories.entities;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String name;
    private String email;
    private String pin;
    private ArrayList<Task> tasks;
    private ArrayList<Category> categories;

    public User() {
        this.name = "";
        this.email = "";
        this.pin = "";
        this.tasks = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public User(User user) {
        this.name = user.name;
        this.email = user.email;
        this.pin = user.pin;
        this.tasks = user.tasks;
        this.categories = user.categories;
    }

    public User(String name, String email, String pin, ArrayList<Task> tasks, ArrayList<Category> categories) {
        this.name = name;
        this.email = email;
        this.pin = pin;
        this.tasks = tasks;
        this.categories = categories;
    }

    public User(Parcel parcel) {
        String[] data = new String[3];
        parcel.readStringArray(data);
        this.name = data[0];
        this.email = data[1];
        this.pin = data[2];
        parcel.readTypedList(this.tasks, Task.CREATOR);
        parcel.readTypedList(this.categories, Category.CREATOR);
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPin() {
        return pin;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.name,this.email,this.pin});
        parcel.writeTypedList(this.tasks);
        parcel.writeTypedList(this.categories);
    }
}
