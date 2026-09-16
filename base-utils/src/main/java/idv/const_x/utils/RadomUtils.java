package idv.const_x.utils;

import java.util.Random;

public class RadomUtils {

	
	public static String getRandomNum(int length){
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
        for(int i = 0; i < length;i++) {
        	builder.append(Math.abs(random.nextInt())%10);
        }
        return builder.toString();
	}
	
	public static String getRandomString(int length) { 
	    String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }  
	
	public static int getLimitedRandomNum(int limited){
		Random random = new Random();
		return random.nextInt(limited);
	}
}
