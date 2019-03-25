package com.example.bucketnotes.bucketmemories.decorators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.bucketnotes.R;


public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable drawable;

    private final int MARGIN_VALUE;

    public DividerItemDecoration(Context context, int margin) {
        drawable = ContextCompat.getDrawable(context, R.drawable.line_divider);
        MARGIN_VALUE = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        final int position = params.getViewAdapterPosition();
        if (position < state.getItemCount()) {
            outRect.set(MARGIN_VALUE, MARGIN_VALUE, MARGIN_VALUE, MARGIN_VALUE);
        } else {
            outRect.setEmpty();
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + drawable.getIntrinsicHeight();

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(c);
        }
    }
}
