package Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {
	
	//Method that performs the AES encryption in AES/CBC mode
	public static byte[] encrypt(String key, String initVector, byte[] inputFileStream) {
        try {
        	//Set the initialization vector
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            
            //Set the secret key specification to AES
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            //Initialize a cipher instance in AES?CBC mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            //Generate the cipher text
            byte[] encrypted = cipher.doFinal(inputFileStream);

            return encrypted;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
