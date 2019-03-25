package com.example.bucketnotes.bucketmemories;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.bucketnotes.bucketmemories.entities.Task;
import com.example.bucketnotes.bucketmemories.enums.ActivityRequest;
import com.example.bucketnotes.bucketmemories.enums.BundleKey;

public class TaskActivity extends BaseActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView descriptionTask;
    private TextView categoryName;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private Task task;
    private String nameTransition, descriptionTransition, categoryTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        if(getIntent().getExtras() == null) {
            finish();
        }
        initUi();
    }

    private void initUi(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        descriptionTask = (TextView) findViewById(R.id.descriptionTask);
        categoryName = (TextView) findViewById(R.id.categoryName);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        setTransitions();
        fillData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskActivity.this, CreateTaskActivity.class);
                intent.putExtra(BundleKey.TASK.name(), task);
                startActivityForResult(intent, ActivityRequest.EDIT_TASK.ordinal());
            }
        });
    }

    private void setTransitions(){
        task = getIntent().getExtras().getParcelable(BundleKey.TASK.name());
        nameTransition = getIntent().getExtras().getString(BundleKey.NAME_TRANSITION.name());
        descriptionTransition = getIntent().getExtras().getString(BundleKey.DESCRIPTION_TRANSITION.name());
        categoryTransition = getIntent().getExtras().getString(BundleKey.CATEGORY_TRANSITION.name());
        if(nameTransition != null && descriptionTransition != null && categoryTransition != null){
            ViewCompat.setTransitionName(collapsingToolbarLayout, nameTransition);
            ViewCompat.setTransitionName(descriptionTask, descriptionTransition);
            ViewCompat.setTransitionName(categoryName, categoryTransition);
        }
    }

    private void fillData(){
        if(task != null){
            collapsingToolbarLayout.setTitle(task.getName());
            descriptionTask.setText(task.getDescription() + "\n" + getString(R.string.description_text));
            categoryName.setText(task.getCategory().getName());
            ((GradientDrawable)categoryName.getBackground()).setStroke(8, task.getCategory().getColor());
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (ActivityRequest.values()[requestCode]){
            case EDIT_TASK:
                if(resultCode == Activity.RESULT_OK){
                    task = data.getParcelableExtra(BundleKey.TASK.name());
                    fillData();
                }
                break;
        }
    }
}
