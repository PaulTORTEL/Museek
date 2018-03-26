package spark.museek.spotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.ArraySet;
import android.util.Log;

import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import spark.museek.manager.DBManager;

public class SpotifyRecommander  {

    private static SpotifyRecommander instance;

    private CopyOnWriteArrayList<SpotifySong> songs;

    private SongRequester listener;

    private boolean songRequested;
    private Context context;

    private String mode = "";
    private Set<String> selections = new ArraySet<>();
    private int tempo = 0;

    private synchronized void manageMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean release_mode = prefs.getBoolean("checkbox_releases", false);
        boolean suggestion_mode = prefs.getBoolean("checkbox_suggestions", false);

        if (release_mode) {
            if (!mode.equals("releases")) {
                mode = "releases";
                songs.clear();
            }
        }

        else if (suggestion_mode) {
            if (!mode.equals("suggestions")) {
                mode = "suggestions";
            }
            songs.clear();
            selections = prefs.getStringSet("genre", null);
            tempo = Integer.parseInt(prefs.getString("tempo", ""));
        }

    }
    public synchronized void requestSong(SongRequester listener, Context context) {
        this.context = context;
        this.listener = listener;

        manageMode(context);

        if (songs.size() < 2) {

            if (mode.equals("releases"))
                SpotifyRequester.getInstance().RequestNewReleases();

            else if (mode.equals("suggestions"))
                SpotifyRequester.getInstance().RequestSuggestions(selections, tempo);
        }

        if (songs.size() > 0) {
            SpotifySong song = songs.get(0);
            songs.remove(0);

            listener.onSongLoaded(song);
        }
        else {
            this.songRequested = true;
        }
    }

    public synchronized void onSongLoaded(SpotifySong song) {

        if (DBManager.getInstance().isSongAlreadyKnown(context, song.getSpotifyID())) {
            Log.d("debug", "song already known!");
            return;
        }

        this.songs.add(song);
        if (this.songRequested) {
            this.songRequested = false;
            if (this.listener == null) return;
            SpotifySong callbackSong = songs.get(0);
            songs.remove(0);
            listener.onSongLoaded(callbackSong);

        }
    }



    private SpotifyRecommander() {
        this.songs = new CopyOnWriteArrayList<SpotifySong>();
    }

    public static synchronized SpotifyRecommander getInstance() {
        if (instance == null) {
            instance = new SpotifyRecommander();
        }
        return instance;
    }


}
