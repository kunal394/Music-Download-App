package com.ks.musicdownloader.activity.listsongs;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.ks.musicdownloader.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kunal Singh(knl.singh) on 26-09-2018.
 */
@SuppressWarnings("DanglingJavadoc")
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private List<String> albumNames;
    private HashMap<String, Boolean> albumCheckedStatus;
    private ArtistAdapterCallback adapterCallback;
    private Integer checkedCount;

    ArtistAdapter(List<String> albumNames, HashMap<String, Boolean> albumCheckedStatus, ArtistAdapterCallback adapterCallback) {
        this.albumNames = albumNames;
        this.albumCheckedStatus = albumCheckedStatus;
        this.adapterCallback = adapterCallback;
        initializeCheckedCount();
    }

    void updateAlbumCheckedStatus(HashMap<String, Boolean> albumCheckedStatus) {
        this.albumCheckedStatus = albumCheckedStatus;
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
        holder.bind(albumNames.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CheckedTextView checkedTextView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.checkedTextView = itemView.findViewById(R.id.checked_text_view);
        }

        void bind(String album) {
            // use the sparse boolean array to check
            checkedTextView.setChecked(albumCheckedStatus.get(album));
            checkedTextView.setText(album);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String album = albumNames.get(adapterPosition);
            Boolean newStatus = !albumCheckedStatus.get(album);
            if (newStatus == Boolean.TRUE) {
                checkedCount++;
            } else {
                checkedCount--;
            }
            checkedTextView.setChecked(newStatus);
            albumCheckedStatus.put(album, newStatus);
            adapterCallback.setAlbumCheckedStatus(album, newStatus, checkedCount);
            if (checkedCount == albumNames.size()) {
                adapterCallback.notifyAllChecked();
            }
        }
    }

    /******************Private************************************/
    /******************Methods************************************/

    private void initializeCheckedCount() {
        checkedCount = 0;
        for (Boolean status : albumCheckedStatus.values()) {
            if (status) {
                checkedCount++;
            }
        }
    }
}
