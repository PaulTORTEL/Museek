package spark.museek.spotify;

import java.util.concurrent.CopyOnWriteArrayList;

public class SpotifyRecommander  {

    private static SpotifyRecommander instance;

    private CopyOnWriteArrayList<SpotifySong> songs;

    private SongRequester listener;

    private boolean songRequested;

    public synchronized void requestSong(SongRequester listener) {
        this.listener = listener;
        if (songs.size() < 2) {
            SpotifyRequester.getInstance().RequestNewReleases();
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
