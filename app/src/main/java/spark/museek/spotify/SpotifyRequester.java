package spark.museek.spotify;

import android.os.AsyncTask;

import com.google.api.client.json.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

import spark.museek.manager.JsonRequest;
import spark.museek.manager.RequestListener;
import spark.museek.manager.RequestParam;

public class SpotifyRequester implements RequestListener {
    private static SpotifyRequester instance;

    public void RequestNewReleases () {
        int randomOffset = ThreadLocalRandom.current().nextInt(0, 50 + 1);
        RequestParam param = new RequestParam(this, "https://api.spotify.com/v1/browse/new-releases")
                                .Channel("releases")
                                .addParam("limit", "10")
                                .addParam("offset", String.valueOf(randomOffset))
                                .addHeader("Authorization", SpotifyUser.getInstance().getHeaderToken());
        JsonRequest req = new JsonRequest();
        req.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
    }


    private SpotifyRequester() {
    }

    public static synchronized SpotifyRequester getInstance() {
        if (instance == null) {
            instance = new SpotifyRequester();
        }
        return instance;
    }

    private void handleReleasesRequest(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray albums = obj.getJSONObject("albums").getJSONArray("items");
            for (int i = 0; i < albums.length(); i++) {
                JSONObject album = albums.getJSONObject(i);
                String albumID = album.getString("id");
                JsonRequest req = new JsonRequest();
                RequestParam params = new RequestParam(new RequestListener() {
                    @Override
                    public void onRequestSuccess(String channel, String json) {
                        try {
                            JSONObject album = new JSONObject(json);

                            if (album.getJSONArray("images").length() < 1) return;

                            SpotifySong song = new SpotifySong();

                            JSONArray tracks = album.getJSONObject("tracks").getJSONArray("items");

                            if (tracks.length() < 1) return;

                            int randomIndex = ThreadLocalRandom.current().nextInt(0, tracks.length());
                            JSONObject track = tracks.getJSONObject(randomIndex);

                            if (track.getJSONArray("artists").length() < 1) return;

                            song.setArtist(track.getJSONArray("artists").getJSONObject(0).getString("name"));
                            song.setTitle(track.getString("name"));
                            song.setSpotifyID(track.getString("id"));
                            song.setAlbum(album.getString("name"));
                            song.setImageURL(album.getJSONArray("images").getJSONObject(0).getString("url"));
                            song.setDuration_ms(track.getString("duration_ms"));

                            if (song.isImageLoaded())
                            SpotifyRecommander.getInstance().onSongLoaded(song);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRequestFailed(String channel) {

                    }
                }, "https://api.spotify.com/v1/albums/" + albumID)
                        .addHeader("Authorization", SpotifyUser.getInstance().getHeaderToken());

                    req.execute(params);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


    }
    @Override
    public void onRequestSuccess(String channel, String json) {
        if (channel.equals("releases")) {
            handleReleasesRequest(json);
        }
    }

    @Override
    public void onRequestFailed(String channel) {

    }
}
