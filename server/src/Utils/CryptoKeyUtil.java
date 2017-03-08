package Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoKeyUtil { 
	public static String PRIVATE_KEY_FILE = "";
	public static String PUBLIC_KEY_FILE = "";
	
	//Method that sets the key file paths
	public static void setKeyFiles(String host){
		
		/*Set appropriate key file paths based on whether 
		  the host is a server or a client*/
		
		if(host == "client"){
        	PRIVATE_KEY_FILE = "keys/client/private.key";
        	PUBLIC_KEY_FILE = "keys/client/public.key";
        }else if(host == "server"){
        	PRIVATE_KEY_FILE = "keys/server/private.key";
        	PUBLIC_KEY_FILE = "keys/server/public.key";
        }
	}

	//Method to generate the key private-public key pair
	public static void generateKeyPair(){
		
    	KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
	        final KeyPair key = keyGen.generateKeyPair();
	        
	        //Create private and public key Files
	        File privateKeyFile = new File(PRIVATE_KEY_FILE);
        	File publicKeyFile = new File(PUBLIC_KEY_FILE);
        	
        	if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
	        privateKeyFile.createNewFile();
	        
	        if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
	        publicKeyFile.createNewFile();
	        
	        //Write the generated public key to the file
	        ObjectOutputStream publicKeyOS = new ObjectOutputStream(
	                new FileOutputStream(publicKeyFile));
	        publicKeyOS.writeObject(key.getPublic());
	        publicKeyOS.close();
	        
	      //Write the generated private key to the file
	        ObjectOutputStream privateKeyOS = new ObjectOutputStream(
	                new FileOutputStream(privateKeyFile));
	        privateKeyOS.writeObject(key.getPrivate());
	        privateKeyOS.close();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
    }
	
	public static byte[] encryptHash(String hash, Object key, String algorithm){
		byte[] cipherText = null;
        Cipher cipher;
        
        try{
        	cipher = Cipher.getInstance(algorithm);
        	
        	//Check if the key is of type PrivateKey or PublicKey or String
        	if(key instanceof PrivateKey){
        		PrivateKey encryptionKey = (PrivateKey) key;
        		cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        	}
        	else if(key instanceof PublicKey){
        		PublicKey encryptionKey = (PublicKey) key;
        		cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        	}
        	else if(key instanceof String){
        		//Set the key and initialization vector
        		String fullKey = (String) key;
        		String[] parts = fullKey.split("<<<>>>");
        		
        		String encryptionKey = parts[0];
        		String initVector = parts[1];
        		
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                //Set the secret key specification to AES
                SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        	}
	        //Generate the cipher text
        	cipherText = cipher.doFinal(hash.getBytes());  
        }catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //encrypt the plain text using the public key
        return cipherText;
	}
	
	public static String decryptHash(byte[] text, Object key, String algorithm){
		byte[] decryptedText = null;
        Cipher cipher;
        
        try{
        	cipher = Cipher.getInstance(algorithm);
        	
        	//Check if the key is of type PrivateKey or PublicKey or String
        	if(key instanceof PrivateKey){
        		PrivateKey encryptionKey = (PrivateKey) key;
        		cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
        	}
        	else if(key instanceof PublicKey){
        		PublicKey encryptionKey = (PublicKey) key;
        		cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
        	}
        	else if(key instanceof String){
        		//Set the key and initialization vector
        		String fullKey = (String) key;
        		String[] parts = fullKey.split("<<<>>>");
        		
        		String encryptionKey = parts[0];
        		String initVector = parts[1];
        		
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                //Set the secret key specification to AES
                SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        	}
	        //Generate the cipher text
        	decryptedText = cipher.doFinal(text);  
        }catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //encrypt the plain text using the public key
        return new String(decryptedText);
	}
	
	public static byte[] encryptHashWithRSA(String hash, PrivateKey privateKey, String algorithm){
    	byte[] cipherText = null;
        Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	        cipherText = cipher.doFinal(hash.getBytes());  
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | 
				InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //encrypt the plain text using the public key
		return cipherText;
    }
	
	public static String decryptHashWithRSA(byte[] text, PublicKey publicKey, String algorithm) {
        byte[] decryptedText = null;
        try {
          // get an RSA cipher object and print the provider
          final Cipher cipher = Cipher.getInstance("RSA");

          //decrypt the text using the private key
          cipher.init(Cipher.DECRYPT_MODE, publicKey);
          decryptedText = cipher.doFinal(text);

        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        return new String(decryptedText);
    }
	
	public static byte[] encryptHashWithRSAPub(String hash, PublicKey publicKey, String algorithm){
    	byte[] cipherText = null;
        Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        cipherText = cipher.doFinal(hash.getBytes());  
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | 
				InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //encrypt the plain text using the public key
		return cipherText;
    }
	
	public static String decryptHashWithRSAPvt(byte[] text, PrivateKey privateKey, String algorithm) {
        byte[] dectyptedText = null;
        try {
          // get an RSA cipher object and print the provider
          final Cipher cipher = Cipher.getInstance("RSA");

          //decrypt the text using the private key
          cipher.init(Cipher.DECRYPT_MODE, privateKey);
          dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        return new String(dectyptedText);
    }
	
	
	public static boolean areKeysPresent() {

        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        if (privateKey.exists() && publicKey.exists()) {
        	return true;
        }
        return false;
    }
}
