package hec.army.usace.hec.cumulus.http.client;

import java.io.File;
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

    public static SSLSocketFactory buildSslSocketFactory(File propertyFile) throws KeyStoreException {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(new KeyManager[] {CACKeyManager.getKeyManager(new char[0])},
                new TrustManager[] {CACTrustManager.getTrustManager(propertyFile)}, null);
            return sc.getSocketFactory();
        } catch (NoSuchAlgorithmException | CertificateException | KeyManagementException | IOException var2) {
            throw new KeyStoreException(var2);
        }
    }

    public static List<CertificateOption> getCertificateOptions() throws KeyStoreException {
        return CACKeyStore.getInstance().getCertificateOptions();
    }

    public static void setPreferredCertificateOption(String alias) {
        Preferences node = PREFERENCE_NODE.node("last_cert_used");
        node.put("certificate_alias", alias);
    }

    static Optional<CertificateOption> getPreferredCertificateOption() throws KeyStoreException {
        Preferences node = PREFERENCE_NODE.node("last_cert_used");
        String alias = node.get("certificate_alias", "");
        return getCertificateOptions().stream().filter((c) -> {
            return alias.equals(c.getAlias());
        }).findAny();
    }
}
