package spark.museek.manager;

import org.apache.http.client.methods.HttpGet;

import java.util.HashMap;
import java.util.Map;

public class RequestParam {

    private String URL;
    private String TOKEN;

    private HashMap<String, String> headers = new HashMap<String, String>();
    private HashMap<String, String> params = new HashMap<String, String>();

    public RequestParam(String URL, String TOKEN) {
        this.URL = URL;
        this.TOKEN = TOKEN;
    }

    public RequestParam addHeader(String key, String value) {
        this.headers.put(key,value);
        return this;
    }

    public RequestParam addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public String getURL() {
        StringBuilder finalURL = new StringBuilder(URL);
        if (params.size() > 0)
            finalURL.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            finalURL.append(key);
            finalURL.append("=");
            finalURL.append(value);
            finalURL.append("&");
        }
        finalURL.deleteCharAt(finalURL.length() - 1);
        return finalURL.toString();
    }

    public void setupHeaders(HttpGet request) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            request.addHeader(key, value);
        }
    }

    public String getTOKEN() {
        return this.TOKEN;
    }
}
