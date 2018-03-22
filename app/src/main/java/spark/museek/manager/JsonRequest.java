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

public class JsonRequest extends AsyncTask<RequestParam, Integer, Boolean>{

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
                    URLEncodedUtils.format(param.getParams(), "UTF-8"),
                    null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            param.getListener().onRequestFailed(param.getChannel());
            return false;
        }

        HttpGet get = new HttpGet(finalURI);

        param.setupHeaders(get);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(get);
            param.getListener().onRequestSuccess(param.getChannel(), EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            param.getListener().onRequestFailed(param.getChannel());
            return false;
        }

        return true;
    }
}
