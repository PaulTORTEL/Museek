package spark.museek.manager;


import java.util.List;

import spark.museek.beans.SongLiked;
import spark.museek.spotify.SpotifySong;

public interface QueryListener {

    // Allows to retrieve the list of all the liked songs
    void onSongsLikedReceived(List<SongLiked> likedSongs);

    // Allows to know if the song has already been liked or not (existence in the SongLiked table)
    void onSongExistsReceived(SpotifySong song, boolean exists);

}
