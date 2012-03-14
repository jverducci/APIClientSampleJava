package com.tradeshift.api.client;

import com.sun.jersey.api.client.*;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

public class Sample {
	
	// You obtain these parameters by activating the "API Access to Own Account" app.
	// The app is activated from: https://sandbox.tradeshift.com/apps/view/Tradeshift.APIAccessToOwnAccount
	
	public static final String HOSTNAME = "https://api-sandbox.tradeshift.com";
	public static final String CONSUMER_KEY = "OwnAccount"; 
	public static final String CONSUMER_SECRET = "OwnAccount";
	public static final String TOKEN = "";
	public static final String TOKEN_SECRET = "";
	public static final String TENANT_ID = "";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = Client.create();
		OAuthParameters params = new OAuthParameters().signatureMethod("HMAC-SHA1").consumerKey(CONSUMER_KEY).token(TOKEN).version();
		OAuthSecrets secrets = new OAuthSecrets().consumerSecret(CONSUMER_SECRET).tokenSecret(TOKEN_SECRET);
		OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), params, secrets);
		WebResource res = client.resource(HOSTNAME + "/tradeshift/rest/external/network/connections");
		res.addFilter(filter);
		String networkResult = res.header("X-Tradeshift-TenantId", TENANT_ID)
				.header("User-Agent", "TradeshiftJerseyTest/0.1")
				.get(String.class);
		System.out.println(networkResult);
	}

}


