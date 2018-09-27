package com.ks.musicdownloader.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.SongInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kunal Singh(knl.singh) on 26-09-2018.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<SongInfo> songInfoList;
    private AlbumAdapterCallback adapterCallback;

    AlbumAdapter(List<SongInfo> songInfoList, AlbumAdapterCallback adapterCallback) {
        this.songInfoList = songInfoList;
        this.adapterCallback = adapterCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(songInfoList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return songInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CheckedTextView checkedTextView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.checkedTextView = itemView.findViewById(R.id.checked_text_view);
        }

        void bind(SongInfo songInfo) {
            // use the sparse boolean array to check
            checkedTextView.setChecked(songInfo.isChecked());
            checkedTextView.setText(songInfo.getName());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            SongInfo songInfo = songInfoList.get(adapterPosition);
            Boolean newStatus = !songInfo.isChecked();
            checkedTextView.setChecked(newStatus);
            songInfo.setChecked(newStatus);
            adapterCallback.setSongCheckedStatus(songInfo.getId(), newStatus);
        }
    }
}
