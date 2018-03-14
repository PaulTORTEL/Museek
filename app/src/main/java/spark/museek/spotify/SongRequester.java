package spark.museek.spotify;

public interface SongRequester {

    public void onSongLoaded(SpotifySong song);
    public void onLoadFailed();

}
