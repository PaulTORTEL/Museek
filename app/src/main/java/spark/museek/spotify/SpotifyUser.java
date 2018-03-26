package spark.museek.spotify;


import com.spotify.sdk.android.player.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import spark.museek.beans.PicturedSongLiked;

public class SpotifyUser {

    private static SpotifyUser mInstance;

    private final String CLIENT_ID = "48dd7790935c4dd8838fa586bd076aee";
    private final String REDIRECT_URI = "museek://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private final int REQUEST_CODE = 1337;

    private String ACCESS_TOKEN;
    private Calendar DATE_EXP_IN;

    private CopyOnWriteArrayList<PicturedSongLiked> likedSongs;
    private boolean likesRequested;

    private boolean parameterChanged;

    public String getClientID() {
        return CLIENT_ID;
    }

    public String getRedirectUri() {
        return REDIRECT_URI;
    }

    public int getRequestCode() {
        return REQUEST_CODE;
    }

    public String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public void setAccessToken(String token) {
        ACCESS_TOKEN = token;
    }

    public CopyOnWriteArrayList<PicturedSongLiked> getLikedSongs() { return likedSongs; }

    public String getHeaderToken() {
        return "Bearer " + this.getAccessToken();
    }

    public void setDateExpIn(Calendar exp_in) {
        DATE_EXP_IN = exp_in;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setParameterChanged() {
        this.parameterChanged = true;
    }

    public boolean hasParameterChanged() {
        if (this.parameterChanged) {
            parameterChanged = false;
            return true;
        }
        return false;
    }
    private Player player;

    public boolean isLikesRequested() {
        return likesRequested;
    }

    public void setLikesRequested(boolean likesRequested) {
        this.likesRequested = likesRequested;
    }

    public Calendar getDateExpIn() {
        return DATE_EXP_IN;
    }

    private SpotifyUser() {
        this.likedSongs = new CopyOnWriteArrayList<PicturedSongLiked>();
    }

    public static synchronized SpotifyUser getInstance() {
        if (mInstance == null) {
            mInstance = new SpotifyUser();
        }
        return mInstance;
    }

}
