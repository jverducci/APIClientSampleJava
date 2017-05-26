package com.tradeshift.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.tradeshift.filter.HeadersFilter;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import java.util.Map;
import java.util.UUID;


/**
 * Base class for an OAuth 1 rest client that signs requests
 * using the following fields:
 *
 * - consumer key
 * - consumer secret
 * - token
 * - token secret
 *
 * For communicating with other endpoints, this class should
 * be extended in a similar fashion as {@link DocumentsClient}.
 */
public abstract class OAuth1Client {
    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String VERSION = "1.0";
    private static final String CONSUMER_KEY = "OwnAccount";
    private static final String CONSUMER_SECRET = "OwnAccount";
    private static final ConsumerCredentials CONSUMER_CREDENTIALS = new ConsumerCredentials(CONSUMER_KEY, CONSUMER_SECRET);

    private static final int CHUNK_ENCODING_SIZE_VAL = 64 * 1024;
    private static final int CONNECT_TIMEOUT_VAL = 10000;
    private static final int READ_TIMEOUT_VAL = 60000;
    private static final boolean FOLLOW_REDIRECTS_VAL = false;

    protected final WebTarget clientTarget;

    private final AccessToken accessToken;
    private final UUID tenantId;
    private final String userAgent;

    protected OAuth1Client(final String targetUrl,
                           final AccessToken accessToken,
                           final UUID tenantId,
                           final String userAgent) {
        checkNotNull(tenantId);
        checkNotNull(accessToken);
        checkArgument(StringUtils.isNotBlank(accessToken.getToken()));
        checkArgument(StringUtils.isNotBlank(accessToken.getAccessTokenSecret()));
        checkArgument(StringUtils.isNotBlank(targetUrl));
        checkArgument(StringUtils.isNotBlank(userAgent));

        this.accessToken = accessToken;
        this.tenantId = tenantId;
        this.userAgent = userAgent;
        this.clientTarget = createClientTarget(targetUrl);
    }

    private WebTarget createClientTarget(final String targetUrl) {
        ClientConfig config = new ClientConfig();

        config.property(ClientProperties.CHUNKED_ENCODING_SIZE, CHUNK_ENCODING_SIZE_VAL);
        config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT_VAL);
        config.property(ClientProperties.FOLLOW_REDIRECTS, FOLLOW_REDIRECTS_VAL);
        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT_VAL);

        config.register(JacksonJaxbJsonProvider.class);
        config.register(initializeOauth1FilterFeature());
        config.register(new HeadersFilter(tenantId, userAgent));

        return ClientBuilder.newClient(config).target(targetUrl);
    }

    private Feature initializeOauth1FilterFeature() {
        return OAuth1ClientSupport
                .builder(CONSUMER_CREDENTIALS)
                .signatureMethod(SIGNATURE_METHOD)
                .version(VERSION)
                .feature()
                .accessToken(accessToken)
                .build();
    }

    /**
     * Adds query parameters to a request URI, if any present.
     *
     * @param target - The target containing the URL to which the params will be applied
     * @param queryParams - The query params to send in the API request
     * @return The new web target with all query params set
     */
    protected WebTarget addQueryParameters(WebTarget target, Map<String, String> queryParams) {
        checkNotNull(target);

        if (queryParams != null) {
            for (Map.Entry entry : queryParams.entrySet()) {
                if (entry.getKey() != null && StringUtils.isNotBlank(entry.getKey().toString())) {
                    target = target.queryParam(entry.getKey().toString(), entry.getValue());
                }
            }
        }

        return target;
    }
}
