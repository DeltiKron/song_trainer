package com.example.song_trainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.android.volley.toolbox.Volley.newRequestQueue;

public class SongListActivity extends AppCompatActivity implements SongsAdapter.onSongListener {
    private static final String TAG = "SongListActivity";
    private List<Song> mSongs;
    private String m_Text = "";

    private void update() {
        // Lookup the recyclerview in activity layout
        RecyclerView rvSongs = findViewById(R.id.rvSongs);

        // Fetch list of songs
        SongDatabase songDB = SongDatabase.getInstance(this);
        mSongs = songDB.songDAO().getSongList();

        // Create adapter passing in the sample user data
        SongsAdapter adapter = new SongsAdapter(mSongs, this);
        // Attach the adapter to the recyclerview to populate items
        rvSongs.setAdapter(adapter);
        // Set layout manager to position the items
        rvSongs.setLayoutManager(new
                LinearLayoutManager(this));
        // That's all!
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


            case R.id.action_import:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Import from JSON endpoint");
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
                View viewInflated = LayoutInflater.from(this).inflate(R.layout.string_query_dialog,findViewById(android.R.id.content),false);
                // Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                // input.setText("192.168.2.132:5000/songs/export_songs");
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    m_Text = input.getText().toString();
                    this.importSongsFromJSON(m_Text,SongDatabase.getInstance(this));

                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                SongDatabase songDB = SongDatabase.getInstance(this);
                this.importSongsFromJSON(m_Text,songDB);

            default:
                return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton add_song_button = findViewById(R.id.add_song_button);
        add_song_button.setOnClickListener(view -> {
                    Intent intent = new Intent(this, AddSongActivity.class);
                    startActivity(intent);
                }
        );

        this.update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.update();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.update();
    }

    @Override
    public void onSongClick(int position) {
        Log.d(TAG, "onSongClick clicked at position " + String.format("%3d", position));
        Song song = mSongs.get(position);
        Toast.makeText(getApplicationContext(), song.title, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PlaySongActivity.class);
        intent.putExtra("songId", song.songId);
        this.startActivity(intent);
    }

    public void importSongsFromJSON(String url, SongDatabase db) {
        // Instantiate the RequestQueue.
        RequestQueue queue = newRequestQueue(this);


        if(!url.startsWith("http")){
            url = String.format("http://%s",url);
        }
// Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                response -> {
                    try {

                        Log.d("JsonArray", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject entry = response.getJSONObject(i);
                            Song song = Song.from_json(entry);
                            Log.d("title", song.title);
                            boolean exists = db.songDAO().songExists(song.title, song.artist);
                            Log.d("exists", String.valueOf(exists));
                            if (!exists) {
                                db.songDAO().insertSong(song);
                            }
                        }

                    } catch (JSONException e) {
                        Snackbar.make( findViewById(android.R.id.content), "Import Failed!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        e.printStackTrace();
                    }
                    Snackbar.make( findViewById(android.R.id.content), "Import Successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

/*
                        try {
                            entry = response.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("JSONEntry", entry.toString());
*/

                }, error -> System.out.println("That didn't work!"));

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

}