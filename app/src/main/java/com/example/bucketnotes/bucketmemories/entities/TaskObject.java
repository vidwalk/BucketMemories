package com.example.bucketnotes.bucketmemories.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;


public abstract class TaskObject implements Parcelable {

    public enum TaskStatus {
        NEW,
        DONE
    }

    private String uuid;
    private String description;
    private TaskStatus status;
    private Category category;

    public TaskObject() {
        status = TaskStatus.NEW;
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeString(this.description);
        dest.writeParcelable(this.category, flags);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected TaskObject (Parcel in) {
        this.uuid = in.readString();
        this.description = in.readString();
        this.category = in.readParcelable(Category.class.getClassLoader());
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : TaskStatus.values()[tmpStatus];
    }

    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskObject that = (TaskObject) o;

        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;

    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TaskObject{" +
                "uuid='" + uuid + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
