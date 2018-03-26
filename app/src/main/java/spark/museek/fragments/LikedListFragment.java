package spark.museek.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import spark.museek.R;
import spark.museek.beans.PicturedSongLiked;
import spark.museek.beans.SongLiked;
import spark.museek.liked.ClickListener;
import spark.museek.liked.LikedAdapter;
import spark.museek.liked.LikedClickListener;
import spark.museek.spotify.SpotifyUser;


public class LikedListFragment extends Fragment {

    private RecyclerView likedView;
    private RecyclerView.LayoutManager layoutManager;
    private LikedAdapter likedAdapter;

    private boolean fragmentAdded;

    private LikedPlayerFragment likedPlayerFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_liked_list, viewGroup, false);

        this.likedPlayerFragment = new LikedPlayerFragment();
        this.likedPlayerFragment.setListFragment(this);

        likedView = (RecyclerView) view.findViewById(R.id.likedRecylerView);

        layoutManager = new LinearLayoutManager(getActivity());
        likedView.setLayoutManager(layoutManager);

        likedAdapter = new LikedAdapter(getActivity());
        likedView.setAdapter(likedAdapter);

        likedView.setItemAnimator(new DefaultItemAnimator());

        likedView.addOnItemTouchListener(new LikedClickListener(getActivity().getApplicationContext(), likedView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            displayPlayer(SpotifyUser.getInstance().getLikedSongs().get(position));
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "Spotify ID : " + SpotifyUser.getInstance().getLikedSongs().get(position).getSongLiked().getSpotifyID(), Toast.LENGTH_LONG).show();
            }
        }));



        return view;
    }

    public synchronized void displayPlayer(PicturedSongLiked song) {
        if (!this.fragmentAdded)  {
            this.likedPlayerFragment.setSong(song);
            this.fragmentAdded = true;
            FragmentManager fm = getFragmentManager();
            FragmentTransaction tr = fm.beginTransaction();

            tr.add(R.id.likedPlayerContainer, likedPlayerFragment);
            tr.commit();
        }
        else {
            this.likedPlayerFragment.updateSong(song);
        }
    }

    public synchronized void hidePlayer() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();

        tr.remove(fm.findFragmentById(R.id.likedPlayerContainer));
        tr.commit();
        this.fragmentAdded = false;
    }

}
