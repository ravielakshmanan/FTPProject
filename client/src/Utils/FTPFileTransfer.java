package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;



public class FTPFileTransfer{
	
	
	public static MessageContent putClient(String inputFileName,
			String encryptedStatus, 
			String password){
		//The client will generate the SHA256 hash of the plaintext file.

		try {
		File inputFile = new File(inputFileName);
		String hash = HashUtil.signFileWithSHA256Hash(inputFile);

		MessageContent mcWrapper = new MessageContent();
		String initVector = "randomInitVector";
		
		mcWrapper.setFileName(inputFileName);
		if (encryptedStatus.equals("E")){

			byte[] AES_KEY = password.getBytes(Charset.forName("UTF-8"));
			SecureRandom random = new SecureRandom();
			random.nextBytes(AES_KEY);
			
			String key = password + "" + password;
			
			byte[] encryptedFile = EncryptUtil.encrypt(key, initVector, FileOperations.readFile(inputFile));// use password to generate IV

			
			OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("encryptedFileTest"));
  			InputStream inputStream = new ByteArrayInputStream(encryptedFile);
  			int token = -1;

			 while((token = inputStream.read()) != -1)
			 {
			   bufferedOutputStream.write(token);
			 }
			 bufferedOutputStream.flush();
			 bufferedOutputStream.close();
			 inputStream.close();




			byte[] initVectorByte = initVector.getBytes(Charset.forName("UTF-8"));
			
			byte[] hashCipherText = new byte[encryptedFile.length+initVectorByte.length];
			
			System.arraycopy(initVectorByte, 0, hashCipherText, 0, initVectorByte.length);
			System.arraycopy(encryptedFile, 0, hashCipherText, initVectorByte.length, encryptedFile.length);
			
			mcWrapper.setEncryptedFile(hashCipherText);
			
		}
		//if not in encrypt mode
		else{
			Path path = Paths.get(inputFileName);
			byte[] file = Files.readAllBytes(path);
			
			mcWrapper.setEncryptedFile(file);
		}

		/*
		The client sends the file 
		(with the IV prepended to it if the file is encrypted) 
		and the hash to the server.
		 */
		mcWrapper.setHashValue(hash.getBytes(Charset.forName("UTF-8")));
		return mcWrapper;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static MessageContent putServer(MessageContent messageFromClient){
		
		try {
		
		/*
		The server will write the file 
		(still with the IV preprended if the file is encrypted) 
		and its corresponding hash to the directory 
		from which the server was executed. 
		These are saved as two separate files (the file and the hash). 
		The hash will be saved in a file with the same name as the file 
		being transmitted with a ".sha256" extension added to the name. 
		For example, if the file is abc.txt the hash file is named abc.txt.sha256
		*/
			String fileName = messageFromClient.getFileName();
			String shaName = fileName + ".sha256";
			
			byte[] fileFromClient = messageFromClient.getEncryptedFile();
			byte[] shaFileFromClient = messageFromClient.getHashValue();
			
			FileOutputStream os = new FileOutputStream(fileName);
			os.write(fileFromClient);
			os.close();
			
			FileOutputStream hashOS = new FileOutputStream(shaName);
			hashOS.write(shaFileFromClient);
			hashOS.close();
			
			MessageContent messageToClient = new MessageContent();
            messageToClient.setMessageStatus("Transfer of file " + fileName + " complete!");
            return messageToClient;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	public static MessageContent getClient(String encryptedStatus, String password, MessageContent messageFromServer){
	
		MessageContent messageToClient = new MessageContent();

		String initVector_default = "randomInitVector";

		byte[] encryptedFile = null;
		

		try {
			if( (encryptedStatus.equals("N")) && (!messageFromServer.isFileStatus())){
				return messageFromServer;
			}

			String encryptedFileHash = new String(messageFromServer.getHashValue());
			String fileName = messageFromServer.getFileName() + ".out";
			byte[] decryptedFileString = null;
			
			String key = password + "" + password;

			//The client will decrypt the file if "E" was specified
			if(encryptedStatus.equals("E")){
				//DecryptUtil.decrypt
				//Read the first 16 bytes to get the IV
				encryptedFile = messageFromServer.getEncryptedFile();
				String initVector = new String(Arrays.copyOf(encryptedFile, 16));
				if (initVector.equals(initVector_default)){
					byte[] newEncryptedFile = Arrays.copyOfRange(encryptedFile, 16, encryptedFile.length);

					decryptedFileString = DecryptUtil.decrypt(key, initVector, newEncryptedFile);
				}
				else{
					messageToClient.setMessageStatus("Error: decryption of  " + messageFromServer.getFileName() + " failed, was file encrypted?");
					return messageToClient;
				}
			}

			if (decryptedFileString == null){
				decryptedFileString = encryptedFile; // no encryption
			}
			
			//The client will compute the sha256 hash of the plaintext file
			String sha256_text = HashUtil.hashFileWithSHA256(decryptedFileString);


			//The client will compare the hash it computed to the hash that was received.
			if(sha256_text.equals(encryptedFileHash)){
				/*
				If the hash matches, 
				the client will write the file (not the hash) 
				to the directory from which the client was executed. 
				In the case of a text file, 
				the user will be able to read the file in another window 
				or after the client stops to manually verify 
				it was retrieved and decrypted. 
				(The programs will be tested with ASCII 
				and binary files when being graded.) 
				 */
				System.out.println("the hash is the same");
				FileOutputStream os = new FileOutputStream(fileName);
				os.write(decryptedFileString);
				os.close();
				
				messageToClient.setMessageStatus("Retrieval of file " + fileName + " complete!");
			}
			else{
				/*
				If the hash did not match, 
				the client will display a message to the user 
				before displaying the prompt again and will not write the file to disk.
				 */
				messageToClient.setMessageStatus("Error: Computed hash of file " + fileName + " does not match retrieved hash!");
			}
			
			return messageToClient;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static MessageContent getServer(MessageContent messageFromClient){
	
	/*
	The server will respond by sending the file and its corresponding hash. 
	The server will always look for the hash with the same name 
	and .sha256 extension in the directory as the file.
	 */
	String requestedFileName = messageFromClient.getFileName();
	String requestedHashFileName = messageFromClient.getFileName()+".sha256";

	//File error handling pending
	File fileSentToClient = new File(requestedFileName);
	File SHAFileSentToClient = new File(requestedHashFileName);
	
	MessageContent messageToClient = new MessageContent();
	
	String workingDir = System.getProperty("user.dir");

	//Override working directory to the absolute path if provided
	if(fileSentToClient.isAbsolute())
		workingDir = fileSentToClient.getAbsolutePath();

	//Check if input is directory
	if(fileSentToClient.isDirectory()){
		messageToClient.setFileStatus(false);
		messageToClient.setMessageStatus("Error: Invalid file \"" + fileSentToClient + "\". No such file exists in current directory \"" + workingDir + "\".");
	}
	//Check if the file is an absolute or relative path
	else if(!fileSentToClient.isAbsolute()){
		//Check if the file exists
		if(!fileSentToClient.exists()) {
			messageToClient.setFileStatus(false);
			messageToClient.setMessageStatus("Error: Invalid file \"" + fileSentToClient + "\". No such file exists in current directory \"" + workingDir + "\".");
		}
	}
	//Else read the file
	else{
		//read the file to create the byte arrays

		byte[] fileToClient = FileOperations.readFile(fileSentToClient);
		byte[] shaFileToClient = FileOperations.readFile(SHAFileSentToClient);

		messageToClient.setFileName(requestedFileName);
		messageToClient.setEncryptedFile(fileToClient);
		messageToClient.setHashValue(shaFileToClient);
	}
	
	return messageToClient;
	
	}
}
