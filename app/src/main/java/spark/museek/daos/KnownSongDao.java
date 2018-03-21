package spark.museek.daos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import spark.museek.beans.KnownSong;

@Dao
public interface KnownSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertKnownSongs(KnownSong... songs);

    @Query("SELECT COUNT(*) FROM KnownSong WHERE spotifyID = :id")
    int getCountSongByID(String id);
}
