package mil.army.usace.hec.cumulus.client.controllers;


import hec.army.usace.hec.cumulus.http.client.CACTrustManager;
import hec.army.usace.hec.cumulus.http.client.CACUtil;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import javax.net.ssl.X509TrustManager;
import mil.army.usace.hec.cumulus.client.model.CumulusObjectMapper;
import mil.army.usace.hec.cwms.http.client.ApiConnectionInfo;
import mil.army.usace.hec.cwms.http.client.HttpRequestBuilderImpl;
import mil.army.usace.hec.cwms.http.client.HttpRequestResponse;
import mil.army.usace.hec.cwms.http.client.request.HttpRequestExecutor;

public class CumulusTokenController {

    private static final String TOKEN_URL = "https://auth.corps.cloud/auth/realms/water/protocol/openid-connect";
    private static final String TOKEN_ENDPOINT = "token";

    public String retrieveToken() throws IOException {
        String retVal = null;
        try {
            HttpRequestExecutor executor =
                new HttpRequestBuilderImpl(new ApiConnectionInfo(TOKEN_URL), TOKEN_ENDPOINT)
                    .addQueryParameter("grant_type", "password")
                    .addQueryParameter("client_id", "cumulus")
                    .addQueryParameter("scope", "openid profile")
                    .addQueryParameter("username", "")
                    .addQueryParameter("password", "")
                    .enableHttp2()
                    .withSslSocketFactory(CACUtil.buildSslSocketFactory(), (X509TrustManager) CACTrustManager.getTrustManager())
                    .post()
                    .withBody("{}")
                    .withMediaType("application/json");
            try (HttpRequestResponse response = executor.execute()) {
                String body = response.getBody();
                if (body != null) {
                    Map<String, String> jsonMap = CumulusObjectMapper.mapJsonStringToMap(body);
                    retVal = jsonMap.get("access_token");
                }
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
        return retVal;
    }
}
