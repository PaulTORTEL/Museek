package spark.museek.fragments;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import spark.museek.R;
import spark.museek.spotify.SongRequester;
import spark.museek.spotify.SpotifyRecommander;
import spark.museek.spotify.SpotifyRequester;
import spark.museek.spotify.SpotifySong;
import spark.museek.spotify.SpotifyUser;

public class PlayerFragment extends Fragment implements SongRequester {

    private Player player;
    private ImageView trackImageView;
    private TextView titleView;
    private TextView artistView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player, viewGroup, false);
        this.trackImageView = view.findViewById(R.id.track_image);
        this.titleView = view.findViewById(R.id.track_title);
        this.artistView = view.findViewById(R.id.track_artist);


        view.findViewById(R.id.pauseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(player.getMetadata().currentTrack.name);
            }
        });

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotifyRecommander.getInstance().requestSong(PlayerFragment.this);
            }
        });

        return view;
    }



    public void onPlayerLoaded(Player player) {
        this.player = player;
        SpotifyRecommander.getInstance().requestSong(this);
    }

    @Override
    public void onSongLoaded(final SpotifySong song) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                player.playUri(new Player.OperationCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Error error) {
                        System.out.println(error.toString());
                    }
                }, song.getTrackURL(), 0, 1);

                if (song.getImage() != null) {
                    trackImageView.setImageBitmap(song.getImage());
                }
                titleView.setText(song.getTitle());
                artistView.setText(song.getArtist());


            }
        });
    }

    @Override
    public void onLoadFailed() {

    }
}
