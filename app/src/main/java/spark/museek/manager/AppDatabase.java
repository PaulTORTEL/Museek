package spark.museek.manager;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import spark.museek.beans.KnownSong;
import spark.museek.beans.SongLiked;
import spark.museek.daos.KnownSongDao;
import spark.museek.daos.SongLikedDao;

// Abstract class to access to the interfaces of our entities
@Database(entities = {SongLiked.class, KnownSong.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Interface to perform operations on the SongLiked entities
    public abstract SongLikedDao songLikedDao();
    public abstract KnownSongDao knownSongDao();
}
