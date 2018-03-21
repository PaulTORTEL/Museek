package spark.museek;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import spark.museek.fragments.LikedListFragment;
import spark.museek.fragments.PlayerFragment;

public class LikedActivity extends AppCompatActivity {

    private LikedListFragment likedListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        Toolbar toolbar = findViewById(R.id.simpleToolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("   My Likes");
        getSupportActionBar().setIcon(R.drawable.ic_favorite_border_white_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        this.likedListFragment = new LikedListFragment();
        transaction.add(R.id.container_liked, likedListFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (android.R.id.home == item.getItemId())
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
