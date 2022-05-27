package hec.army.usace.hec.cumulus.http.client;

import hec.security.CACUtil;
import hec.security.CertificateOption;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509KeyManager;


final class CACKeyManager implements X509KeyManager
{
	private static final Logger LOGGER = Logger.getLogger(CACKeyManager.class.getName());
	private final CACKeyStore _cacKeyStore;
	private final char[] _certificateAlias;

	private CACKeyManager(CACKeyStore cacKeyStore)
	{
		this(cacKeyStore, new char[0]);
	}

	private CACKeyManager(CACKeyStore cacKeyStore, char[] certificateAlias)
	{
		_cacKeyStore = cacKeyStore;
		_certificateAlias = certificateAlias;
	}

	static KeyManager getKeyManager() throws KeyStoreException
	{
		return new CACKeyManager(CACKeyStore.getInstance());
	}

	static KeyManager getKeyManager(char[] certificateAlias) throws KeyStoreException
	{
		return new CACKeyManager(CACKeyStore.getInstance(), certificateAlias);
	}

	@Override
	public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2)
	{
		LOGGER.fine("Choosing client alias ");
		String retval = null;
		try
		{
			String alias;
			if(_certificateAlias != null)
			{
				alias = new String(_certificateAlias);
			}
			else
			{
				alias = CACUtil.getPreferredCertificateOption().map(CertificateOption::getAlias).orElse("");
			}
			boolean certExists = _cacKeyStore.getCertificateOptions().stream().anyMatch(c -> alias.equals(c.getAlias()));
			if(certExists)
			{
				retval = alias;
			}
			else
			{
				retval = _cacKeyStore.chooseClientAlias();
			}
		}
		catch(KeyStoreException e)
		{
			LOGGER.severe("Error reading keystore: " + e.getMessage());
		}
		return retval;
	}

	@Override
	public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2)
	{
		LOGGER.warning("Returning null for server alias");
		return null;
	}

	@Override
	public X509Certificate[] getCertificateChain(String arg0)
	{
		LOGGER.fine(() -> "Getting certificate chain for: " + arg0);
		try
		{
			return _cacKeyStore.getCertificateChain(arg0);
		}
		catch(KeyStoreException e)
		{
			LOGGER.severe("Error getting certificate chain for " + arg0 + ":" + e.getMessage());
		}
		LOGGER.severe("Returning empty certificate chain");
		return new X509Certificate[0];
	}

	@Override
	public String[] getClientAliases(String arg0, Principal[] arg1)
	{
		LOGGER.fine("Getting client  aliases");
		try
		{
			return _cacKeyStore.getClientAliases(arg0);
		}
		catch(KeyStoreException e)
		{
			LOGGER.severe("Error getting aliases:" + e.getMessage());
		}
		return new String[0];
	}

	@Override
	public PrivateKey getPrivateKey(String arg0)
	{
		LOGGER.fine(() -> "Getting private key for: " + arg0);
		if(arg0 != null)
		{
			return _cacKeyStore.getPrivateKey(arg0);
		}
		LOGGER.severe(() -> "Returning null private key for " + arg0);
		return null;
	}

	@Override
	public String[] getServerAliases(String arg0, Principal[] arg1)
	{
		LOGGER.warning("Returning null for server aliases");
		return new String[0];
	}

}
