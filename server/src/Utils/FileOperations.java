package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperations {
	
	//Method that writes a byte array contents to a File file
	public static File writeFile(File outputFile, byte[] outputFileStream ){
    	FileOutputStream outputStream;
    	
    	try {
    		outputStream = new FileOutputStream(outputFile);
    		outputStream.write(outputFileStream);
    		outputStream.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return outputFile;
    }
	
	//Method that reads a File object to return a byte stream
	public static byte[] readFile(File file){
    	FileInputStream inputStream;
    	byte[] inputBytes = new byte[(int) file.length()];
    	
    	try {
			inputStream = new FileInputStream(file);
	        inputStream.read(inputBytes);
	        inputStream.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputBytes;
    }
}
