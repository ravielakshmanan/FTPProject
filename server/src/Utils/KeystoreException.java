package Utils;

public class KeystoreException
{
    //invalidKeyStore: prints error message for invalid keyStore file.
    public static void invalidKeyStore(){
        System.out.println("Keystore not found.");
        System.exit(1);
    }
    //invalid KeyStoreFile: key store not found at this path
    public static void invalidKeyStoreFile(String keyStorePath){
        System.out.println("Given keyStorePath: " + keyStorePath);
        System.out.println("Please use a valid path for the keystore.");
        System.exit(1);
    }
    //invalidKeyStoreType: prints error message for invalid keyStore type
    public static void invalidKeyStoreType(String keyStoreType){
        System.out.println("Given keyStoreType: " + keyStoreType);
        System.out.println("keyStoreType is either \"JKS\" or \"PKCS12\"");
        System.exit(1);
    }
    //invalidKeyStoreData: issue with the keystore at the given path
    public static void invalidKeyStoreData(String keyStorePath){
        System.out.println("Problem with keystore found at " + keyStorePath);
        System.exit(1);
    }
    //invalidAlgorithm: issue with keystore algorithm
    public static void invalidAlgorithm(String keyStorePath){
        System.out.println("The algorithm used to check the integrity of the keystore found at " + keyStorePath + " cannot be found.");
        System.exit(1);
    }
    //invalidCertificate: issue with cert found at the keystore
    public static void invalidCertificate(String keyStorePath){
        System.out.println("One or more of the certificates in the keystore found at "
                           + keyStorePath + "could not be loaded.");
        System.exit(1);
    }
}
