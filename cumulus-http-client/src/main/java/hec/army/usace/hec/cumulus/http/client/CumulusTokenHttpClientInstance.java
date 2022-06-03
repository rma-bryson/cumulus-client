package hec.army.usace.hec.cumulus.http.client;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

class CumulusTokenHttpClientInstance {

    private static final Logger LOGGER = Logger.getLogger(CumulusTokenHttpClientInstance.class.getName());
    static final String CALL_TIMEOUT_PROPERTY_KEY = "cumulus.token.http.client.calltimeout.seconds";
    static final Duration CALL_TIMEOUT_PROPERTY_DEFAULT = Duration.ofSeconds(0);
    static final String CONNECT_TIMEOUT_PROPERTY_KEY = "cumulus.token.http.client.connecttimeout.seconds";
    static final Duration CONNECT_TIMEOUT_PROPERTY_DEFAULT = Duration.ofSeconds(5);
    static final String READ_TIMEOUT_PROPERTY_KEY = "cumulus.token.http.client.readtimeout.seconds";
    static final Duration READ_TIMEOUT_PROPERTY_DEFAULT = Duration.ofSeconds(TimeUnit.MINUTES.toSeconds(5));

    private static final OkHttpClient INSTANCE = createClient();


    private CumulusTokenHttpClientInstance() {
        throw new AssertionError("Singleton utility class, cannot instantiate");
    }

    // package scoped for testing only
    static OkHttpClient createClient() {
        OkHttpClient retVal = null;
        try {
            SSLSocketFactory sslSocketFactory = CACUtil.buildSslSocketFactory();
            retVal =  new OkHttpClient.Builder()
                .callTimeout(getCallTimeout())
                .connectTimeout(getConnectTimeout())
                .readTimeout(getReadTimeout())
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) CACTrustManager.getTrustManager())
                .build();
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error building SSL Socket Factory", e);
        }
        return retVal;
    }

    private static Duration getReadTimeout() {
        String readTimeoutPropertyValue = System.getProperty(READ_TIMEOUT_PROPERTY_KEY);
        Duration readTimeout = READ_TIMEOUT_PROPERTY_DEFAULT;
        if (readTimeoutPropertyValue == null) {
            LOGGER.log(Level.FINE,
                () -> "Setting " + READ_TIMEOUT_PROPERTY_KEY + " is not set in system properties. Defaulting to " + READ_TIMEOUT_PROPERTY_DEFAULT);
        }
        else {
            LOGGER.log(Level.FINE,
                () -> "Setting " + READ_TIMEOUT_PROPERTY_KEY + " read from system properties as " + readTimeoutPropertyValue);
            readTimeout = Duration.parse(readTimeoutPropertyValue);
        }
        return readTimeout;
    }

    private static Duration getConnectTimeout() {
        String connectTimeoutPropertyValue = System.getProperty(CONNECT_TIMEOUT_PROPERTY_KEY);
        Duration connectTimeout = CONNECT_TIMEOUT_PROPERTY_DEFAULT;
        if (connectTimeoutPropertyValue == null) {
            LOGGER.log(Level.FINE,
                () -> "Setting " + CONNECT_TIMEOUT_PROPERTY_KEY + " is not set in system properties. Defaulting to " +
                    CONNECT_TIMEOUT_PROPERTY_DEFAULT);
        }
        else {
            LOGGER.log(Level.FINE,
                () -> "Setting " + CONNECT_TIMEOUT_PROPERTY_KEY + " read from system properties as " + connectTimeoutPropertyValue);
            connectTimeout = Duration.parse(connectTimeoutPropertyValue);
        }
        return connectTimeout;
    }

    private static Duration getCallTimeout() {
        String callTimeoutPropertyValue = System.getProperty(CALL_TIMEOUT_PROPERTY_KEY);
        Duration callTimeout = CALL_TIMEOUT_PROPERTY_DEFAULT;
        if (callTimeoutPropertyValue == null) {
            LOGGER.log(Level.FINE,
                () -> "Setting " + CALL_TIMEOUT_PROPERTY_KEY + " is not set in system properties. Defaulting to " + CALL_TIMEOUT_PROPERTY_DEFAULT);
        }
        else {
            LOGGER.log(Level.FINER,
                () -> "Setting " + CALL_TIMEOUT_PROPERTY_KEY + " read from system properties as " + callTimeoutPropertyValue);
            callTimeout = Duration.parse(callTimeoutPropertyValue);
        }
        return callTimeout;
    }

    static OkHttpClient getInstance() {
        return INSTANCE;
    }
}
