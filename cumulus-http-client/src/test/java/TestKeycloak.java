import static org.junit.jupiter.api.Assertions.assertNotNull;

import hec.army.usace.hec.cumulus.http.client.CACTrustManager;
import hec.army.usace.hec.cumulus.http.client.CACUtil;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;

class TestKeycloak {

    @Test
    void testGetToken() {
        Keycloak instance = Keycloak.getInstance("https://auth.corps.cloud/auth", "water", "", "","cumulus");
        TokenManager tokenmanager = instance.tokenManager();
        String accessToken = tokenmanager.getAccessTokenString();
        assertNotNull(accessToken);
    }

    @Test
    void testGetTokenOkHttp() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        String url = "https://auth.corps.cloud/auth/realms/water/protocol/openid-connect/token";
        String resource = "cumulus/cac/CWMSServerCert.ts";
        URL resourceUrl = getClass().getClassLoader().getResource(resource);
        if (resourceUrl == null) {
            throw new IOException("Failed to get resource: " + resource);
        }
        SSLSocketFactory sslSocketFactory = CACUtil.buildSslSocketFactory();
        OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, (X509TrustManager) CACTrustManager.getTrustManager())
            .build();
        Call call = client.newCall(new Request.Builder()
            .url(url)
            .build());
        Response response = call.execute();
        ResponseBody body = response.body();
        String bodyStr = body.string();
        response.close();
        assertNotNull(bodyStr);

    }
}
