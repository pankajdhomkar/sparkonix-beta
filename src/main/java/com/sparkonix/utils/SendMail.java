package com.sparkonix.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.gson.JsonObject;
import com.sparkonix.ApplicationContext;

public class SendMail implements Runnable{
	
	private String sendTo;
	private String subject;
	private String body;
	
	public SendMail(JsonObject jsonObject) {
		this.sendTo = jsonObject.get("sendTo").getAsString();
		this.subject = jsonObject.get("subject").getAsString();
		this.body = jsonObject.get("body").getAsString();
	}

	@Override
	public void run() {
		final String username = ApplicationContext.getInstance().getConfig().getMailUsername();
		final String password = ApplicationContext.getInstance().getConfig().getMailPassword();
		final String host = ApplicationContext.getInstance().getConfig().getMailHost();
		final String fromEmail = ApplicationContext.getInstance().getConfig().getMailSetFrom();
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
			message.setSubject(subject);
			// message.setText(body);
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	

}
