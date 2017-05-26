package com.tradeshift.client;

import static com.tradeshift.client.ClientConstants.BASE_URI;
import static com.tradeshift.client.ClientConstants.PRODUCTION_HOSTNAME;
import static com.tradeshift.client.ClientConstants.SANDBOX_HOSTNAME;

import com.fasterxml.jackson.databind.JsonNode;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;


/**
 * This is a client specifically for handling REST API's
 * to Tradeshift's documents endpoint.
 */
public class DocumentsClient extends OAuth1Client {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentsClient.class);
    private static final String DOC_RESOURCE_URL = "/documents";

    private DocumentsClient(final String hostUrl,
                           final AccessToken token,
                           final UUID tenantId,
                           final String userAgent) {
        super(hostUrl + BASE_URI + DOC_RESOURCE_URL,
                token,
                tenantId,
                userAgent);
    }

    /**
     * Can switch to calling this method when ready to hit production.
     * Calls to Tradeshift API should be tested on Sandbox first.
     */
    @SuppressWarnings("unused")
    public static DocumentsClient createProductionClient(final String token,
                                                         final String tokenSecret,
                                                         final UUID tenantId,
                                                         final String userAgent) {
        return new DocumentsClient(
                PRODUCTION_HOSTNAME,
                new AccessToken(token, tokenSecret),
                tenantId,
                userAgent);
    }

    public static DocumentsClient createSandboxClient(final String token,
                                                      final String tokenSecret,
                                                      final UUID tenantId,
                                                      final String userAgent) {
        return new DocumentsClient(
                SANDBOX_HOSTNAME,
                new AccessToken(token, tokenSecret),
                tenantId,
                userAgent);
    }

    /**
     * Calls to Tradeshift's API for retrieving a list of documents.
     *
     * @param queryParams - Query parameters to add to the request URI.
     * @return A JSON Node representation of a DocumentsList entity.
     */
    public JsonNode getDocumentsJson(Map<String, String> queryParams) {
        WebTarget target = addQueryParameters(clientTarget, queryParams);
        LOG.info("Request URL: {}", target.getUri().toString());
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        try {
            if (response.getStatus() != 200) {
                LOG.error("Response status: [{}], payload: {}",
                        response.getStatus(),
                        response.readEntity(String.class));
                throw new RuntimeException("Request to Tradeshift API failed.");
            }
            return response.readEntity(JsonNode.class);
        } catch (ProcessingException | IllegalStateException ex) {
            throw new RuntimeException("Error processing payload from response.", ex);
        }
        finally {
            response.close();
        }
    }

}
