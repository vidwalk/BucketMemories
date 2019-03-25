package com.example.bucketnotes.bucketmemories.enums;

import com.example.bucketnotes.R;

public enum TaskState {
    DONE(R.string.done_task),
    EXPIRED(R.string.expired_task),
    ALL(R.string.all_tasks);

    public int pageTitle;

    TaskState(int pageTitle) {
        this.pageTitle = pageTitle;
    }
}
