package com.example.bucketnotes.bucketmemories.data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.bucketnotes.bucketmemories.entities.Category;
import com.example.bucketnotes.bucketmemories.entities.DefaultCategories;
import com.example.bucketnotes.bucketmemories.entities.Task;
import com.example.bucketnotes.bucketmemories.entities.User;
import com.example.bucketnotes.bucketmemories.enums.BundleKey;
import com.example.bucketnotes.bucketmemories.listeners.OnDataChangedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

public class FileDataSource implements IDataSource, LoaderManager.LoaderCallbacks<Bundle>{

    private static final String TAG = "FILE_DATA_SOURCE";
    private static final String TASKS_FILE_NAME = "tasks";
    private static final String CATEGORIES_FILE_NAME = "categories";
    private static final String USERS_FILE_NAME = "users";
    private static final String CURRENT_USER_FILE_NAME = "currentUser";
    private static final int LOADER_ID = 555555;

    private enum Mode{
        GET_ALL_DATA,
        UPDATE_TASK,
        UPDATE_CATEGORY,
        CREATE_TASK,
        CREATE_CATEGORY,
        CREATE_USER,
        SET_USER,
    }

    private ArrayList<Task> tasks;
    private ArrayList<Category> categories;
    private AppCompatActivity context;
    private OnDataChangedListener dataListener;
    private boolean needLoadEverything;
    private User currentUser;
    private ArrayList<User> users;

    public FileDataSource(AppCompatActivity context, OnDataChangedListener dataListener){
        tasks = new ArrayList<>();
        categories = new ArrayList<>();
        users = new ArrayList<>();
        this.context = context;
        this.dataListener = dataListener;
        needLoadEverything = true;
        doInBackground(Mode.GET_ALL_DATA, null);
    }

