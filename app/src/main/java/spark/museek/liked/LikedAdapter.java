package spark.museek.liked;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import spark.museek.R;
import spark.museek.beans.PicturedSongLiked;
import spark.museek.beans.SongLiked;
import spark.museek.manager.DBManager;
import spark.museek.manager.QueryListener;
import spark.museek.spotify.SpotifySong;
import spark.museek.spotify.SpotifyUser;

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.ViewHolder> implements QueryListener{



    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView, artistAlbumView;
        public ImageView pictureView;

        public ViewHolder(View view) {
            super(view);
            titleView = view.findViewById(R.id.liked_item_title);
            artistAlbumView = view.findViewById(R.id.liked_item_artist_album);
            pictureView = view.findViewById(R.id.liked_item_picture);
        }
    }

    public LikedAdapter(Context context) {
        this.context = context;
        if (!SpotifyUser.getInstance().isLikesRequested()) {
            DBManager.getInstance().queryListLikedSongs(this, context);
            SpotifyUser.getInstance().setLikesRequested(true);
        }

    }


    @Override
    public LikedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.liked_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PicturedSongLiked song = SpotifyUser.getInstance().getLikedSongs().get(position);
        String title = song.getSongLiked().getTitle();
        if (title.length() >= 50) {
            title = title.substring(0,46);
            title += "...";
        }
        String artist = song.getSongLiked().getArtist();
        if (artist.length() >= 20) {
            artist = artist.substring(0,16);
            artist += "...";
        }
        String album = song.getSongLiked().getAlbum();
        if (album.length() >= 20) {
            album = album.substring(0,16);
            album += "...";
        }
        holder.titleView.setText(title);
        holder.artistAlbumView.setText(artist + " - " + album);

        if (song.isPictureLoaded()) {
            holder.pictureView.setImageBitmap(song.getPicture());
        }
    }

    @Override
    public int getItemCount() {
        if (SpotifyUser.getInstance().getLikedSongs() == null) return 0;
        return SpotifyUser.getInstance().getLikedSongs().size();
    }

    @Override
    public void onSongsLikedReceived(List<SongLiked> songs) {

        for (SongLiked s : songs) {
            PicturedSongLiked song = new PicturedSongLiked(s);
            if (!SpotifyUser.getInstance().getLikedSongs().contains(song))
            SpotifyUser.getInstance().getLikedSongs().add(song);
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LikedAdapter.this.notifyDataSetChanged();
            }
        });

        LikedPictureTask task = new LikedPictureTask();
        LikedSongsParam param = new LikedSongsParam();
        param.adapter = LikedAdapter.this;
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);

    }

    @Override
    public void onSongExistsReceived(SpotifySong song, boolean exists) {

    }
}
