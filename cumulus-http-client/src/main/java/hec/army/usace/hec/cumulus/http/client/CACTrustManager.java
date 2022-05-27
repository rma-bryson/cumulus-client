package hec.army.usace.hec.cumulus.http.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public final class CACTrustManager implements X509TrustManager
{
    private static final Logger LOGGER = Logger.getLogger(CACTrustManager.class.getName());
    private final TrustManagerFactory _trustManagerFactory;

    private CACTrustManager(TrustManagerFactory trustManagerFactory)
    {
        _trustManagerFactory = trustManagerFactory;
    }

    public static X509TrustManager getTrustManager(File propertyFile) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ts = KeyStore.getInstance("JKS");
        try (FileInputStream fp = new FileInputStream(propertyFile)) {
            ts.load(fp, null);
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(ts);
        return new CACTrustManager(trustManagerFactory);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
    {
        for(TrustManager trustManager : _trustManagerFactory.getTrustManagers())
        {
            if(trustManager instanceof X509TrustManager)
            {
                try
                {
                    ((X509TrustManager) trustManager).checkClientTrusted(x509Certificates, s);
                }
                catch(CertificateException e)
                {
                    LOGGER.log(Level.WARNING, () -> "Certificate chain not part of trusted certificates for this JRE: " + Arrays.stream(x509Certificates)
                        .map(X509Certificate::getSubjectX500Principal)
                        .map(X500Principal::getName)
                        .collect(Collectors.joining(",")));
                    LOGGER.log(Level.FINE, e, () -> "Certificate chain not part of trusted certificates for this JRE: " + Arrays.toString(x509Certificates));
                    throw e;
                }
            }
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
    {
        for(TrustManager trustManager : _trustManagerFactory.getTrustManagers())
        {
            if(trustManager instanceof X509TrustManager)
            {
                try
                {
                    ((X509TrustManager) trustManager).checkServerTrusted(x509Certificates, s);
                }
                catch(CertificateException e)
                {
                    LOGGER.log(Level.WARNING, () -> "Certificate chain not part of trusted certificates for this JRE: " + Arrays.stream(x509Certificates)
                        .map(X509Certificate::getSubjectX500Principal)
                        .map(X500Principal::getName)
                        .collect(Collectors.joining(",")));
                    LOGGER.log(Level.FINE, e, () -> "Certificate chain not part of trusted certificates for this JRE: " + Arrays.toString(x509Certificates));
                    throw e;
                }
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return Arrays.stream(_trustManagerFactory.getTrustManagers())
            .filter(t -> t instanceof X509TrustManager)
            .map(t -> (X509TrustManager) t)
            .map(X509TrustManager::getAcceptedIssuers)
            .flatMap(Arrays::stream)
            .toArray(X509Certificate[]::new);
    }

}
