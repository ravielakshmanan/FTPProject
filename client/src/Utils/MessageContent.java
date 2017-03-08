package Utils;

import java.io.Serializable;

public class MessageContent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String key;
	private byte[] initVector;
	private byte[] encryptedFile;
	private byte[] hashValue;
	private byte[] encryptedHashValue;
	private byte[] encryptedPassword;
	private String messageStatus;
	private String fileName;
	private boolean fileStatus;
	private String messageAttribute; // either get or put
	
	public void setMessageAttribute(String m_attr){
		this.messageAttribute = m_attr;
	}
	public String getMessageAttribute(){
		return this.messageAttribute;
	}
	
	public boolean isFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(boolean fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getKey() {
		return key;
	}
	
	//Method to set the key
	public void setKey(String key) {
		this.key = key;
	}
	
	//Method to get the Initialization Vector
	public byte[] getInitVector() {
		return initVector;
	}
	
	//Method to set the Initialization Vector
	public void setInitVector(byte[] initVector) {
		this.initVector = initVector;
	}
	
	//Method to get the Encrypted File
	public byte[] getEncryptedFile() {
		return encryptedFile;
	}
	
	//Method to SET the Encrypted File
	public void setEncryptedFile(byte[] encryptedFile) {
		this.encryptedFile = encryptedFile;
	}
	
	//Method to get the Hash Value
	public byte[] getHashValue() {
		return hashValue;
	}
	
	//Method to set the Hash Value
	public void setHashValue(byte[] hashValue) {
		this.hashValue = hashValue;
	}
	
	//Method to get the Encrypted Hash Value
	public byte[] getEncryptedHashValue() {
		return encryptedHashValue;
	}
	
	//Method to set the Encrypted Hash Value
	public void setEncryptedHashValue(byte[] encryptedHashValue) {
		this.encryptedHashValue = encryptedHashValue;
	}
	
	//Method to get the Encrypted Password
	public byte[] getEncryptedPassword() {
		return encryptedPassword;
	}
	
	//Method to set the Encrypted Password
	public void setEncryptedPassword(byte[] encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	//Method to get the Message Status
	public String getMessageStatus() {
		return messageStatus;
	}
	
	//Method to set the Message Status
	public void setMessageStatus(String message) {
		this.messageStatus = message;
	}
	
	//Method to get the file name
	public String getFileName() {
		return fileName;
	}

	//Method to set the file name
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
