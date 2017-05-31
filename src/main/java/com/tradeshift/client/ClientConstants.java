package com.tradeshift.client;

import org.glassfish.jersey.client.oauth1.ConsumerCredentials;

public final class ClientConstants {
    // Client Connection Configurations
    public static final boolean FOLLOW_REDIRECTS_VAL = false;
    public static final int CHUNK_ENCODING_SIZE_VAL = 64 * 1024;
    public static final int CONNECT_TIMEOUT_VAL = 10000;
    public static final int DEFAULT_CONNECTIONS_PER_ROUTE = 20;
    public static final int MAX_CONNECTIONS = 100;
    public static final int MAX_CONNECTIONS_PER_HOST = 50;
    public static final int READ_TIMEOUT_VAL = 60000;

    // Consumer Credentials for Own Account
    public static final String CONSUMER_KEY = "OwnAccount";
    public static final String CONSUMER_SECRET = "OwnAccount";
    public static final ConsumerCredentials OWN_ACCOUNT_CREDENTIALS = new ConsumerCredentials(CONSUMER_KEY,
            CONSUMER_SECRET);

    // OAuth1 Signature Constants
    public static final String SIGNATURE_METHOD = "HMAC-SHA1";
    public static final String VERSION = "1.0";

    // URI Constants
    public static final String PRODUCTION_HOSTNAME = "https://api.tradeshift.com";
    public static final String SANDBOX_HOSTNAME = "https://api-sandbox.tradeshift.com";
    public static final String BASE_URI = "/tradeshift/rest/external";

    private ClientConstants() {
    }
}
