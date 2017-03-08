/**
 * group 6
 *
 */

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Scanner;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.*;
import javax.net.ssl.SSLContext;
import java.net.Socket;
import java.nio.charset.Charset;
import java.io.*;
import Utils.*;

public class FTPClient{

	public static void main(String[] args){

		/* Check for the number of arguments passed */
		if(args.length != 2)
			IllegalArgumentsException.invalidNumberofClientArguments(args.length);

		try{
			/* parse input and error check */
			String serverIP = args[0];
			int clientPort = Integer.parseInt(args[1]);

			/* check for evil length of ip and port */
			if (args[0].length() != 15) {
				System.out.println("error: usage <server ip> <port number>");
				System.out.println("please input valid length IPv4 server address <[0-255].[0-255].[0-255].[0-255]>");
				System.out.println("ip address must be entered like this: 127.000.000.001");
				System.exit(-1);
			}

			if (args[1].length() > 5) {
				System.out.println("error: usage <server ip> <port number>");
				System.out.println("please input valid length port number > 0 and < 65535");
				System.exit(-1);
			}

			/* check for valid ip address */
			if (serverIP.matches("^.[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}") != true){
				System.out.println("error: usage <server ip> <port number>");
				System.out.println("please input valid IPv4 server address <[0-255].[0-255].[0-255].[0-255]>");
				System.exit(-1);
			}

			if(clientPort < 0 || clientPort > 65535) {
				System.out.println("error: usage FTPClient <server ip> <port number>");
				System.out.println("please input valid port > 0 and < 65535");
				IllegalArgumentsException.invalidPortNumber();
			}

			/* set keystore variables */
			String keyStoreType = "JKS";
			String keyStorePath = "client.keystore";
			String keyStorePassword = "client";
			String alias = "client";
			String trustalias = "server";
			String trustStorePath = "clienttrust.keystore";
			String trustStorePassword = "client";

			/* instantiate and load client.keystore */
			KeyStore keyStore = FTPKeyStore.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			KeyManagerFactory keyManagerFactory = FTPKeyStore.keyManagerTLS(keyStore, keyStorePassword);

			/* instantiate and load clienttrust.keystore */
			KeyStore trustStore = FTPKeyStore.keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
			TrustManagerFactory trustManagerFactory = FTPKeyStore.trustManagerTLS(trustStore);

			/* at this point the client should have all keys loaded from the keystore */

			/* connect to server with tls */
			SSLContext sslContext = FTPKeyStore.SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(serverIP, clientPort);

			sslSocket.setEnabledProtocols(new String[]{"TLSv1"});

			/* HANDSHAKE */

			sslSocket.startHandshake();

			/* create the streams for the sockets  */
			OutputStream out = sslSocket.getOutputStream();
			InputStream in = sslSocket.getInputStream();
			ObjectOutputStream objOutput = new ObjectOutputStream(out);
			ObjectInputStream objInput = new ObjectInputStream(in);
			BufferedOutputStream outBuf = new BufferedOutputStream(out);

			/* Accept user command to transfer or receive file from terminal reader */
			Scanner reader = new Scanner(System.in);

			while (hostAvailabilityCheck(serverIP, clientPort))
			{
				/* loop infinitely until server quits or user stops */
				while (true)
				{
					/* Initialize input arguments */
					String command = null;
					String filename = null;
					String mode = null;
					String password = null;
					System.out.print("> ");
					String s = reader.nextLine();
					String argumentArray[] = s.split(" ");
					/* Check if commands passed are valid */
					if(!(argumentArray[0].toString().equalsIgnoreCase("get")) &&
					   !(argumentArray[0].toString().equalsIgnoreCase("put")) &&
					   !(argumentArray[0].toString().equalsIgnoreCase("stop"))) {
						System.out.println("error: command  must be put or get or stop");
						break;
					}
					else{
						command = argumentArray[0].toString();
					}

					/* Check for a minimum of three arguments for put and get */
					if((argumentArray[0].toString().equalsIgnoreCase("get")) ||
					   (argumentArray[0].toString().equalsIgnoreCase("put"))){

						if(argumentArray.length < 3) {
							System.out.println("error: usage <put> and <get> require at least 3 arguments");
							break;
						}

					}
					//if stop command, exit
					else{
						MessageContent messageToServer = new MessageContent();

						messageToServer.setMessageAttribute("stop");

						objOutput.writeObject(messageToServer);

						reader.close();
						out.close();
						sslSocket.close();
						System.out.println("Exiting..."); // shouldnt we be closing socket etc...

						System.exit(0);
					}
					/* check to make sure user enters an invalid mode */
					if (!(argumentArray[2].toString().equalsIgnoreCase("E")) && !(argumentArray[2].toString().equalsIgnoreCase("N"))) {
						System.out.println("error: mode must be E or N");
						break;
					}
					//Check if password is provided for encrypt mode
					if((argumentArray[2].toString().equalsIgnoreCase("E")) && (argumentArray.length != 4) ) {
						System.out.println("error: E mode requires a password");
						break;
					} else if((argumentArray[2].toString().equalsIgnoreCase("E")) && (argumentArray.length == 4)) {

						mode = argumentArray[2].toString();
						password = argumentArray[3].toString();

						//Check if password exceeds 8 bytes
						if(password.getBytes(Charset.forName("UTF-8")).length != 8) {
							System.out.println("password must be 8 bytes");
							break;
						}

					} else {
						mode = argumentArray[2].toString();
					}
					/* check if wrong number of arguments are provided in no encrypt mode */
					if((argumentArray[2].toString().equalsIgnoreCase("N")) && (argumentArray.length != 3 )) {
						System.out.println("error: usage <put> and <get> in mode N require exactly 3 arguments");
						break;
					} else {
						mode = argumentArray[2].toString();
					}

					/* parse filename */
					filename = argumentArray[1].toString();
					File inputFile = new File(filename);
					switch(command){
					case "get":
					{
						// initialize a new message that sends to server
						MessageContent getmcWrapper = new MessageContent();
						// set the request file name
						filename = argumentArray[1].toString();
						getmcWrapper.setFileName(filename);
						// set message attribute
						getmcWrapper.setMessageAttribute("get");
						// send message
						objOutput.writeObject(getmcWrapper);

						// client sent a message to server to request a file
						MessageContent messageFromServer = (MessageContent)objInput.readObject();
						// server receive a message from client of requesting file
						MessageContent message = FTPFileTransfer.getClient(mode, password, messageFromServer);
						// get the message status
						String messageStatus = message.getMessageStatus();
						System.out.println(messageStatus);
						break;
					}
					case "put":
					{
						//Get current working directory
						String workingDir = System.getProperty("user.dir");
						//Override working directory to the absolute path if provided
						if(inputFile.isAbsolute())
							workingDir = inputFile.getAbsolutePath();

						//Check if input is directory
						if(inputFile.isDirectory()) {
							System.out.println("error: file inputted is a directory");
							break;
						} else {
							//Check if the file is an absolute or relative path
							if(!inputFile.isAbsolute()){
								//Check if the file exists
								if(!inputFile.exists()) {
									System.out.println("error: file error");
									break;
								}
							}
						}
						MessageContent mcWrapper = FTPFileTransfer.putClient(filename, mode, password);
						mcWrapper.setMessageAttribute("put");
						objOutput.writeObject(mcWrapper);
						MessageContent messageFromServer = (MessageContent)objInput.readObject();
						String messageStatus = messageFromServer.getMessageStatus();
						System.out.println(messageStatus);
						break;
					}
					default:
					{
						System.out.println("Invalid command");
						break;
					}
					}
				}
			}
			/* close buffers */

			reader.close();
			//in.close();
			out.close();
			/* close socket */
			sslSocket.close();
		}catch (NumberFormatException exception) {
			IllegalArgumentsException.invalidIntegerArgument();
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}

/**
 * This method checks if the host is accessible
 * @param serverIP - The IPAddress
 * @param serverPort - Port number
 * @return boolean
 */
	private static boolean hostAvailabilityCheck(String serverIP, int serverPort){
		try {
			Socket s = new Socket(serverIP, serverPort);
			return true;
		} catch (IOException e){
			System.out.println("Server closed.");
		}
		return false;
	}
}
