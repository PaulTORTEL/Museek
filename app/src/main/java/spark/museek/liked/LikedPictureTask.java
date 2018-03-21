package spark.museek.liked;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import spark.museek.beans.PicturedSongLiked;
import spark.museek.spotify.SpotifyUser;


class LikedSongsParam {
    public LikedAdapter adapter;
}
public class LikedPictureTask extends AsyncTask<LikedSongsParam, Void, Boolean> {

    private LikedAdapter adapter;

    @Override
    protected Boolean doInBackground(LikedSongsParam... params) {
        if (params.length < 1) return false;

        LikedSongsParam param = params[0];

        this.adapter = param.adapter;

        for (PicturedSongLiked song : SpotifyUser.getInstance().getLikedSongs()) {
            if (song.isPictureLoaded()) continue;
            try {
                URL url = new URL(song.getSongLiked().getImageURL());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                song.setPicture(BitmapFactory.decodeStream(input));
                publishProgress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Void... progress) {

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        adapter.notifyDataSetChanged();
    }
}
