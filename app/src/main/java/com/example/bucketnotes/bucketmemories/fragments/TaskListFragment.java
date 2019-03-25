package com.example.bucketnotes.bucketmemories.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bucketnotes.bucketmemories.R;
import com.example.bucketnotes.bucketmemories.adapters.TaskAdapter;
import com.example.bucketnotes.bucketmemories.entities.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskListFragment extends Fragment implements TaskAdapter.OnItemClickListener{
    private static final String ARGS_TASK_LIST = "ARGS_TASK_LIST";

    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private RecyclerView taskRecycler;
    private TaskFragmentCallback callback;

    public interface TaskFragmentCallback{
        void onItemClick(Task task);
    }

    public static TaskListFragment newInstance(List<Task> taskList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGS_TASK_LIST, new ArrayList<>(taskList));
        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (TaskFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        taskList = getArguments().getParcelableArrayList(ARGS_TASK_LIST);

        initRecycler(view);

        setHasOptionsMenu(true);

        return view;
    }

    private void initRecycler(View view) {
        taskRecycler = (RecyclerView) view.findViewById(R.id.taskRecyclerView);
        taskAdapter = new TaskAdapter(taskList, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        taskRecycler.setAdapter(taskAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_change_layout:
                changeLayout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.layout_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        taskRecycler.setLayoutManager(layoutManager);
    }

    private void changeLayout() {
        if (taskRecycler.getLayoutManager() instanceof GridLayoutManager) {
            setLayoutManager(getLinearLayoutManager());
        } else {
            setLayoutManager(getGridLayoutManager());
        }
    }

    @NonNull
    private GridLayoutManager getGridLayoutManager() {
        return new GridLayoutManager(getContext(), getResources()
                .getInteger(R.integer.column_count));
    }

    @NonNull
    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public void onItemClick(Task task) {
        if (callback != null){
            callback.onItemClick(task);
        }
    }
}