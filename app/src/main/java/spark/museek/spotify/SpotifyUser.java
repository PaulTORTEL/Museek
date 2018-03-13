package spark.museek.spotify;


import java.util.Date;

public class SpotifyUser {

    private static SpotifyUser mInstance;

    private final String CLIENT_ID = "48dd7790935c4dd8838fa586bd076aee";
    private final String REDIRECT_URI = "museek://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private final int REQUEST_CODE = 1337;

    private String ACCESS_TOKEN;
    //private Date DATE_EXP_IN;

    public String getClientID() {
        return CLIENT_ID;
    }

    public String getRedirectUri() {
        return REDIRECT_URI;
    }

    public int getRequestCode() {
        return REQUEST_CODE;
    }

    public String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public void setAccessToken(String token) {
        ACCESS_TOKEN = token;
    }

    public void setDateExpIn(int exp_in) {

    }
    public void getDateExpIn() {
    //    return DATE_EXP_IN;
    }

    public static synchronized SpotifyUser getInstance() {
        if (mInstance == null) {
            mInstance = new SpotifyUser();
        }
        return mInstance;
    }

}
