package spark.museek.manager;

//The interface for listen to async json request
public interface RequestListener {

    public void onRequestSuccess(String channel, String json);
    public void onRequestFailed(String channel);
}
