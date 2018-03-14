package spark.museek.manager;


public interface RequestListener {

    public void onRequestSuccess(String channel, String json);
    public void onRequestFailed(String channel);
}
