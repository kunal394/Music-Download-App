package com.ks.musicdownloader.activity.listsongs;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.ks.musicdownloader.R;
import com.ks.musicdownloader.activity.common.SongInfo;

import java.util.List;

/**
 * Created by Kunal Singh(knl.singh) on 26-09-2018.
 */
@SuppressWarnings("DanglingJavadoc")
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<SongInfo> songInfoList;
    private AlbumAdapterCallback adapterCallback;
    private Integer checkedCount;

    AlbumAdapter(List<SongInfo> songInfoList, AlbumAdapterCallback adapterCallback) {
        this.songInfoList = songInfoList;
        this.adapterCallback = adapterCallback;
        initializeCheckedCount();
    }

    void updateSongInfoList(List<SongInfo> songInfoList) {
        this.songInfoList = songInfoList;
        initializeCheckedCount();
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
            if (newStatus == Boolean.TRUE) {
                checkedCount++;
            } else {
                checkedCount--;
            }
            checkedTextView.setChecked(newStatus);
            songInfo.setChecked(newStatus);
            adapterCallback.setSongCheckedStatus(songInfo.getId(), newStatus, checkedCount);
            if (checkedCount == songInfoList.size()) {
                adapterCallback.notifyAllChecked();
            }
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void initializeCheckedCount() {
        checkedCount = 0;
        for (SongInfo songInfo : songInfoList) {
            if (songInfo.isChecked()) {
                checkedCount++;
            }
        }
    }
}
