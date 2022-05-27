package hec.army.usace.hec.cumulus.http.client;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CertificateOption {
    private final Certificate _cert;
    private final String _alias;

    public CertificateOption(String alias, Certificate cert) {
        _cert = cert;
        _alias = alias;
    }

    private static String getCN(String dn) {
        String pat = "CN=(.*?),";

        Pattern r = Pattern.compile(pat);
        Matcher m = r.matcher(dn);

        if (m.find()) {
            if (m.groupCount() == 1) {
                return m.group(1);
            }
        }
        return null;
    }

    public String getAlias() {
        return _alias;
    }

    @Override
    public String toString() {
        if (_cert instanceof X509Certificate) {
            X509Certificate xc = (X509Certificate) _cert;
            String subject = getCN(xc.getSubjectX500Principal().getName());
            String issuer = getCN(xc.getIssuerX500Principal().getName());

            if (subject != null && issuer != null) {
                return "Subject: " + subject + " Issuer: " + issuer;
            }
        }
        return _alias;
    }

}

