package com.example.song_trainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private onSongListener onSongListener;
    private List<Song> mSongs;
    private List<Song> songsCopy;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    public SongsAdapter(List<Song> songs, onSongListener onSongListener) {
        this.mSongs = songs;
        this.onSongListener = onSongListener;
        this.songsCopy.addAll(songs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View songView = inflater.inflate(R.layout.song_list_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(songView, onSongListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Song song = mSongs.get(position);

        // Set item views based on your views and data model
        TextView idTextView = holder.idTextView;
        idTextView.setText(String.format("%03d", song.songId));
        TextView titleTextView = holder.titleTextView;
        titleTextView.setText(song.title);
        TextView artistTextView = holder.artistTextView;
        artistTextView.setText(song.artist);
        TextView skillLevelTextView = holder.skillLevelTextView;
        skillLevelTextView.setText(String.format("%.1f", song.skillLevel));
        TextView playCountTextView = holder.playCountTextView;
        playCountTextView.setText(String.format("%d", song.playCount));

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView idTextView;
        public TextView titleTextView;
        public TextView artistTextView;
        public TextView playCountTextView;
        public TextView skillLevelTextView;

        private onSongListener onSongListener;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, onSongListener onSongListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            idTextView = itemView.findViewById(R.id.tvId);
            titleTextView = itemView.findViewById(R.id.tvTitle);
            artistTextView = itemView.findViewById(R.id.tvArtist);
            playCountTextView = itemView.findViewById(R.id.tvPlayCount);
            skillLevelTextView = itemView.findViewById(R.id.tvSkillLevel);
            this.onSongListener = onSongListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSongListener.onSongClick(getAbsoluteAdapterPosition());
        }
    }

    public interface onSongListener {
        void onSongClick(int position);
    }

    public void filter(String text) {
        mSongs.clear();
        if(text.isEmpty()){
            mSongs.addAll(itemsCopy);
        } else{
            text = text.toLowerCase();
            for(PhoneBookItem item: itemsCopy){
                if(item.name.toLowerCase().contains(text) || item.phone.toLowerCase().contains(text)){
                    mSongs.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
