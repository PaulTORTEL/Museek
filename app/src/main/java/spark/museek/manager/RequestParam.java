package spark.museek.manager;

import android.app.DownloadManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParam {

    private String URL;
    private String TOKEN;

    private HashMap<String, String> headers = new HashMap<String, String>();
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    private String channel;

    private RequestListener listener;

    public RequestParam(RequestListener listener, String URL) {
        this.URL = URL;
        this.channel = "default";
        this.listener = listener;
    }

    public RequestParam addHeader(String key, String value) {
        this.headers.put(key,value);
        return this;
    }

    public RequestParam addParam(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
        return this;
    }

    public String getURL() {
        return URL;
    }

    public void setupHeaders(HttpGet request) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            request.addHeader(key, value);
        }
    }
    public RequestParam Channel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getChannel() {
        return this.channel;
    }

    public List<NameValuePair> getParams() {
        return this.params;
    }

    public RequestListener getListener() {
        return this.listener;
    }
}
