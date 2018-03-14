package spark.museek;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import spark.museek.spotify.SpotifyUser;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Try to retrieve the token from the saving file, will be null if there is nothing saved
        SpotifyUser.getInstance().setAccessToken(TryLoadFromCache("token"));

        // if there is a valid token retrieved
        if (SpotifyUser.getInstance().getAccessToken() != null && SpotifyUser.getInstance().getAccessToken().length() > 0) {

            // Check if the token is still usable (expiration date to be checked)
            if (!isTokenStillValid()) {
                Toast.makeText(getApplicationContext(), "Session expired!", Toast.LENGTH_LONG).show();

                // flush the old token to display the toast only once
                WritefromCache("token", "");
                return;
            }

            // Start a new activity if the token is still usable
            Intent newintent = new Intent(this, DiscoverActivity.class);
            startActivity(newintent);
            finish();
        }
    }

    // Return true if the expiration date > right now's date
    public boolean isTokenStillValid() {

        // We retrieve the expiration date from the saving file
        String exp = TryLoadFromCache("expiration");

        if (exp == null)
            return false;

        String expParams[] = exp.split(" ");

        if (expParams.length != 6)
            return false;

        Calendar expDate = Calendar.getInstance();
        expDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(expParams[0]));
        expDate.set(Calendar.MONTH, Integer.parseInt(expParams[1]));
        expDate.set(Calendar.YEAR, Integer.parseInt(expParams[2]));
        expDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(expParams[3]));
        expDate.set(Calendar.MINUTE, Integer.parseInt(expParams[4]));
        expDate.set(Calendar.SECOND, Integer.parseInt(expParams[5]));

        Calendar rightNow = Calendar.getInstance();

        // We compare the dates
        if (rightNow.getTimeInMillis() < expDate.getTimeInMillis()) {
            SpotifyUser.getInstance().setDateExpIn(expDate);
            return true;
        }

        return false;
    }

    // Function called when the user presses the "connect" button
    public void connectUser(View v) {

        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);

        Button connectBt = findViewById(R.id.connectButton);
        Button quitBt = findViewById(R.id.quitButton);

        connectBt.setEnabled(false);
        quitBt.setEnabled(false);

        // We start to build the Spotify's request to authenticate the user
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyUser.getInstance().getClientID(),
                AuthenticationResponse.Type.TOKEN,
                SpotifyUser.getInstance().getRedirectUri());
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        // We send the request
        AuthenticationClient.openLoginActivity(this, SpotifyUser.getInstance().getRequestCode(), request);
    }

    // Function called when the user presses the "quit" button
    public void quitApp(View v) {
        finish();
        System.exit(0);
    }


    // Function called when Spotify's API sent us the authentication response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == SpotifyUser.getInstance().getRequestCode()) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            // Check if response's type is of type TOKEN
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                System.out.println("======= GETTING TOKEN => " + Thread.currentThread().getName() + "======");
                // We store the access token of the user and we save it into a file
                SpotifyUser.getInstance().setAccessToken(response.getAccessToken());
                WritefromCache("token", SpotifyUser.getInstance().getAccessToken());

                // We get the current date and we calculate the expiration date, since getExpiresIn() is in seconds
                Calendar rightNow = Calendar.getInstance();
                rightNow.add(Calendar.SECOND, response.getExpiresIn());
                SpotifyUser.getInstance().setDateExpIn(rightNow);

                String exp = rightNow.get(Calendar.DAY_OF_MONTH) + " " + rightNow.get(Calendar.MONTH) +
                        " " + rightNow.get(Calendar.YEAR) + " " + rightNow.get(Calendar.HOUR_OF_DAY) + " " + rightNow.get(Calendar.MINUTE) + " " + rightNow.get(Calendar.SECOND);

                // We save the date into a file
                WritefromCache("expiration", exp);

                ProgressBar bar = findViewById(R.id.progressBar);
                bar.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), "Connection successful!", Toast.LENGTH_SHORT).show();

                // The user has successfully connected, we start a new activity
                Intent newintent = new Intent(this, DiscoverActivity.class);
                startActivity(newintent);

            }
            else {

                // An error occured (incorrect type)
                Toast.makeText(getApplicationContext(), "Incorrect type!", Toast.LENGTH_SHORT).show();

                ProgressBar bar = findViewById(R.id.progressBar);
                bar.setVisibility(View.INVISIBLE);

                Button connectBt = findViewById(R.id.connectButton);
                Button quitBt = findViewById(R.id.quitButton);

                connectBt.setEnabled(true);
                quitBt.setEnabled(true);
            }
        }
        else {

            // An error occured (connection failed)
            Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT).show();

            ProgressBar bar = findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);

            Button connectBt = findViewById(R.id.connectButton);
            Button quitBt = findViewById(R.id.quitButton);

            connectBt.setEnabled(true);
            quitBt.setEnabled(true);
        }
    }


    // TEMPORARY functions to write and read app files
    public String TryLoadFromCache(String key) {
        String ret = null;
        try {

            InputStream inputStream = this.openFileInput("cache_" + key + ".txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void WritefromCache(String key, String value) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("cache_" + key + ".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(value);
            outputStreamWriter.close();


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}