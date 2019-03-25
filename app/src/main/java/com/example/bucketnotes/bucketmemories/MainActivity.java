package com.example.bucketnotes.bucketmemories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.example.bucketnotes.bucketmemories.R;
import com.example.bucketnotes.bucketmemories.adapters.TaskFragmentPagerAdapter;
import com.example.bucketnotes.bucketmemories.data.FileDataSource;
import com.example.bucketnotes.bucketmemories.data.IDataSource;
import com.example.bucketnotes.bucketmemories.entities.Task;
import com.example.bucketnotes.bucketmemories.enums.ActivityRequest;
import com.example.bucketnotes.bucketmemories.enums.BundleKey;
import com.example.bucketnotes.bucketmemories.fragments.TaskListFragment;
import com.example.bucketnotes.bucketmemories.listeners.OnDataChangedListener;

public class MainActivity extends BaseActivity implements TaskListFragment.TaskFragmentCallback, OnDataChangedListener{

    private FloatingActionButton createTaskButton;
    private IDataSource dataSource;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;
    private TaskFragmentPagerAdapter taskFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCreateTaskButton();

        dataSource = new FileDataSource(this, this);

        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int size = (dataSource.getTaskList() == null) ? 0 : dataSource.getTaskList().size();
        Toast.makeText(this, String.format("%d task%s", size, size > 0 ? "s" : ""),
                Toast.LENGTH_SHORT).show();
    }

    private void initCreateTaskButton() {

        createTaskButton = (FloatingActionButton) findViewById(R.id.createTaskButton);
        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = new Task();
                Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
                intent.putExtra(BundleKey.TASK.name(), task);
                startActivityForResult(intent, ActivityRequest.CREATE_TASK.ordinal());
            }
        });
    }

    private void initViewPager(){
        taskFragmentAdapter = new TaskFragmentPagerAdapter(this, getSupportFragmentManager(), dataSource.getTaskList());
        mainTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mainViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mainTabLayout.setupWithViewPager(mainViewPager);
        mainViewPager.setAdapter(taskFragmentAdapter);
    }

    private void forceInitPager(){
        int lastTabPosition = mainTabLayout.getSelectedTabPosition();
        taskFragmentAdapter = new TaskFragmentPagerAdapter(this, getSupportFragmentManager(), dataSource.getTaskList());
        mainViewPager.setAdapter(taskFragmentAdapter);
        mainTabLayout.setScrollPosition(lastTabPosition, 0, false);
        mainViewPager.setCurrentItem(lastTabPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityRequest.CREATE_TASK.ordinal()) {
            if (resultCode == Activity.RESULT_OK) {
                Task task = data.getParcelableExtra(BundleKey.TASK.name());
                if (task != null) {
                    dataSource.createTask(task);
                    forceInitPager();
                }
            }
        } else if (requestCode == ActivityRequest.UPDATE_TASK.ordinal()){
            if (resultCode == RESULT_OK){
                Task task = data.getParcelableExtra(BundleKey.TASK.name());
                if (task != null){
                    dataSource.updateTask(task);
                    forceInitPager();
                }
            }
        }else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onItemClick(Task task) {
        Intent intent = new Intent(this, CreateTaskActivity.class);
        intent.putExtra(BundleKey.TASK.name(), task);
        startActivityForResult(intent, ActivityRequest.UPDATE_TASK.ordinal());
    }

    @Override
    public void notifyDataChanged() {
        forceInitPager();
    }
}
