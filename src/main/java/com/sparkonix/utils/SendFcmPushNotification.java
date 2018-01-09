package com.sparkonix.utils;

import java.net.URI;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.sparkonix.ApplicationContext;

public class SendFcmPushNotification implements Runnable{
	
	private Gson gson = new Gson();
	private FcmMessage fcmMessage;
	private final Logger log = Logger.getLogger(SendFcmPushNotification.class.getName());

	public SendFcmPushNotification(FcmMessage fcmMessage) {
		this.fcmMessage = fcmMessage;
	}

	@Override
	public void run() {
		log.info("Sending FCM Message");
		
		String fcmSeverKey = ApplicationContext.getInstance().getConfig().getFcmServerKey();
		
		// Construct data
		try {
			URI uri = new URI("https://fcm.googleapis.com/fcm/send");
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(uri);
			
			httpPost.setEntity(new StringEntity(gson.toJson(fcmMessage), ContentType.create("application/json")));
			httpPost.setHeader("Authorization", "key=" + fcmSeverKey);

			Integer errorCode;
			CloseableHttpResponse response = null;
			
			try {
				response = httpClient.execute(httpPost);
				errorCode = response.getStatusLine().getStatusCode();
			} finally {
				if (response != null) {
					response.close();
				}
			}

			if (errorCode != 200) {
				log.info("Failed to send FCM Message to :: " + fcmMessage.getTo());
				log.info("Failed to send FCM Message Reason :: " + response.getStatusLine().getReasonPhrase());
			} else {
				log.info("FCM message sent");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("FCM message sending failed. Reason: " + e.getMessage());
		}
		
	}

}
