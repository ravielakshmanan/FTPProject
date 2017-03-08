package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;



public class FTPKeyStore {

	/* method to instantiate and return keystore */
	public static KeyStore keyStoreTLS(String keyStoreType, String keyStorePath, String password){

		KeyStore keyStore = null;

		try{
			/* create keystore file input, get instance, and load keystore password */
			FileInputStream inputStream = new FileInputStream(keyStorePath);
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(inputStream, password.toCharArray());
			inputStream.close();
			return keyStore;
			/* handle errors */
		} catch (FileNotFoundException e){
			KeystoreException.invalidKeyStoreFile(keyStorePath);
		} catch (KeyStoreException e){
			KeystoreException.invalidKeyStoreType(keyStoreType);
		} catch (IOException e){
			KeystoreException.invalidKeyStoreData(keyStorePath);
		} catch (NoSuchAlgorithmException e){
			KeystoreException.invalidAlgorithm(keyStorePath);
		} catch (CertificateException e){
			KeystoreException.invalidCertificate(keyStorePath);
		}
		return keyStore;
	}

	/* method to instaniate and return keystore manager */
	public static KeyManagerFactory keyManagerTLS(KeyStore keyStore, String password){

		KeyManagerFactory keyManagerFactory = null;

		try{
			/* initialize instance of the key manager */
			keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
			keyManagerFactory.init(keyStore, password.toCharArray());
			return keyManagerFactory;
			/* handle errors */
		} catch (NoSuchAlgorithmException e){
			TrustManagerException.invalidAlgorithm();
		} catch (NoSuchProviderException e){
			TrustManagerException.invalidProvider();
		} catch (IllegalArgumentException e){
			TrustManagerException.illegalArgument();
		} catch (NullPointerException e){
			TrustManagerException.nullPointer();
		} catch (KeyStoreException e){
			KeystoreException.invalidKeyStore();
		} catch (UnrecoverableKeyException e){
			KeystoreException.invalidKeyStore();
		}
		return keyManagerFactory;
	}

	/* method to instantiate and return trust keystore manager */
	public static TrustManagerFactory trustManagerTLS(KeyStore trustStore){

		TrustManagerFactory trustManagerFactory = null;

		try{
			trustManagerFactory =
					TrustManagerFactory.getInstance("PKIX", "SunJSSE");
			trustManagerFactory.init(trustStore);
			return trustManagerFactory;
		} catch (NoSuchAlgorithmException e){
			TrustManagerException.invalidAlgorithm();
		} catch (NoSuchProviderException e){
			TrustManagerException.invalidProvider();
		} catch (IllegalArgumentException e){
			TrustManagerException.illegalArgument();
		} catch (NullPointerException e){
			TrustManagerException.nullPointer();
		} catch (KeyStoreException e){
			TrustManagerException.trustStoreFail();
		}
		return trustManagerFactory;
	}

	/* SSL socket function */
	public static SSLContext SSLContextTLS(KeyManagerFactory keyManagerFactory,
			TrustManagerFactory trustManagerFactory, String password){

		/* instantiate SSL Context */
		SSLContext sslContext = null;

		try{
			/* x509keymanager checks for available key manager factories and instantiates */
			X509KeyManager x509KeyManager = null;
			for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
				if (keyManager instanceof X509KeyManager) {
					x509KeyManager = (X509KeyManager) keyManager;
					break;
				}
			}
			/* if none found */
			if (x509KeyManager == null) {
				throw new NullPointerException();
			}
			/* now do for x509trustmanager */
			//instantiate X509 Trust Manager
		    X509TrustManager x509TrustManager = null;
			for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) { 
				//check for available trust manager factories for each unique trust manager
				if (trustManager instanceof X509TrustManager) {
					//check for a unique instance of X509 trust manager in the trust manager
					x509TrustManager = (X509TrustManager) trustManager; 
					//set X509 trust manager
					break;
				}
			}
			//no X509 Trust Managers found
			if (x509TrustManager == null) {
				throw new NullPointerException();
			}
			
			/* instantiate instance of TLS */
			sslContext = SSLContext.getInstance("TLS");
			/* initialize and return  the SSL Context with X509 key and trust managers */
			sslContext.init(new X509KeyManager[]{x509KeyManager}, new X509TrustManager[]{x509TrustManager}, null);
			return sslContext;
		} catch (NoSuchAlgorithmException e){
			TrustManagerException.invalidAlgorithm();
		} catch (NullPointerException e){
			TrustManagerException.invalidProtocol();
		} catch (KeyManagementException e){
			TrustManagerException.invalidManager();
		} catch(IllegalStateException e){
			TrustManagerException.illegalState();
		}
		return sslContext;
	}
}
