package spark.museek.fragments;

import android.app.Fragment;
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
import spark.museek.liked.ClickListener;
import spark.museek.liked.LikedAdapter;
import spark.museek.liked.LikedClickListener;
import spark.museek.spotify.SpotifyUser;


public class LikedListFragment extends Fragment {

    private RecyclerView likedView;
    private RecyclerView.LayoutManager layoutManager;
    private LikedAdapter likedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_liked_list, viewGroup, false);

        likedView = (RecyclerView) view.findViewById(R.id.likedRecylerView);

        layoutManager = new LinearLayoutManager(getActivity());
        likedView.setLayoutManager(layoutManager);

        likedAdapter = new LikedAdapter(getActivity());
        likedView.setAdapter(likedAdapter);

        likedView.setItemAnimator(new DefaultItemAnimator());

        likedView.addOnItemTouchListener(new LikedClickListener(getActivity().getApplicationContext(), likedView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "Spotify ID : " + SpotifyUser.getInstance().getLikedSongs().get(position).getSongLiked().getSpotifyID(), Toast.LENGTH_LONG).show();
            }
        }));

        return view;
    }

}
