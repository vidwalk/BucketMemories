package com.example.bucketnotes.bucketmemories.adapters;

import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bucketnotes.bucketmemories.R;
import com.example.bucketnotes.bucketmemories.entities.Task;

import java.util.List;

public class TaskAdapterStyles extends RecyclerView.Adapter<TaskAdapterStyles.TaskViewHolderRunning> {

    private List<Task> tasks;

    public TaskAdapterStyles(@NonNull List<Task> tasks) {
        super();
        this.tasks = tasks;
    }

    @Override
    public TaskViewHolderRunning onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(new ContextThemeWrapper(parent.getContext(), viewType))
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolderRunning(view) ;
    }

    @Override
    public int getItemViewType(int position) {
        return tasks.get(position).isExpire() ? R.style.Item_Expired : R.style.Item;
    }

    @Override
    public void onBindViewHolder(TaskViewHolderRunning holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return (tasks == null) ? 0 : tasks.size();
    }

    public void add(Task task) {
        tasks.add(task);
        notifyItemInserted(tasks.size() - 1);
    }

    public class TaskViewHolderRunning extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;

        TaskViewHolderRunning(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameTextView);
            description = (TextView) itemView.findViewById(R.id.descriptionTextView);
        }

        void bind(Task task) {
            name.setText(task.getName());
            description.setText(task.getDescription());
        }
    }
}
