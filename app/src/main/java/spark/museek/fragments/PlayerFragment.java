package spark.museek.fragments;


import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;

import java.util.Calendar;
import java.util.List;

import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import spark.museek.R;
import spark.museek.beans.KnownSong;
import spark.museek.beans.PicturedSongLiked;
import spark.museek.beans.SongLiked;

import spark.museek.manager.DBManager;
import spark.museek.manager.QueryListener;
import spark.museek.spotify.SongRequester;
import spark.museek.spotify.SpotifyRecommander;

import spark.museek.spotify.SpotifySong;
import spark.museek.spotify.SpotifyUser;


public class PlayerFragment extends Fragment implements SongRequester, QueryListener {


    private ImageView trackImageView;
    private TextView titleView;
    private TextView artistView;

    private Timer timer;
    private TimerTask task;
    private SeekBar seekBar;

    private SpotifySong currentSong;

    private ImageButton playButton, pauseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_player, viewGroup, false);
        this.trackImageView = view.findViewById(R.id.track_image);
        this.titleView = view.findViewById(R.id.track_title);
        this.artistView = view.findViewById(R.id.track_artist);

        this.seekBar = view.findViewById(R.id.track_progress);


        this.playButton = view.findViewById(R.id.playButton);
        this.pauseButton = view.findViewById(R.id.pauseButton);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;

                if (progress == seekBar.getMax()) {
                    view.findViewById(R.id.playButton).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pauseButton).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SpotifyUser.getInstance().getPlayer().seekToPosition(null, progress * 1000);
            }
       });

        // Timer task to update the seek bar progression
        timer = new Timer();
        task = new TimerTask() {

            public void run() {
                PlaybackState state = SpotifyUser.getInstance().getPlayer().getPlaybackState();

                final int positionSec = (int) (state.positionMs / 1000);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(positionSec);
                    }

                });
            }
        };

        view.findViewById(R.id.playButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyUser.getInstance().getPlayer().resume(null);
                view.findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.pauseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SpotifyUser.getInstance().getPlayer().pause(null);

                view.findViewById(R.id.playButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.pauseButton).setVisibility(View.INVISIBLE);


            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // We store it into the known songs table as well
                KnownSong newSongKnown = new KnownSong();
                newSongKnown.setSpotifyID(currentSong.getSpotifyID());
                DBManager.getInstance().saveSong(getActivity().getApplicationContext(), newSongKnown);

                // We ask another song to be displayed for the user
                SpotifyRecommander.getInstance().requestSong(PlayerFragment.this, getActivity().getApplicationContext());

                view.findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);

            }
        });

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // We create the Song which has just been liked
                if (currentSong == null) return;

                SongLiked songLiked = new SongLiked();

                songLiked.setTitle(currentSong.getTitle());
                songLiked.setAlbum(currentSong.getAlbum());
                songLiked.setArtist(currentSong.getArtist());
                Calendar now = Calendar.getInstance();
                now.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

                songLiked.setDate(now.getTimeInMillis());
                songLiked.setDuration_ms(currentSong.getDuration_ms());
                songLiked.setImageURL(currentSong.getImageURL());
                songLiked.setSpotifyID(currentSong.getSpotifyID());

                PicturedSongLiked song = new PicturedSongLiked(songLiked);
                song.setPicture(currentSong.getImage());
                if (!SpotifyUser.getInstance().getLikedSongs().contains(song))
                    SpotifyUser.getInstance().getLikedSongs().add(0,song);

                // We store it in the Database
                DBManager.getInstance().saveLikedSong(getActivity().getApplicationContext(), songLiked);

                // We store it into the known songs table as well
                KnownSong newSongKnown = new KnownSong();
                newSongKnown.setSpotifyID(currentSong.getSpotifyID());
                DBManager.getInstance().saveSong(getActivity().getApplicationContext(), newSongKnown);

                // We ask another song to be displayed for the user
                SpotifyRecommander.getInstance().requestSong(PlayerFragment.this, getActivity().getApplicationContext());

                view.findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
            }
        });



        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
    }


    public void onPlayerLoaded() {
        timer.schedule(task, 0, 500);
        SpotifyRecommander.getInstance().requestSong(this, getActivity().getApplicationContext());
    }

    public void prepareRestartCurrentSong() {



        SpotifyUser.getInstance().getPlayer().playUri(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                SpotifyUser.getInstance().getPlayer().pause(null);
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Error error) {

            }
        }, currentSong.getTrackURL(), 0, 1);

    }

    public void resumeCurrentSong() {
        SpotifyUser.getInstance().getPlayer().playUri(new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Error error) {

            }
        }, currentSong.getTrackURL(), 0, 1);
    }

    @Override
    public void onSongLoaded(final SpotifySong song) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SpotifyUser.getInstance().getPlayer().playUri(new Player.OperationCallback() {
                    @Override
                    public void onSuccess() {
                       playButton.setVisibility(View.INVISIBLE);
                       pauseButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Error error) {
                        System.out.println(error.toString());
                    }
                }, song.getTrackURL(), 0, 1);

                if (song.getImage() != null) {
                    trackImageView.setImageBitmap(song.getImage());
                }

                String title = song.getTitle();
                String artist = song.getArtist();

                if (title.length() >= 50) {
                    title = title.substring(0,46);
                    title += "...";
                }

                if (artist.length() >= 50) {
                    artist = artist.substring(0,46);
                    artist += "...";
                }

                titleView.setText(title);
                artistView.setText(artist);

                int duration = Integer.parseInt(song.getDuration_ms()) / 1000;
                seekBar.setMax(duration);

                currentSong = song;

            }
        });
    }

    @Override
    public void onLoadFailed() {

    }

    @Override
    public void onSongsLikedReceived(List<SongLiked> likedSongs) {

        for(SongLiked s : likedSongs)
            Log.d("debug", "Song stored ==> " + s.getSpotifyID() + s.getTitle() + " " + s.getArtist() + " " + s.getAlbum() + " " + s.getImageURL());
    }

}
