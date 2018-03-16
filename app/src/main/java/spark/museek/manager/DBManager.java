package spark.museek.manager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import spark.museek.beans.SongLiked;
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
                Log.d("debug", "Song saved!");
                return null;
            }
        }.execute(songs);
    }

    // Returns by a callback if the song passed by argument has already been liked or not
    public void isSongAlreadyLiked(final QueryListener listener, Context context, SpotifySong song) {

        final SongLikedDao dao = getAppDatabase(context).songLikedDao();

        new AsyncTask<SpotifySong, Void, Void>() {
            @Override
            protected Void doInBackground(SpotifySong... params) {

                if (params.length != 1)
                    return null;

                SongLiked song = dao.getSongLikedById(params[0].getSpotifyID());

                if (song == null)
                    listener.onSongExistsReceived(params[0], false);

                else
                    listener.onSongExistsReceived(params[0], true);

                return null;
            }
        }.execute(song);
    }

    public static synchronized DBManager getInstance() {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }
}
