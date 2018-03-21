package spark.museek.manager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import spark.museek.beans.KnownSong;
import spark.museek.beans.SongLiked;
import spark.museek.daos.KnownSongDao;
import spark.museek.daos.SongLikedDao;
import spark.museek.spotify.SpotifySong;


// Singleton to ease the access to the Room database
public class DBManager {

    private static DBManager mInstance;
    private AppDatabase db;

    private DBManager() {

    }

    // Returns the instance of the Room database
    public AppDatabase getAppDatabase(Context context) {

        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, "database-name").build();
        }

        return db;
    }

    // Returns by a callback the list of all the liked songs
    public void queryListLikedSongs(QueryListener listener, Context context) {

        // We fetch the interface for the SongLiked entity
        final SongLikedDao dao = getAppDatabase(context).songLikedDao();

        // We perform our query in an async task in order to free the main thread (if it is it which called the function)
        new AsyncTask<QueryListener, Void, Void>() {
            @Override
            protected Void doInBackground(QueryListener... params) {

                if (params.length != 1)
                    return null;

                // We send a callback to the listener (activity, fragment..) with in parameter, the result of the request
                params[0].onSongsLikedReceived(dao.getAllSongsLiked());
                return null;
            }

        }.execute(listener);

    }

    // Save the songs sent by arguments
    public void saveLikedSong(Context context, SongLiked... songs) {

        // We fetch the proper interface
        final SongLikedDao dao = getAppDatabase(context).songLikedDao();

        // We perform the async task to insert the songs into the DB
        new AsyncTask<SongLiked, Void, Void>() {
            @Override
            protected Void doInBackground(SongLiked... params) {

                if (params.length == 0)
                    return null;

                dao.insertSongsLiked(params);

                return null;
            }
        }.execute(songs);
    }

    public void saveSong(Context context, KnownSong... songs) {

        final KnownSongDao dao = getAppDatabase(context).knownSongDao();

        new AsyncTask<KnownSong, Void, Void>() {
            @Override
            protected Void doInBackground(KnownSong... params) {

                if (params.length == 0)
                    return null;

                dao.insertKnownSongs(params);

                return null;
            }
        }.execute(songs);
    }


    /** Return whether a song is liked or not
     * CAUTION: cannot be called in the main thread!
     * @param context context of the app
     * @param spotifyID id of the song
     * @return
     */
    public boolean isSongAlreadyKnown(Context context, String spotifyID) {

        final KnownSongDao dao = getAppDatabase(context).knownSongDao();

        int countSong = dao.getCountSongByID(spotifyID);

        if (countSong == 1)
            return true;

        return false;
    }

    public static synchronized DBManager getInstance() {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }
}
