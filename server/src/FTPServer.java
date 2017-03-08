/**
 * group6
 *
 */

import java.security.KeyStore;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.*;
import java.io.*;
import Utils.*;


public class FTPServer {

	public static void main(String[] args) {

		/* Check for the number of arguments passed */
		if(args.length != 1)
			IllegalArgumentsException.invalidNumberofServerArguments(args.length);

		try {

			if (args[0].length() > 5) {
				System.out.println("error: usage FTPServer <port number>");
				System.out.println("please input valid length port number > 0 and < 65535");
				System.exit(-1);
			}

			/* parse input and error check */
			int portnum = Integer.parseInt(args[0]);
			if(portnum < 0 || portnum > 65535)
				IllegalArgumentsException.invalidPortNumber();

			if(portnum < 0 || portnum > 65535) {
				System.out.println("error: usage FTPServer  <port number>");
				System.out.println("please input valid port > 0 and < 65535");
				IllegalArgumentsException.invalidPortNumber();
			}

			/* set key store variables */
			String keyStoreType = "JKS";
			String keyStorePath = "server.keystore";
			String keyStorePassword = "server";
			String alias = "server";
			String trustalias = "client";
			String trustStorePath = "servertrust.keystore";
			String trustStorePassword = "server";

			/* instaniate and load keystore */
			KeyStore keyStore = FTPKeyStore.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			KeyManagerFactory keyManagerFactory = FTPKeyStore.keyManagerTLS(keyStore, keyStorePassword);

			/* instantiate and load servertrust.keystore */
			KeyStore trustStore = FTPKeyStore.keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
			TrustManagerFactory trustManagerFactory = FTPKeyStore.trustManagerTLS(trustStore);

			/* at this point the server should have all keys loaded from the kerystore */

			/* open socket and listen for a client */
			SSLContext sslContext = FTPKeyStore.SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
			SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
			SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(portnum);

			/* require mutual authentication */
			serverSocket.setNeedClientAuth(true);
			/* set TLS protocol */
			serverSocket.setEnabledProtocols(new String[]{"TLSv1"});
			/* accept socket connection */
			SSLSocket sslSocket = (SSLSocket) serverSocket.accept();

			sslSocket.getSession(); /* handshake */

			/* I/O */
			OutputStream out = sslSocket.getOutputStream();
			InputStream in = sslSocket.getInputStream();
			ObjectOutputStream objOutput = new ObjectOutputStream(out);
			ObjectInputStream objInput = new ObjectInputStream(in);

			while (!sslSocket.isOutputShutdown())
			{

				MessageContent messageFromClient = (MessageContent)objInput.readObject();

				String messageAttr = messageFromClient.getMessageAttribute();

				/* communicate with client */

				if (messageAttr.equals("put")){

					MessageContent messageToClient = FTPFileTransfer.putServer(messageFromClient);
					objOutput.writeObject(messageToClient);
				}
				else if (messageAttr.equals("get")){

					MessageContent messageFromServer = FTPFileTransfer.getServer(messageFromClient);
					objOutput.writeObject(messageFromServer);
				}
				else if(messageAttr.equals("stop")){
					sslSocket.close();
					System.exit(0);
				}

			}

			sslSocket.close(); //close socket

		}catch (NumberFormatException exception) {
			IllegalArgumentsException.invalidIntegerArgument();
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
