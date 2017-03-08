package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPv4AddressValidator {
	private Pattern ipv4Pattern;
    private Matcher ipv4PatternMatcher;

    private static final String ipv4AddressPattern =
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."  +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."  +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public IPv4AddressValidator(){
    	ipv4Pattern = Pattern.compile(ipv4AddressPattern);
    }

   public boolean validate(final String ip){
	   ipv4PatternMatcher = ipv4Pattern.matcher(ip);
	   return ipv4PatternMatcher.matches();
    }
}
