package network.http;

/**
 * Created by homer on 16-10-17.
 */
public interface Callback {
    void onFailure();
    void onSuccess(String content);
}
