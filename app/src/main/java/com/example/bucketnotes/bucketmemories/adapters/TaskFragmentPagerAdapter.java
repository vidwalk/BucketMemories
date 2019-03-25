package com.example.bucketnotes.bucketmemories.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.bucketnotes.bucketmemories.entities.Task;
import com.example.bucketnotes.bucketmemories.enums.TaskState;
import com.example.bucketnotes.bucketmemories.fragments.TaskListFragment;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private HashMap<TaskState, ArrayList<Task>> splitTasks = new HashMap<>();

    private Context context;

    public TaskFragmentPagerAdapter(Context context, FragmentManager fragmentManager,
                                     ArrayList<Task> tasks) {
        super(fragmentManager);
        this.context = context;
        splitTasksByStatus(tasks);
    }

    private void splitTasksByStatus(ArrayList<Task> tasks) {
        for (int i = 0; i < TaskState.values().length; i++) {
            splitTasks.put(TaskState.values()[i], new ArrayList<Task>());
        }
        for(Task x : tasks){
            if (x.isDone())
                splitTasks.get(TaskState.DONE).add(x);
            else if (x.isExpire())
                splitTasks.get(TaskState.EXPIRED).add(x);
            else
                splitTasks.get(TaskState.ALL).add(x);
        }

    }

    @Override
    public int getItemPosition(Object object) {
       return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return TaskListFragment.newInstance(splitTasks.get(TaskState.values()[position]));
    }

    @Override
    public int getCount() {
        return TaskState.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(TaskState.values()[position].pageTitle);
    }
}
