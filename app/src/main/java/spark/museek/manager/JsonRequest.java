package spark.museek.manager;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * An async task which can be used to retrieves JSON data on a specific URL
 *
 */
public class JsonRequest extends AsyncTask<RequestParam, Integer, Boolean>{

    //The RequesParam stores the URL, headers and parameter of the request
    @Override
    protected Boolean doInBackground(RequestParam... params)  {
        if (params.length < 1)
            return false;

        RequestParam param = params[0];

        URL url = null;
        try {
            url = new URL(
                    param.getURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            param.getListener().onRequestFailed(param.getChannel());
            return false;
        }

        URI finalURI = null;
        try {
            finalURI = URIUtils.createURI(
                    url.getProtocol(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    URLEncodedUtils.format(param.getParams(), "UTF-8"), //we add the params to the URL
                    null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            param.getListener().onRequestFailed(param.getChannel());
            return false;
        }

        HttpGet get = new HttpGet(finalURI);

        param.setupHeaders(get); //we setup the header of the request from the params

        DefaultHttpClient httpClient = new DefaultHttpClient(); //we create an http client to handle the get request
        try {
            HttpResponse response = httpClient.execute(get); //we retrieve the answer as an http response
            param.getListener().onRequestSuccess(param.getChannel(), EntityUtils.toString(response.getEntity())); //if the request succeed, we notify the listener who asked the request
        } catch (Exception e) {
            e.printStackTrace();
            param.getListener().onRequestFailed(param.getChannel());
            return false;
        }

        return true;
    }
}
