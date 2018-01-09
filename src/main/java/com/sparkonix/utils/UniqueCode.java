package com.sparkonix.utils;

import java.security.SecureRandom;

public class UniqueCode {
	private static final Integer UNIQUE_CODE_LENGTH = 12;
	
	public static String generateUniqueCode(){
		String pool = "0123456789abcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < UNIQUE_CODE_LENGTH; i++){
			sb.append(pool.charAt(rnd.nextInt(pool.length())));
			if(i == 3){
				sb.append("-");//xxxx-xxxx-xxxx
			}
			if(i == 7){
				sb.append("-");
			}
		}
		return sb.toString();
	}
}
