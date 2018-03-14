package spark.museek;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

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


    public static class SettingsFragment extends PreferenceFragment {

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

            CheckBoxPreference checkboxRealease = (CheckBoxPreference) getPreferenceManager().findPreference("checkbox_release");
            checkboxRealease.setOnPreferenceClickListener(listener);

            CheckBoxPreference checkboxGenre = (CheckBoxPreference) getPreferenceManager().findPreference("checkbox_genre");
            checkboxGenre.setOnPreferenceClickListener(listener);

            checkboxes.add(checkboxRealease);
            checkboxes.add(checkboxGenre);
        }
    }
}