    private void doInBackground(Mode mode,@Nullable Bundle args){
        if(args == null){
            args = new Bundle();
        }
        args.putInt(BundleKey.MODE.name(), mode.ordinal());
        if(this.context.getSupportLoaderManager().getLoader(LOADER_ID) != null){
            this.context.getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
        } else {
            this.context.getSupportLoaderManager().initLoader(LOADER_ID, args, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle data) {
        if(needLoadEverything) {
            this.tasks.clear();
            this.tasks.addAll(data.<Task>getParcelableArrayList(BundleKey.TASK.name()));
            this.categories.clear();
            this.categories.addAll(data.<Category>getParcelableArrayList(BundleKey.CATEGORY.name()));
            this.users.clear();
            this.users.addAll(data.<User>getParcelableArrayList(BundleKey.USER.name()));
            this.currentUser = data.getParcelable(BundleKey.CURRENT_USER.name());
            if(dataListener != null){
                dataListener.notifyDataChanged();
            }
            needLoadEverything = false;
        }
    }

    @Override
    public Loader<Bundle> onCreateLoader(int i, Bundle bundle) {
        return new DataLoader(context, bundle);
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {

    }

    @Override
    public ArrayList<Task> getTaskList() {
        return tasks;
    }

    @Override
    public ArrayList<Category> getCategoryList() {
        return categories;
    }

    @Override
    public boolean createTask(@NonNull Task task) {
        Bundle taskBundle = new Bundle();
        taskBundle.putParcelable(BundleKey.TASK.name(), task);
        doInBackground(Mode.CREATE_TASK, taskBundle);
        return tasks.add(task);
    }

    @Override
    public boolean createCategory(@NonNull Category category) {
        Bundle categoryBundle = new Bundle();
        categoryBundle.putParcelable(BundleKey.CATEGORY.name(), category);
        doInBackground(Mode.CREATE_CATEGORY, categoryBundle);
        return categories.add(category);
    }

    @Override
    public boolean updateTask(@NonNull Task task, int index) {
        boolean result = false;
        if(index >= 0 && tasks.size() > index) {
            tasks.set(index, task);
            Bundle taskBundle = new Bundle();
            taskBundle.putParcelable(BundleKey.TASK.name(), task);
            taskBundle.putInt(BundleKey.INDEX.name(), index);
            doInBackground(Mode.UPDATE_TASK, taskBundle);
            result = true;
        }
        return result;
    }

    @Override
    public boolean updateTask(@NonNull Task task) {
        int position = tasks.indexOf(task);
        return updateTask(task, position);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public ArrayList<User> getUserList() {
        return users;
    }

    @Override
    public boolean setCurrentUser(@NonNull User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleKey.USER.name(), user);
        doInBackground(Mode.SET_USER, bundle);
        currentUser = user;
        return true;
    }

    @Override
    public boolean addUser(@NonNull User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BundleKey.USER.name(), user);
        doInBackground(Mode.CREATE_USER, bundle);
        return users.add(user);
    }

    @Nullable
    @Override
    public Category getCategoryById(long id) {
        Category returnCategory = null;
        for(Category category : categories){
            if(category.getId() == id){
                returnCategory = category;
                break;
            }
        }
        return returnCategory;
    }

    @Override
    public boolean isNameFreeForCategory(String name) {
        boolean result = true;
        for (Category category : categories){
            if (category.getName().equalsIgnoreCase(name)){
                result = false;
                break;
            }
        }
        return result;
    }


    protected static class DataLoader extends AsyncTaskLoader<Bundle> {
        private File tasksFile, categoriesFile, usersFile, currentUserFile;
        private ArrayList<Category> categories;
        private ArrayList<Task> tasks;
        private ArrayList<User> users;
        private User currentUser;
        private Gson gson;
        private Mode mode;
        private Bundle args;

        DataLoader(Context context, Bundle args) {
            super(context);
            gson = new Gson();
            this.tasks = new ArrayList<>();
            this.categories = new ArrayList<>();
            this.users = new ArrayList<>();
            this.args = args;
            this.mode = Mode.values()[this.args.getInt(BundleKey.MODE.name())];
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public Bundle loadInBackground() {
            Bundle result = null;
            openDataSource();
            switch (mode) {
                case GET_ALL_DATA:
                    result = getAllData();
                    break;
                case CREATE_TASK:
                    createTask();
                    break;
                case UPDATE_TASK:
                    updateTask();
                    break;
                case CREATE_CATEGORY:
                    createCategory();
                    break;
                case CREATE_USER:
                    createUser();
                    break;
                case SET_USER:
                    setUser();
                default:
                    break;
            }
            return result;
        }

        private void openDataSource(){
            categoriesFile = new File(getContext().getFilesDir(), CATEGORIES_FILE_NAME);
            openFile(categoriesFile);
            tasksFile = new File(getContext().getFilesDir(), TASKS_FILE_NAME);
            openFile(tasksFile);
            usersFile = new File(getContext().getFilesDir(), USERS_FILE_NAME);
            openFile(usersFile);
            currentUserFile = new File(getContext().getFilesDir(), CURRENT_USER_FILE_NAME);
            openFile(currentUserFile);

        }

        private void openFile(File file){
            if(file.exists()){
                openFileAndLoadData(file);
            } else {
                createDefaultDataAndFile(file);
            }
        }

        private void openFileAndLoadData(File file){
            try(FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader)){
                String data = reader.readLine();
                if(data != null){
                    transformDataFromFile(data, file.getName());
                }
            } catch (IOException exception){
                Log.e(TAG, "openFileAndLoadData: ", exception);
            }
        }

        private void createDefaultDataAndFile(File file){
            switch (file.getName()){
                case TASKS_FILE_NAME:
                    for (int i = 0; i < 60; i++){
                        Task task = new Task();
                        task.setName("bla"+i*300);
                        task.setDescription("gokoprthijrthiojrithojrtoihjrtoihjroithjroithjroithjrit");
                        task.setCategory(categories.get(new Random().nextInt(categories.size())));
                        task.setExpireDate(new Date());
                        tasks.add(task);
                    }
                    break;
                case CATEGORIES_FILE_NAME:
                    for(DefaultCategories categoryName : DefaultCategories.values()){
                        categories.add(new Category(categoryName.name()));
                    }
            }
            saveToFile(file);
        }

        private void saveToFile(File file) {
            try(FileWriter fileWriter = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fileWriter)){
                switch (file.getName()){
                    case TASKS_FILE_NAME:
                        writer.write(gson.toJson(tasks));
                        break;
                    case CATEGORIES_FILE_NAME:
                        writer.write(gson.toJson(categories));
                        break;
                    case USERS_FILE_NAME:
                        writer.write(gson.toJson(users));
                        break;
                    case CURRENT_USER_FILE_NAME:
                        writer.write(gson.toJson(currentUser));
                }
            } catch (IOException exception){
                Log.e(TAG, "saveToFile: ", exception);
            }
        }

        private void transformDataFromFile(String data, String name){
            switch (name){
                case TASKS_FILE_NAME:
                    tasks.clear();
                    Type typeTask = new TypeToken<ArrayList<Task>>() {}.getType();
                    tasks.addAll((Collection<? extends Task>) gson.fromJson(data, typeTask));
                    break;
                case CATEGORIES_FILE_NAME:
                    categories.clear();
                    Type typeCategories = new TypeToken<ArrayList<Category>>() {}.getType();
                    categories.addAll((Collection<? extends Category>) gson.fromJson(data, typeCategories));
                    break;
                case USERS_FILE_NAME:
                    users.clear();
                    Type typeUsers = new TypeToken<ArrayList<User>>() {}.getType();
                    users.addAll((Collection<? extends User>) gson.fromJson(data, typeUsers));
                    break;
                case CURRENT_USER_FILE_NAME:
                    Type typeUser = new TypeToken<User>() {}.getType();
                    currentUser = gson.fromJson(data, typeUser);
                    break;
                default:
                    break;
            }
        }

        private Bundle getAllData(){
            Bundle result = new Bundle();
            result.putParcelableArrayList(BundleKey.CATEGORY.name(), categories);
            result.putParcelableArrayList(BundleKey.TASK.name(), tasks);
            result.putParcelableArrayList(BundleKey.USER.name(), users);
            result.putParcelable(BundleKey.CURRENT_USER.name(), currentUser);
            return result;
        }

        private void createUser(){
            users.add((User) args.getParcelable(BundleKey.USER.name()));
            saveToFile(usersFile);
        }

        private void setUser(){
            currentUser = args.getParcelable(BundleKey.USER.name());
            saveToFile(currentUserFile);
        }

        private void createCategory(){
            categories.add((Category) args.getParcelable(BundleKey.CATEGORY.name()));
            saveToFile(categoriesFile);
        }

        private void updateTask(){
            tasks.set(args.getInt(BundleKey.INDEX.name()), (Task) args.getParcelable(BundleKey.TASK.name()));
            saveToFile(tasksFile);
        }

        private void createTask(){
            tasks.add((Task) args.getParcelable(BundleKey.TASK.name()));
            saveToFile(tasksFile);
        }
    }
}