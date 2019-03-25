package com.example.bucketnotes.bucketmemories.adapters;


public interface ISwipeItemAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
