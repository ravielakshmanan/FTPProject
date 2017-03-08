package Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DecryptUtil {
	
	//Method that performs the AES decryption in AES/CBC mode
	public static byte[] decrypt(String key, String initVector, byte[] encryptedFile) {
		try {
            
        	IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            //Get an instance of the cipher passing in the mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            //Generate the plain text
            byte[] originalPlainText = cipher.doFinal(encryptedFile);
            
            return originalPlainText;
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
