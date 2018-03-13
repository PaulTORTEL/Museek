package spark.museek.spotify;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONObject;

import spark.museek.manager.JsonRequest;
import spark.museek.manager.RequestParam;

public class SpotifyRequester {
    private static SpotifyRequester instance;
    private static Context context;

    public void test (String token) {

        System.out.println("========== TEST ===========");
        System.out.println("Token : " + token);
        RequestParam param = new RequestParam("https://api.spotify.com/v1/browse/new-releases", token)
                                .addHeader("Authorization","Bearer " + token);
        JsonRequest req = new JsonRequest();
        req.execute(param);
        System.out.println("========== TEST LAUNCHED ===========");
    }


    private SpotifyRequester() {
    }

    public static synchronized SpotifyRequester getInstance(Context context) {
        if (instance == null) {
            instance = new SpotifyRequester();
        }
        instance.context = context;
        return instance;
    }

}
