package spark.museek.spotify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpotifySong {

    private String title = "";
    private String artist = "";
    private String album = "";
    private String spotifyID = "";
    private String imageURL = "";
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

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;

        try {
            URL url = new URL(this.imageURL);
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
        sb.append("== Spotify Song ==\n");
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
