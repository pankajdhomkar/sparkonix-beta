package com.sparkonix.utils;

import java.security.SecureRandom;
/*
 *	Class of generate a OTP using a string pool and pick randomly and create 6 length otp 
 */
public class Otp {
	private static final Integer OTP_LENGTH = 6;

	public static String generate(){
		String pool = "0123456789abcdefghijklmnopqrstuvwxyz";
		SecureRandom rand = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < OTP_LENGTH; i++){
			//Here we append a character in OTP using a pool
			sb.append(pool.charAt(rand.nextInt(pool.length())));
		}
		
		return sb.toString();
		
	}
}
