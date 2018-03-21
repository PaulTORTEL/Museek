package spark.museek.beans;

import android.graphics.Bitmap;
import android.util.Log;

public class PicturedSongLiked {


    private SongLiked songLiked;

    private Bitmap picture;
    private boolean pictureLoaded;

    public PicturedSongLiked(SongLiked songLiked) {
        this.songLiked = songLiked;
    }

    public SongLiked getSongLiked() {
        return songLiked;
    }

    public void setSongLiked(SongLiked songLiked) {
        this.songLiked = songLiked;
    }

    public Bitmap getPicture() {
        return this.picture;
    }
    public void setPicture(Bitmap picture) {
        this.picture = picture;
        this.pictureLoaded = true;
    }
    public boolean isPictureLoaded() {
        return this.pictureLoaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PicturedSongLiked that = (PicturedSongLiked) o;
        return songLiked.equals(that.songLiked);
    }

}
