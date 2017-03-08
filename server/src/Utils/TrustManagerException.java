package Utils;

public class TrustManagerException
{
    //invalid algorithm: issue with key/trust manager algorithm specified
    public static void invalidAlgorithm(){ //since directly specified, this should not ever occur.
        System.out.println("Unknown trust manager algorithm issue.");
        System.exit(-1);
    }
    //invalid provider: issue with key/trust manager provider specified
    public static void invalidProvider(){ //since directly specified, this should not ever occur.
        System.out.println("Unknown trust manager provider issue.");
        System.exit(-1);
    }
    //illegal argument: incorrect number of arguments for algorithm/provider
    public static void illegalArgument(){ //since directly specified, this should not ever occur.
        System.out.println("Provider name null/empty.");
        System.exit(-1);
    }
    //null pointer: null pointer found
    public static void nullPointer(){//since directly specified, this should not ever occur.
        System.out.println("Algorithm is null.");
        System.exit(-1);
    }
    //trustStoreFailed: trust initialization failed
    public static void trustStoreFail(){
        System.out.println("Trust store init failed.");
        System.exit(1);
    }
    //illegalState: issue with factory
    public static void illegalState(){
        System.out.println("Factory not initialized.");
        System.exit(1);
    }
    //invalidProtocol: issue with protocol specified
    public static void invalidProtocol(){ //since directly specified, this should not ever occur.
        System.out.println("Protocol is null");
        System.exit(-1);
    }
    //invalidManager: incorrect manager used
    public static void invalidManager(){
        System.out.println("Failed context initialization.");
        System.exit(1);
    }
}
