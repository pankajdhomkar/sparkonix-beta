
package com.sparkonix.entity.dto;

/**
 * @author Pankaj Dhomkar
 *
 */
public class PhoneOperatorDTO {
    private String phoneNumber;
    private String otp;
    private String fcmToken;
    private String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
	return otp;
    }

    public void setOtp(String otp) {
	this.otp = otp;
    }

    public String getFcmToken() {
	return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
	this.fcmToken = fcmToken;
    }
}
