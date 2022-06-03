package hec.army.usace.hec.cumulus.http.client;

import static hec.army.usace.hec.cumulus.http.client.CumulusTokenHttpClientInstance.createClient;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class CumulusTokenHttpRequestExecutor {

    private CumulusTokenHttpRequestExecutor() {

    }

    public static String execute(String url) throws IOException {
        String retVal = null;
        Call call = createClient().newCall(new Request.Builder()
            .url(url)
            .build());
        try (Response response = call.execute()) {
            ResponseBody body = response.body();
            if (body != null) {
                retVal = body.string();
            }
        }
        return retVal;
    }
}
