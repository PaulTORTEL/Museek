package spark.museek.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import spark.museek.beans.SongLiked;

// Methods to be called for the SongLiked entities
@Dao
public interface SongLikedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongsLiked(SongLiked... songs);

    @Update
    void updateSongsLiked(SongLiked... songs);

    @Delete
    void deleteSongsLiked(SongLiked... songs);


    @Query("SELECT * FROM SongLiked ORDER BY date DESC")
    List<SongLiked> getAllSongsLiked();

    @Query("SELECT * FROM SongLiked WHERE spotifyID = :id")
    SongLiked getSongLikedById(String id);


}
