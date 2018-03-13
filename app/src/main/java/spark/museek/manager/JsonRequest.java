package spark.museek.manager;

import android.os.AsyncTask;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JsonRequest extends AsyncTask<RequestParam, Integer, Boolean>{


    @Override
    protected Boolean doInBackground(RequestParam... params)  {
        System.out.println("========== 1 ===========");
        if (params.length < 1)
            return false;

        RequestParam param = params[0];
        System.out.println("========== 2 ===========");


        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        URL url = null;
        try {
            url = new URL(
                    "https://api.spotify.com/v1/browse/new-release");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URI finalURI = null;
        try {
            finalURI = URIUtils.createURI(
                    url.getProtocol(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    URLEncodedUtils.format(parameters, "UTF-8"),
                    null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpGet get = new HttpGet(finalURI);

        System.out.println("using getRequestLine(): " + get.getRequestLine());
        System.out.println("using getURI(): " + get.getURI().toString());

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(get);
            System.out.println("response: "
                    + response.getStatusLine().toString());
            System.out.println( "Response content is:" );
            System.out.println( EntityUtils.toString( response.getEntity() ) );
        } catch (Exception e) {
            System.err.println("HttpClient: An error occurred- ");
            e.printStackTrace();
        }

        return true;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
