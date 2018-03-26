package spark.museek.fragments;


import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

import spark.museek.R;
import spark.museek.beans.PicturedSongLiked;

import spark.museek.spotify.SpotifyUser;

public class LikedPlayerFragment extends Fragment {

    private LikedListFragment listFragment;

    private PicturedSongLiked song;

    private ImageView pictureView;
    private TextView titleView, infosView, dateView;

    private ImageButton playButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_liked_player, viewGroup, false);

        this.pictureView = view.findViewById(R.id.track_picture);
        this.titleView = view.findViewById(R.id.track_title);
        this.infosView = view.findViewById(R.id.tracks_infos);
        this.dateView = view.findViewById(R.id.track_liked_date);

        ((ImageButton) view.findViewById(R.id.closePlayerButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseSong(false);
                listFragment.hidePlayer();
            }
        });

        this.playButton = view.findViewById(R.id.playButton);

        this.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SpotifyUser.getInstance().getIsPlaying()) {
                    pauseSong(false);
                }
                else {
                    playSong();
                }
            }
        });
        this.updateView();

        playSong();

        return view;
    }

    public void setListFragment(LikedListFragment listFragment) {
        this.listFragment = listFragment;
    }

    public void setSong(PicturedSongLiked song) {
        this.song = song;
    }

    public void updateSong(PicturedSongLiked song) {
        this.song = song;
        this.updateView();
        this.pauseSong(true);
    }

    private void playSong() {
        if (SpotifyUser.getInstance().getIsPlaying()) return;

        SpotifyUser.getInstance().getPlayer().playUri(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                SpotifyUser.getInstance().setIsPlaying(true);
                playButton.setImageResource(R.mipmap.ic_pause);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        },  "spotify:track:" + this.song.getSongLiked().getSpotifyID(), 0 ,0);
    }
    private void pauseSong(final boolean reset) {
        if (!SpotifyUser.getInstance().getIsPlaying() && !reset) return;

        if (!SpotifyUser.getInstance().getIsPlaying() && reset) {
            playSong();
            return;
        }

        SpotifyUser.getInstance().getPlayer().pause(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                SpotifyUser.getInstance().setIsPlaying(false);
                if (reset)
                    playSong();
                else
                    playButton.setImageResource(R.mipmap.ic_play);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateView() {
        this.pictureView.setImageBitmap(this.song.getPicture());

        String title = this.song.getSongLiked().getTitle();
        if (title.length() >= 50) {
            title = title.substring(0,46);
            title += "...";
        }
        this.titleView.setText(title);

        String artist = this.song.getSongLiked().getArtist();
        String album = this.song.getSongLiked().getAlbum();

        if (artist.length() >= 14) {
            artist = artist.substring(0,11);
            artist += "...";
        }
        if (album.length() >= 14) {
            album = album.substring(0,11);
            album += "...";
        }
        this.infosView.setText(artist + " - " + album);
        Date likedDate = new Date(this.song.getSongLiked().getDate());
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        this.dateView.setText(dt.format(likedDate));
    }

}
