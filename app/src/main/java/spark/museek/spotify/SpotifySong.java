package spark.museek.spotify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpotifySong {


    private String spotifyID = "";

    private String title = "";
    private String artist = "";
    private String album = "";
    private String imageURL = "";
    private String imageHQURL = "";
    private String duration_ms = "";

    private Bitmap image;
    private boolean imageLoaded;



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

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageHQURL() { return imageHQURL; }

    public void setImageHQURL(String imgURL) { this.imageHQURL = imgURL; }


    public void setImageURL(String imageHighQURL, String imageLowQURL) {
        this.imageURL = imageLowQURL;
        this.imageHQURL = imageHighQURL;

        try {
            URL url = new URL(imageHighQURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            this.image = BitmapFactory.decodeStream(input);

            this.imageLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDuration_ms(String duration_ms) {
        this.duration_ms = duration_ms;
    }

    public String getDuration_ms() {
        return this.duration_ms;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTrackURL() {
        return "spotify:track:" + this.spotifyID;
    }
    public SpotifySong() {

    }

    public boolean isImageLoaded () {
        return this.imageLoaded;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("== Spotify SongLiked ==\n");
        sb.append("Title : ");
        sb.append(this.title);
        sb.append("\n");
        sb.append("Album : ");
        sb.append(this.album);
        sb.append("\n");
        sb.append("Artist : ");
        sb.append(this.artist);
        sb.append("\n");
        sb.append("ID : ");
        sb.append(this.spotifyID);
        sb.append("\n");
        sb.append("Track URL : ");
        sb.append(this.getTrackURL());
        sb.append("\n");
        sb.append("Picture URL : ");
        sb.append(this.imageURL);
        sb.append("\n============");
        return sb.toString();
    }


}
