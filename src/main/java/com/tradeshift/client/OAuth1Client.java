package com.tradeshift.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.tradeshift.client.ClientConstants.CHUNK_ENCODING_SIZE_VAL;
import static com.tradeshift.client.ClientConstants.CONNECT_TIMEOUT_VAL;
import static com.tradeshift.client.ClientConstants.DEFAULT_CONNECTIONS_PER_ROUTE;
import static com.tradeshift.client.ClientConstants.FOLLOW_REDIRECTS_VAL;
import static com.tradeshift.client.ClientConstants.MAX_CONNECTIONS;
import static com.tradeshift.client.ClientConstants.MAX_CONNECTIONS_PER_HOST;
import static com.tradeshift.client.ClientConstants.OWN_ACCOUNT_CREDENTIALS;
import static com.tradeshift.client.ClientConstants.READ_TIMEOUT_VAL;
import static com.tradeshift.client.ClientConstants.SIGNATURE_METHOD;
import static com.tradeshift.client.ClientConstants.VERSION;

import com.tradeshift.exception.TradeshiftRestClientException;
import com.tradeshift.filter.HeadersFilter;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for an OAuth 1 rest client that signs requests
 * using the following fields:
 * <ul>
 *   <li>consumer key</li>
 *   <li>consumer secret</li>
 *   <li>token</li>
 *   <li>token secret</li>
 * </ul>
 * <p/>
 * For communicating with other endpoints, this class should
 * be extended in a similar fashion as {@link DocumentsClient}.
 */
public abstract class OAuth1Client {
    private static final Logger LOG = LoggerFactory.getLogger(OAuth1Client.class);

    private final Client client;

    protected OAuth1Client(final String host, final AccessToken accessToken, final UUID tenantId,
            final String userAgent) {
        checkNotNull(tenantId);
        checkNotNull(accessToken);
        checkArgument(StringUtils.isNotBlank(accessToken.getToken()));
        checkArgument(StringUtils.isNotBlank(accessToken.getAccessTokenSecret()));
        checkArgument(StringUtils.isNotBlank(host));
        checkArgument(StringUtils.isNotBlank(userAgent));

        this.client = createClient(host, accessToken, tenantId, userAgent);
    }

    protected Client client() {
        return client;
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

    /**
     * Ensures that the HTTP response status code returned from Tradeshift
     * API calls match what the client expects to see.
     *
     * @param response - The response to validate
     * @param expectedStatus - The expected HTTP status code
     */
    protected void validateResponseCode(final Response response, final Response.Status expectedStatus) {
        checkNotNull(response);
        checkNotNull(expectedStatus);

        if (response.getStatus() != expectedStatus.getStatusCode()) {
            LOG.error("Unexpected response status returned: [{}], payload: {}", response.getStatus(),
                    response.readEntity(String.class));
            throw new TradeshiftRestClientException("Request to Tradeshift API failed.");
        }
    }

    protected abstract void close();

    private Client createClient(final String host, final AccessToken accessToken, final UUID tenantId,
            final String userAgent) {
        ClientConfig config = new ClientConfig();

        config.property(ClientProperties.CHUNKED_ENCODING_SIZE, CHUNK_ENCODING_SIZE_VAL);
        config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT_VAL);
        config.property(ClientProperties.FOLLOW_REDIRECTS, FOLLOW_REDIRECTS_VAL);
        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT_VAL);

        PoolingHttpClientConnectionManager connectionManager = initializeConnectionManager(host);
        config.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);
        config.connectorProvider(new ApacheConnectorProvider());

        config.register(JacksonFeature.class);
        config.register(initializeOauth1FilterFeature(accessToken));
        config.register(new HeadersFilter(tenantId, userAgent));

        return ClientBuilder.newClient(config);
    }

    private Feature initializeOauth1FilterFeature(final AccessToken accessToken) {
        return OAuth1ClientSupport.builder(OWN_ACCOUNT_CREDENTIALS).signatureMethod(SIGNATURE_METHOD).version(VERSION)
                .feature().accessToken(accessToken).build();
    }

    private PoolingHttpClientConnectionManager initializeConnectionManager(final String host) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_CONNECTIONS_PER_ROUTE);
        connectionManager.setMaxPerRoute(new HttpRoute(HttpHost.create(host)), MAX_CONNECTIONS_PER_HOST);
        return connectionManager;
    }
}
