package spark.museek.manager;


import java.util.List;

import spark.museek.beans.SongLiked;
import spark.museek.spotify.SpotifySong;

public interface QueryListener {

    // Allows to retrieve the list of all the liked songs
    void onSongsLikedReceived(List<SongLiked> likedSongs);


}
