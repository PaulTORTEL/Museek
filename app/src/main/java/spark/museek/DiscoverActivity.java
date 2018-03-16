package spark.museek;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.spotify.sdk.android.player.Config;

import java.util.Set;

import spark.museek.fragments.PlayerFragment;
import spark.museek.spotify.SongRequester;
import spark.museek.spotify.SpotifyRecommander;
import spark.museek.spotify.SpotifyRequester;
import spark.museek.spotify.SpotifySong;

import spark.museek.spotify.SpotifyUser;

public class DiscoverActivity extends AppCompatActivity implements ConnectionStateCallback {


    private PlayerFragment playerFragment;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        this.playerFragment = new PlayerFragment();
        transaction.add(R.id.container_player, playerFragment);
        transaction.commit();

        // We set the toolbar up
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Discover");
        getSupportActionBar().setIcon(R.drawable.ic_audiotrack_white_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> selections = prefs.getStringSet("genre", null);
      //  Log.d("debug", "test : " + prefs.getBoolean("checkbox_release", false));
      //  Log.d("debug", "test : " + prefs.getBoolean("checkbox_genre", false));
      //  Log.d("debug", "genres selected: " + selections.toString());


        Config playerConfig = new Config(this, SpotifyUser.getInstance().getAccessToken(), SpotifyUser.getInstance().getClientID());

        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                player = spotifyPlayer;
                player.addConnectionStateCallback(DiscoverActivity.this);
               // mPlayer.addNotificationCallback(DiscoverActivity.this);

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.discover_toolbar, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                Intent newintent = new Intent(this, DiscoverPreferences.class);
                startActivity(newintent);


                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onLoggedIn() {
        this.playerFragment.onPlayerLoaded(player);
    }

    @Override
    public void onLoggedOut() {
        System.out.println("=====  LOGGED OUT  =======");
    }

    @Override
    public void onLoginFailed(Error error) {
        System.out.println("=====  LOGIN FAILED  =======");
    }

    @Override
    public void onTemporaryError() {
        System.out.println("=====  TEMPORARY ERROR  =======");
    }

    @Override
    public void onConnectionMessage(String s) {
        System.out.println("=====  CONNECTION MESSAGE  =======");
    }
}
