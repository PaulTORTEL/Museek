package spark.museek.spotify;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.json.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import spark.museek.manager.JsonRequest;
import spark.museek.manager.RequestListener;
import spark.museek.manager.RequestParam;

public class SpotifyRequester implements RequestListener {
    private static SpotifyRequester instance;

    //Request new releases from the Spotify API
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

    //Handle the JSon given by the SpotifyAPI and convert it to a SpotifySong
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
                            song.setImageURL(album.getJSONArray("images").getJSONObject(1).getString("url"),
                                    album.getJSONArray("images").getJSONObject(2).getString("url"));
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

        else if (channel.equals("suggestions")) {
            handleSuggestionsRequest(json);
        }
    }

    @Override
    public void onRequestFailed(String channel) {

    }


    //Request suggestions from the Spotify API managed by the user preferences
    public void RequestSuggestions (Set<String> selections, int tempo) {
        int randomOffset = ThreadLocalRandom.current().nextInt(0, 50 + 1);
        RequestParam param = new RequestParam(this, "https://api.spotify.com/v1/recommendations")
                .Channel("suggestions")
                .addParam("limit", "10")
                .addParam("offset", String.valueOf(randomOffset))
                .addHeader("Authorization", SpotifyUser.getInstance().getHeaderToken());

        if (selections.size() > 0) {

            StringBuilder inlineSelect = new StringBuilder();
            String[] temp = new String[selections.size()];
            selections.toArray(temp);

            for (int i = 0; i < temp.length; i++) {

                if (i + 1 < temp.length)
                    inlineSelect.append(temp[i] + ",");

                else
                    inlineSelect.append(temp[i]);
            }

            param.addParam("seed_genres", inlineSelect.toString());
        }
        float tempoFloat = (float) tempo;

        if (tempoFloat > 0.0 && tempoFloat <= 200.0)
            param.addParam("min_tempo", "" + tempoFloat);

        JsonRequest req = new JsonRequest();
        req.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
    }

    //Handle the json response
    private void handleSuggestionsRequest(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray tracks = obj.getJSONArray("tracks");

            if (tracks.length() < 1) return;

            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);

                SpotifySong song = new SpotifySong();

                song.setSpotifyID(track.getString("id"));
                song.setArtist(track.getJSONArray("artists").getJSONObject(0).getString("name"));
                song.setTitle(track.getString("name"));
                song.setSpotifyID(track.getString("id"));

                JSONObject album = track.getJSONObject("album");
                song.setAlbum(album.getString("name"));
                song.setImageURL(album.getJSONArray("images").getJSONObject(1).getString("url"),
                        album.getJSONArray("images").getJSONObject(2).getString("url"));
                song.setDuration_ms(track.getString("duration_ms"));

                if (song.isImageLoaded())
                    SpotifyRecommander.getInstance().onSongLoaded(song);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


    }
}
