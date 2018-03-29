package spark.museek.spotify;

//Listener to handle song request
public interface SongRequester {

    public void onSongLoaded(SpotifySong song);
    public void onLoadFailed();

}
