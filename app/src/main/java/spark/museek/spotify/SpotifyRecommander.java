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

//Class used to request a song from spotify API
public class SpotifyRecommander  {

    private static SpotifyRecommander instance;

    //ArrayList of current queued songs
    private CopyOnWriteArrayList<SpotifySong> songs;

    //The listener who asked a song
    private SongRequester listener;

    //If a song was requested
    private boolean songRequested;

    private Context context;

    private String mode = "";
    private Set<String> selections = new ArraySet<>();
    private int tempo = 0;

    //Get the user preferences to handle if he wants new releases or recommanded song
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

    //request a song and setup the listener
    public synchronized void requestSong(SongRequester listener, Context context) {
        this.context = context;
        this.listener = listener;

        //we handle the user preferences
        manageMode(context);

        //if the song queue is close to get empty
        if (songs.size() < 2) {
            //we request to the SpotifyRequest new songs from the Spotify Database according to the user preferences

            if (mode.equals("releases"))
                SpotifyRequester.getInstance().RequestNewReleases();

            else if (mode.equals("suggestions"))
                SpotifyRequester.getInstance().RequestSuggestions(selections, tempo);
        }

        //if the songs queue is not empty, we callback immediatly the listener giving the song
        if (songs.size() > 0) {
            SpotifySong song = songs.get(0);
            songs.remove(0);

            listener.onSongLoaded(song);
        }
        else {
            //if the queue is empty, we wait for the SpotifyRequester to fill it
            this.songRequested = true;
        }
    }

    //When the SpotifyRequest has loaded a song and callback the spotify recommander

    public synchronized void onSongLoaded(SpotifySong song) {

        //If the user have already seen this song
        if (DBManager.getInstance().isSongAlreadyKnown(context, song.getSpotifyID())) {
            Log.d("debug", "song already known!");
            return;
        }
        //we add the song to the queue
        this.songs.add(song);
        //and if the user was waiting for a song we callback the listener with the song
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
