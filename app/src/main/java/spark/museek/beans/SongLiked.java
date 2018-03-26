package spark.museek.beans;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class SongLiked {

    @PrimaryKey
    @NonNull
    private String spotifyID;

    private String title = "";
    private String artist = "";
    private String album = "";
    private String imageURL = "";
    private String imageHQURL = "";
    private String duration_ms = "";
    private long date;

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageHQURL() { return imageHQURL; }

    public void setImageHQURL(String imgURL) { this.imageHQURL = imgURL; }


    public String getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(String duration_ms) {
        this.duration_ms = duration_ms;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongLiked that = (SongLiked) o;

        return spotifyID.equals(that.spotifyID);

    }
}
