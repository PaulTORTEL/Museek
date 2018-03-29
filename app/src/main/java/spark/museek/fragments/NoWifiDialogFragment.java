package spark.museek.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import spark.museek.MainActivity;
import spark.museek.R;


public class NoWifiDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final MainActivity mainActivity = (MainActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = "Oops! It seems that the Wifi is disabled! :(";

        builder.setMessage(title)
                .setPositiveButton("Enable WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConnectivityManager connM = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo netInfo = connM.getActiveNetworkInfo();

                        if (netInfo.isConnected())
                            mainActivity.tryConnectFromPreviousToken();
                    }
                });

        return builder.create();
    }

}
