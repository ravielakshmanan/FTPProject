package Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
	private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
    
    public static String signFileWithSHA256Hash(File file) {
    	byte[] hashedBytes = null;
    	
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
     
            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;
     
            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }
     
            hashedBytes = digest.digest();
     
        } catch (NoSuchAlgorithmException | IOException ex) {
        	System.out.println("Could not generate hash from file" + ex.toString());
        }
        
        return convertByteArrayToHexString(hashedBytes);
    }
    
    public static String hashFileWithSHA256(byte[] input) {
    	byte[] hashedBytes = null;
    	
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
    	 
    	try{
    		MessageDigest digest = MessageDigest.getInstance("SHA-256");
     
            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;
     
            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }
     
            hashedBytes = digest.digest();
     
        } catch (NoSuchAlgorithmException | IOException ex) {
        	System.out.println("Could not generate hash from file" + ex.toString());
        }
        
        return convertByteArrayToHexString(hashedBytes);
    }
}
