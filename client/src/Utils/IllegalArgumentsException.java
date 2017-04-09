package Utils;

/**
 * An exception class that handles input argument checks
 * @author ravielakshmanan
 *
 */
public class IllegalArgumentsException {
	
	
    /**
     * Checks for the number of arguments passed during server startup.
     * @param size - size of arguments
     */
    public static void invalidNumberofServerArguments(int size){
        System.out.println("Error: You have entered " + size + " number of arguments instead of 1. Please retry.");
        System.exit(1);
    }
    
    
    /**
     * Check for the number of arguments passed during client startup.
     * @param size
     */
    public static void invalidNumberofClientArguments(int size){
        System.out.println("Error: You have entered " + size + " number of arguments instead of 2. Please retry.");
        System.exit(1);
    }
    
    //If the argument passed is not of type Int
    public static void invalidIntegerArgument(){
        System.out.println("Error: Please enter a valid value for port number.");
        System.exit(1);
    }
    
    //Check if the port number passed in negative
    public static void invalidPortNumber(){
        System.out.println("Error: Port number has to be between 1100 and 65535. Please retry.");
        System.exit(1);
    }
    
   //Check if the IP address is valid
    public static void invalidIPv4Address(){
        System.out.println("Error: The IP address format entered is invalid. Please retry.");
        System.exit(1);
    }
    
    //Check if server name is localhost
    public static void invalidServer(){
        System.out.println("Error: Server cannot be localhost. Please enter a valid server name or IPv4 address.");
        System.exit(1);
    }
    
    //Check for the number of FTP arguments passed from client.
    public static void invalidNumberofFTPArguments(int size){
        System.out.println("Error: Missing parameters, a minimum of a filename and \"N\" or \"E\" is required");
        System.exit(1);
    }
    
    //Check for the number of FTP arguments passed from client.
    public static void invalidNumberofFTPArgumentsInEncryptMode(){
        System.out.println("Error: Missing parameters, \"E\" requires a password");
        System.exit(1);
    }
    
    //Check for the number of FTP arguments passed from client.
    public static void invalidNumberofFTPArgumentsInNoEncryptMode(){
        System.out.println("Error: Invalid format. Please retry - get/put <filename> N.");
        System.exit(1);
    }
    
    //Check if password exceeds 8 bytes in length
    public static void invalidPassword(){
        System.out.println("Error: Password cannot exceed 8 characters. Please retry.");
        System.exit(1);
    }
    
    //Check for invalid FTP commands
    public static void invalidFTPCommand(String command){
        System.out.println("Error: Invalid command \"" + command + "\". Valid options are \"get\", \"put\" and \"stop\"");
        System.exit(1);
    }
    
    //Check for file exists and if it's not a directory
    public static void invalidInputFile(String file, String currentDir){
        System.out.println("Error: Invalid file \"" + file + "\". No such file exists in current directory \"" + currentDir + "\".");
        System.exit(1);
    }
}
