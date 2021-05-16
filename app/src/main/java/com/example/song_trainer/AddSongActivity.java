package com.example.song_trainer;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class AddSongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SongDatabase songDB = SongDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            String title = ((EditText) findViewById(R.id.editTitle)).getText().toString();
            String artist = ((EditText) findViewById(R.id.editArtist)).getText().toString();
            String notes = ((EditText) findViewById(R.id.editNotes)).getText().toString();

            if(title.isEmpty()){
                Snackbar.make(view, "Missing Title!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if(artist.isEmpty()){
                Snackbar.make(view, "Missing Artist Name!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                Song song = new Song(title, artist, 0, 0, notes);
                if (!songDB.songDAO().songExists(song.title, song.artist)) {
                    songDB.songDAO().insertSong(song);
                } else{
                    Snackbar.make(view, "Song already exists!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                this.finish();
            }
        });
    }
}