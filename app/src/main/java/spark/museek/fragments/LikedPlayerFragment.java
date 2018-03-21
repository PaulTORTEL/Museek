package spark.museek.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import spark.museek.R;
import spark.museek.liked.ClickListener;
import spark.museek.liked.LikedAdapter;
import spark.museek.liked.LikedClickListener;
import spark.museek.spotify.SpotifyUser;

public class LikedPlayerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_liked_player, viewGroup, false);


        return view;
    }
}
