package com.example.bucketnotes.bucketmemories;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bucketnotes.R;
import com.example.bucketnotes.bucketmemories.adapters.ItemTouchHelperCallback;
import com.example.bucketnotes.bucketmemories.adapters.SubTaskAdapter;
import com.example.bucketnotes.bucketmemories.entities.SubTask;
import com.example.bucketnotes.bucketmemories.entities.Task;
import com.example.bucketnotes.bucketmemories.entities.TaskObject;
import com.example.bucketnotes.bucketmemories.enums.BundleKey;
import com.example.bucketnotes.bucketmemories.enums.TaskState;
import com.example.bucketnotes.bucketmemories.fragments.AddSubTaskDialogFragment;
import com.example.bucketnotes.bucketmemories.fragments.DatePickerFragment;
import com.example.bucketnotes.bucketmemories.validators.Validator;

import java.util.Date;

public class CreateTaskActivity extends BaseActivity implements
        DatePickerFragment.OnDateSelectedListener,
        AddSubTaskDialogFragment.CreateSubTaskDialogListener,
        SubTaskAdapter.ItemSwipeCallback{

    private Task task;
    private TextInputLayout nameWrapper;
    private TextInputLayout descriptionWrapper;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private TextView dateTextView;
    private RecyclerView subTaskRecycler;
    private SubTaskAdapter subTaskAdapter;
    private LinearLayout taskDateLayout;
    private Validator stringValidator = new Validator.StringValidatorBuilder()
            .setNotEmpty()
            .setMinLength(3)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey(BundleKey.TASK.name())) {
            task = bundle.getParcelable(BundleKey.TASK.name());
            initUI();
            setData();
        } else {
            Toast.makeText(getApplicationContext(), "Task not found", Toast.LENGTH_LONG).show();
            finish();
        }
        initDatePickerClick();
        initSubTaskRecycler();
        initCreateTaskButton();
    }

    private void initUI() {
        nameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        descriptionWrapper = (TextInputLayout) findViewById(R.id.descriptionWrapper);
        descriptionEditText = (EditText) findViewById(R.id.descriptionText);
        subTaskRecycler = (RecyclerView) findViewById(R.id.subTaskRecycler);
        taskDateLayout = (LinearLayout) findViewById(R.id.taskDateLayout);
    }

    private void setData() {
        nameEditText.setText(task.getName());
        descriptionEditText.setText(task.getDescription());
        dateTextView.setText(task.getExpireDateString());
    }

    private void fillData() {
        task.setName(nameEditText.getText().toString());
        task.setDescription(descriptionEditText.getText().toString());
    }

    private void showEditDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddSubTaskDialogFragment subTaskDialogFragment = AddSubTaskDialogFragment.newInstance("SubTask dialog");
        subTaskDialogFragment.show(fragmentManager, fragmentManager.getClass().getSimpleName());
    }

    private void initDatePickerClick(){
        taskDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    @Override
    public void onFinishSubTask(SubTask subTask) {
        subTaskAdapter.addNewSubTask(subTask);
    }

    private void initSubTaskRecycler(){
        subTaskAdapter = new SubTaskAdapter(this);
        subTaskRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        subTaskRecycler.setAdapter(subTaskAdapter);
        if (task.getSubTasks().size() != 0){
            subTaskAdapter.addAllSubTask(task.getSubTasks());
        }

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(subTaskAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(subTaskRecycler);
    }

    private void initCreateTaskButton() {
        FloatingActionButton createSubTaskButton = (FloatingActionButton)
                findViewById(R.id.createSubTaskButton);
        createSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_editor, menu);
        menu.findItem(R.id.item_done_task).setTitle(task.isDone() ?
                R.string.undone_task : R.string.done_task);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                saveTask();
                return true;
            case R.id.item_done_task:
                setTaskDone();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setTaskDone() {
        if (task.getStatus() == TaskObject.TaskStatus.DONE) {
            task.setStatus(TaskObject.TaskStatus.NEW);
            saveTask();
        }
        else if (subTaskAdapter.isAllSubTaskDone() || subTaskAdapter.getSubTaskList().size() == 0) {
            task.setStatus(TaskObject.TaskStatus.DONE);
            saveTask();
        } else {
            Toast.makeText(this, "All SubTasks must be done!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTask() {
        if (validate(nameWrapper) && validate(descriptionWrapper)) {
            fillData();
            task.setSubTasks(subTaskAdapter.getSubTaskList());
            Intent result = new Intent();
            result.putExtra(BundleKey.TASK.name(), task);
            result.putExtra(BundleKey.TASK_STATUS.name(), getRootTaskStatus());
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }

    private String getRootTaskStatus(){
        String status;
        if (task.isAllSubTasksDone() || task.isDone()){
            status = TaskState.DONE.name();
        }
        else if (task.isExpire())
            status = TaskState.EXPIRED.name();
        else status = TaskState.ALL.name();

        return status;
    }

    private boolean validate(TextInputLayout wrapper) {
        wrapper.setErrorEnabled(false);
        boolean result = stringValidator.validate(wrapper.getEditText().getText().toString(),
                wrapper.getHint().toString());
        if (!result) {
            wrapper.setErrorEnabled(true);
            wrapper.setError(stringValidator.getLastMessage());
        }
        return result;
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(Date date) {
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        task.setExpireDate(date);
        dateTextView.setText(task.getExpireDateString());
    }

    @Override
    public void onItemRemoved() {
        showUndoSnackBar();
    }

    private void showUndoSnackBar(){
        Snackbar.make(findViewById(R.id.activity_create_task), R.string.sub_task_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subTaskAdapter.restoreRemovedItem();
                    }
                }).show();
    }
}