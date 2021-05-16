package com.example.song_trainer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlaySongActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView artistText;
    private TextView playCountText;
    private TextView ratingText;
    private TextView noteText;
    private Song mSong;
    SongDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        RatingBar skillBar = findViewById(R.id.ratingBar);

        int songId = getIntent().getIntExtra("songId", 0);
        db = SongDatabase.getInstance(this);
        mSong = db.songDAO().getSongById(songId);


        titleText = findViewById(R.id.tvTitle);
        artistText = findViewById(R.id.tvArtist);
        playCountText = findViewById(R.id.tvPlayCount);
        ratingText = findViewById(R.id.tvRating);
        noteText = findViewById(R.id.tvNotes);

        titleText.setText(mSong.title);
        artistText.setText(mSong.artist);
        playCountText.setText(String.format(java.util.Locale.US, "Play Count: %3d", mSong.playCount));
        String ratingString = String.format(java.util.Locale.US, "Rating: %3.1f", mSong.skillLevel);
        ratingText.setText(ratingString);

        if (!mSong.notes.equals("null")){
            noteText.setText(String.format("Notes: %s", mSong.notes));
        }
        else{
            noteText.setVisibility(View.INVISIBLE);
        }
        skillBar.setNumStars(5);
        skillBar.setRating(mSong.skillLevel);

        // Add callback to print out rating on click
        FloatingActionButton button = findViewById(R.id.submit_rating_button);
        button.setOnClickListener(view -> {
            String rating = String.valueOf(skillBar.getRating());
            Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();

            float new_rating = skillBar.getRating();
            int play_count = mSong.playCount;
            float old_rating = mSong.skillLevel;
            float mean_rating;

            if (play_count == 0) {
                play_count = 1;
                mean_rating = new_rating;
            } else {
                play_count += 1;
                mean_rating = old_rating + ((new_rating - old_rating) / play_count);

            }
            mSong.skillLevel = mean_rating;
            mSong.playCount = play_count;
            db.songDAO().updateSong(mSong);

            this.finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;


            case R.id.action_edit:
                Intent intent = new Intent(this, EditSongActivity.class);
                intent.putExtra("songId", mSong.songId);
                startActivity(intent);
                return true;

            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Song")
                        .setMessage("Do you really want to delete this song entry?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            db.songDAO().deleteSong(mSong);
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                return true;

            case android.R.id.home:
                // replace up button with back button
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}