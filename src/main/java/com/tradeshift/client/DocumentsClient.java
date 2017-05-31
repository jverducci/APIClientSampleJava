package com.tradeshift.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.tradeshift.client.ClientConstants.BASE_URI;
import static com.tradeshift.client.ClientConstants.PRODUCTION_HOSTNAME;
import static com.tradeshift.client.ClientConstants.SANDBOX_HOSTNAME;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Map;
import java.util.UUID;

/**
 * This is a client specifically for handling REST API's
 * to Tradeshift's documents endpoint.
 */
public class DocumentsClient extends OAuth1Client {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentsClient.class);

    private static final Entity EMPTY_JSON = Entity.json(StringUtils.EMPTY);
    private static final String DOCUMENTS_RESOURCE = "/documents";
    private static final String TAG_RESOURCE = "/%s/tags/%s";

    private String baseDocumentsUrl;

    private DocumentsClient(final String host, final AccessToken token, final UUID tenantId, final String userAgent) {
        super(host, token, tenantId, userAgent);
        this.baseDocumentsUrl = host + BASE_URI + DOCUMENTS_RESOURCE;
    }

    /**
     * Can switch to calling this method when ready to hit production.
     * Calls to Tradeshift API should be tested on Sandbox first.
     */
    @SuppressWarnings("unused")
    public static DocumentsClient createProductionClient(final String token, final String tokenSecret,
            final UUID tenantId, final String userAgent) {
        return new DocumentsClient(PRODUCTION_HOSTNAME, new AccessToken(token, tokenSecret), tenantId, userAgent);
    }

    public static DocumentsClient createSandboxClient(final String token, final String tokenSecret, final UUID tenantId,
            final String userAgent) {
        return new DocumentsClient(SANDBOX_HOSTNAME, new AccessToken(token, tokenSecret), tenantId, userAgent);
    }

    /**
     * Calls to Tradeshift's API for retrieving a list of documents.
     *
     * @param queryParams - Query parameters to add to the request URI.
     * @return A JSON Node representation of a DocumentsList entity.
     */
    public JsonNode getDocumentsJson(Map<String, String> queryParams) {
        WebTarget target = addQueryParameters(client().target(baseDocumentsUrl), queryParams);
        LOG.info("Request URL: {} {}", HttpMethod.GET, target.getUri().toString());
        Response response = target.request(MediaType.APPLICATION_JSON).get();

        try {
            validateResponseCode(response, Status.OK);
            return response.readEntity(JsonNode.class);
        } catch (ProcessingException | IllegalStateException ex) {
            throw new RuntimeException("Error processing payload from response.", ex);
        } finally {
            response.close();
        }
    }

    /**
     * Calls to Tradeshift's API to add a tag to a specific document.
     *
     * @param documentId - The unique identifier of the document to tag.
     * @param tag - The value of the tag to add to the document.
     */
    public void addTagToDocument(final UUID documentId, final String tag) {
        checkNotNull(documentId);
        checkArgument(StringUtils.isNotBlank(tag));

        // To cut down on instances of web targets being created, we
        // figure out the full path at once rather than building it.
        String targetUrl = String.format(baseDocumentsUrl + TAG_RESOURCE, documentId.toString(), tag);
        WebTarget target = client().target(targetUrl);
        LOG.info("Request URL: {} {}", HttpMethod.PUT, target.getUri().toString());
        // Jersey requires an entity to always be passed in a PUT request
        Response response = target.request().put(EMPTY_JSON);

        try {
            validateResponseCode(response, Status.CREATED);
        } finally {
            response.close();
        }
    }

    /**
     * Calls to Tradeshift's API to remove a tag from a specific document.
     *
     * @param documentId - The unique identifier of the document to remove a tag from.
     * @param tag - The value of the tag to remove from the document.
     */
    public void removeTagFromDocument(final UUID documentId, final String tag) {
        checkNotNull(documentId);
        checkArgument(StringUtils.isNotBlank(tag));

        // To cut down on instances of web targets being created, we
        // figure out the full path at once rather than building it.
        String targetUrl = String.format(baseDocumentsUrl + TAG_RESOURCE, documentId.toString(), tag);
        WebTarget target = client().target(targetUrl);
        LOG.info("Request URL: {} {}", HttpMethod.DELETE, target.getUri().toString());
        Response response = target.request().delete();

        try {
            validateResponseCode(response, Status.OK);
        } finally {
            response.close();
        }
    }

    @Override
    public void close() {
        client().close();
    }
}
