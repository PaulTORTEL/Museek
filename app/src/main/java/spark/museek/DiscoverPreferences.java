package spark.museek;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import java.util.ArrayList;
import java.util.Set;

import spark.museek.spotify.SpotifyUser;

public class DiscoverPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_preferences);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        SettingsFragment sf = new SettingsFragment();
        ft.add(R.id.frag_settings_container, sf, "SETTINGS_FRAGMENT");
        ft.commit();

        sf.setListener(new PreferenceListener() {
            @Override
            public void onPreferenceChanged() {
                SpotifyUser.getInstance().getPlayer().pause(new Player.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        SpotifyUser.getInstance().setParameterChanged();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(DiscoverPreferences.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface PreferenceListener {
        void onPreferenceChanged();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private PreferenceListener prefListener;

        public void setListener(PreferenceListener listener) {
            this.prefListener = listener;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.fragment_preferences);
            setupCheckboxListener();
        }

        private void setupCheckboxListener() {

            final ArrayList<CheckBoxPreference> checkboxes = new ArrayList<>();

            Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                prefListener.onPreferenceChanged();
                for (CheckBoxPreference checkbox : checkboxes) {
                    if (!checkbox.getKey().equals(preference.getKey()) && checkbox.isChecked()) {
                        checkbox.setChecked(false);
                    }
                    else if (checkbox.getKey().equals(preference.getKey()) && !checkbox.isChecked()) {
                        checkbox.setChecked(true);
                    }
                }

                return false;
                }
            };

            CheckBoxPreference checkboxRealease = (CheckBoxPreference) getPreferenceManager().findPreference("checkbox_releases");
            checkboxRealease.setOnPreferenceClickListener(listener);

            CheckBoxPreference checkboxGenre = (CheckBoxPreference) getPreferenceManager().findPreference("checkbox_suggestions");
            checkboxGenre.setOnPreferenceClickListener(listener);

            checkboxes.add(checkboxRealease);
            checkboxes.add(checkboxGenre);


            final EditTextPreference tempoText = (EditTextPreference) getPreferenceManager().findPreference("tempo");
            tempoText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    int tempo = Integer.parseInt((String) o);

                    if (tempo > 200) {
                        tempoText.setText("200");
                        return false;
                    }

                    else if (tempo < 0) {
                        tempoText.setText("0");
                        return false;
                    }
                    prefListener.onPreferenceChanged();
                    return true;
                }
            });


            MultiSelectListPreference multiSelectListPref = (MultiSelectListPreference) getPreferenceManager().findPreference("genre");
            if (multiSelectListPref != null) {
                multiSelectListPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        MultiSelectListPreference mpreference = (MultiSelectListPreference) preference;

                        Set<String> values = (Set<String>) newValue;

                        if (values.size() == 0) {
                            values.add("hard-rock");
                            values.add("pop");
                            values.add("hip-hop");
                            values.add("electro");
                            values.add("classical");

                            mpreference.setValues(values);

                            return false;
                        }

                        prefListener.onPreferenceChanged();

                        return true;
                    }
                });
            }
        }
    }
}
