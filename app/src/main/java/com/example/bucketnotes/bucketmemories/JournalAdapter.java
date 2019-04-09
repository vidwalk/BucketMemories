package com.example.bucketnotes.bucketmemories;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class JournalAdapter extends Adapter<JournalAdapter.TextCardViewHolder> {
    public static ClickListener clickListenerInterface;
    private Activity context;
    private List<JournalEntry> journalEntries;


    public JournalAdapter(List<JournalEntry> list, Activity ctx) {
        journalEntries = list;
        context = ctx;
    }

    @NonNull
    @Override
    public TextCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.list_recycler_items;
        View view = inflater.inflate(layoutIdForListItem, parent,false);

        return new TextCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextCardViewHolder holder, final int position) {
        holder.bind(journalEntries.get(position));

    }

    @Override
    public int getItemCount() {
        if(journalEntries == null){
            return 0;
        }
        return journalEntries.size();
    }

    public class  TextCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewAuthor,textViewTitle,textViewText;
          View view;
        private TextCardViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textViewTitle = itemView.findViewById(R.id.text_title_recycler_list);
            textViewTitle = itemView.findViewById(R.id.text_author_recycler_list);



    }
        void bind(final JournalEntry journalEntry){
            textViewTitle.setText(journalEntry.getTitle());
            textViewText.setText(journalEntry.getText());
            textViewAuthor.setText(journalEntry.getAuthor());


        }


        @Override
        public void onClick(View v) {
            clickListenerInterface.onItemClick(getAdapterPosition(), v);
        }


    }

    public interface ClickListener {
        void onItemClick(int position ,View v);
    }
}
