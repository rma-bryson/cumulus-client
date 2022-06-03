package hec.army.usace.hec.cumulus.http.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public final class CACUtil {
    private static final Preferences PREFERENCE_NODE = Preferences.userRoot().node("mil/army/usace/hec/security/cac");

    private CACUtil() {
        throw new AssertionError("Utility class");
    }

    public static SSLSocketFactory buildSslSocketFactory() throws KeyStoreException {
        return buildSslSocketFactory(new char[0]);
    }

    public static SSLSocketFactory buildSslSocketFactory(char[] certificateAlias) throws KeyStoreException {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(new KeyManager[] {CACKeyManager.getKeyManager(certificateAlias)},
                new TrustManager[] {CACTrustManager.getTrustManager()}, null);
            return sc.getSocketFactory();
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            throw new KeyStoreException(e);
        }
    }

    public static List<CertificateOption> getCertificateOptions() throws KeyStoreException {
        return CACKeyStore.getInstance().getCertificateOptions();
    }

    public static void setPreferredCertificateOption(String alias) {
        Preferences node = PREFERENCE_NODE.node("last_cert_used");
        node.put("certificate_alias", alias);
    }

    public static Optional<CertificateOption> getPreferredCertificateOption() throws KeyStoreException {
        Preferences node = PREFERENCE_NODE.node("last_cert_used");
        String alias = node.get("certificate_alias", "");
        return getCertificateOptions().stream().filter(c -> alias.equals(c.getAlias())).findAny();
    }
}