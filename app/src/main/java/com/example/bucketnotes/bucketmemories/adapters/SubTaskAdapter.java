package com.example.bucketnotes.bucketmemories.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.bucketnotes.bucketmemories.R;
import com.example.bucketnotes.bucketmemories.entities.SubTask;
import com.example.bucketnotes.bucketmemories.entities.TaskObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.SubTaskHolder> implements ISwipeItemAdapter{
    private List<SubTask> subTaskList;
    private SubTask lastDeletedTask;
    private int lastDeleteTaskPosition;
    private ItemSwipeCallback swipeCallback;

    public interface ItemSwipeCallback{
        void onItemRemoved();
    }

    public SubTaskAdapter(Context context){
        subTaskList = new ArrayList<>();
        swipeCallback = (ItemSwipeCallback) context;
    }

    public void addNewSubTask(SubTask subTask){
        subTaskList.add(subTask);
        notifyItemInserted(subTaskList.size());
    }

    public void addAllSubTask(List<SubTask> subTasks){
        for (SubTask x : subTasks){
            addNewSubTask(x);
        }
    }

    @Override
    public SubTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubTaskHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sub_task, parent, false));
    }

    @Override
    public void onBindViewHolder(SubTaskHolder holder, int position) {
        holder.onBind(subTaskList.get(position));
    }

    @Override
    public int getItemCount() {
        return subTaskList.size();
    }

    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public boolean isAllSubTaskDone(){
        int count = 0;
        for (SubTask subTask : subTaskList) {
            if (subTask.isDone()) {
                ++count;
            }
        }
        return count != 0 && count == subTaskList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(subTaskList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        lastDeletedTask = subTaskList.remove(position);
        lastDeleteTaskPosition = position;
        notifyItemRemoved(position);
        swipeCallback.onItemRemoved();
    }

    public void restoreRemovedItem(){
        if (lastDeletedTask != null){
            subTaskList.add(lastDeleteTaskPosition, lastDeletedTask);
            notifyItemInserted(lastDeleteTaskPosition);
            lastDeletedTask = null;
        }
    }

    class SubTaskHolder extends RecyclerView.ViewHolder implements ISwipeItemHolder{
        private TextView subTaskDescription;
        private SwitchCompat subTaskStatusSwitcher;

        SubTaskHolder(View itemView) {
            super(itemView);
            subTaskDescription = (TextView) itemView.findViewById(R.id.itemSubTaskDesc);
            subTaskStatusSwitcher = (SwitchCompat) itemView.findViewById(R.id.itemSubTaskStatus);
        }

        void onBind(final SubTask subTask){
            subTaskDescription.setText(subTask.getDescription());
            subTaskStatusSwitcher.setChecked(subTask.isDone());
            subTaskStatusSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    subTask.setStatus(isChecked ? TaskObject.TaskStatus.DONE : TaskObject.TaskStatus.NEW);
                }
            });
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
