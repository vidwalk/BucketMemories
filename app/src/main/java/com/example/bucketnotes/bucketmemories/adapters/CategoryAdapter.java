package com.example.bucketnotes.bucketmemories.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bucketnotes.bucketmemories.R;
import com.example.bucketnotes.bucketmemories.entities.Category;
import com.example.bucketnotes.bucketmemories.listeners.OnCategoryClickListener;

import java.util.ArrayList;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> allCategories;
    private List<Category> searchedCategories;
    private OnCategoryClickListener clickListener;
    private String currentText;

    public CategoryAdapter(@NonNull List<Category> categories){
        super();
        this.allCategories = categories;
        this.searchedCategories = new ArrayList<>(categories);
    }

    public CategoryAdapter(@NonNull List<Category> categories, OnCategoryClickListener clickListener){
        this(categories);
        this.clickListener = clickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bind(searchedCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return searchedCategories.size();
    }

    public void filter(String text){
        currentText = text;
        updateFilter();
    }

    public void updateFilter() {
        searchedCategories.clear();
        if(currentText != null && currentText.length() > 0){
            for (Category category : allCategories) {
                if(category.getName().toLowerCase().startsWith(currentText.toLowerCase())){
                    searchedCategories.add(category);
                }
            }
        } else {
            searchedCategories.addAll(allCategories);
        }
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView categoryName;
        private Category category;

        CategoryViewHolder(View view){
            super(view);
            categoryName = (TextView) view.findViewById(R.id.categoryName);
            view.setOnClickListener(this);
        }

        void bind(Category category){
            this.category = category;
            categoryName.setText(category.getName());
            categoryName.setTextColor(category.getColor());
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onClick(category);
            }
        }
    }
}
