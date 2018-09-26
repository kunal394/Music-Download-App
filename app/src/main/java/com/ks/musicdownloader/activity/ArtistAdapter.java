package com.ks.musicdownloader.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Kunal Singh(knl.singh) on 26-09-2018.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private String[] albumNames;
    private OnItemClickListener listener;

    public ArtistAdapter(String[] albumNames, OnItemClickListener listener) {
        this.albumNames = albumNames;
        this.listener = listener;
    }

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater vi = LayoutInflater.from(parent.getContext());
//        View v = vi.inflate(R.layout.drawer_list_item, parent, false);
//        TextView tv = (TextView) v.findViewById(android.R.id.text1);
//        return new ViewHolder(tv);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.textView.setText(albumNames[holder.getAdapterPosition()]);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumNames.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        public ViewHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }
}
