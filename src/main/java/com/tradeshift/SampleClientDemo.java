package com.tradeshift;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.tradeshift.client.DocumentsClient;

import java.util.Map;
import java.util.UUID;

/**
 * This is an example of how to connect to Tradeshift APIs
 * to retrieve a list of documents using the sample client.
 * <p/>
 * In order to populate the constant values below, you will
 * need to access the APIAccessToOwnAccount app from the
 * App Store:
 * <p/>
 * <a href="https://sandbox.tradeshift.com/apps/view/Tradeshift.APIAccessToOwnAccount">APIAccessToOwnAccount</a>
 */
public class SampleClientDemo {
    // This is simply here for demo purposes when pretty printing responses.
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final String BUSINESS_DELIVERED_TAG = "BusinessDelivered";
    private static final String DOC_TYPE_PARAM = "type";
    private static final String DOC_PROCESS_STATE_PARAM = "processState";
    private static final String DOC_WITHOUT_TAG_PARAM = "withouttag";

    // -- REPLACE YOUR VALUES HERE!! --
    // (NOTE: "USER_AGENT" is your choice and should be a value that uniquely
    // identifies your client as an API user , i.e. "Customer X/1.0.0".)
    private static final String USER_AGENT = "<YOUR_VALUE_HERE>";
    private static final String TOKEN = "<YOUR_VALUE_HERE>";
    private static final String TOKEN_SECRET = "<YOUR_VALUE_HERE>";
    private static final UUID TRADESHIFT_TENANT_ID = UUID.fromString("<YOUR_VALUE_HERE>");

    /**
     * DEMO CODE STARTS HERE!!
     */
    public static void main(final String[] args) throws Exception {
        /*
         * STEP 1: Create a client that is capable of talking to Document APIs.
         *
         * (NOTE: DocumentsClient.createProductionClient() can be used for production.
         */
        final DocumentsClient documentsClient = DocumentsClient.createSandboxClient(TOKEN, TOKEN_SECRET,
                TRADESHIFT_TENANT_ID, USER_AGENT);

        /*
         * STEP 2: Establish any query params you want to specify when fetching
         * any documents.
         *
         * Refer to https://api.tradeshift.com/tradeshift/rest/external/doc,
         * specifically in the "Documents" section, for the list of all
         * possible query params you can pass to the API.
         */
        Map<String, String> queryParams = Maps.newHashMap();
        queryParams.put(DOC_TYPE_PARAM, "invoice");
        queryParams.put(DOC_PROCESS_STATE_PARAM, "ACCEPTED");
        queryParams.put(DOC_WITHOUT_TAG_PARAM, BUSINESS_DELIVERED_TAG);

        // STEP 3: Pull all accepted invoices that don't have the tag "BusinessDelivered".
        JsonNode documentListRoot = documentsClient.getDocumentsJson(queryParams);
        prettyPrint(documentListRoot);

        /*
         * STEP 4: Add the "BusinessDelivered" tag to one of the documents in the list.
         * We'll also make another call to fetch all documents and print it to show
         * that the tag took effect.
         */
        String docIdText = documentListRoot.get("Document").get(0).get("DocumentId").asText();
        UUID documentId = UUID.fromString(docIdText);
        documentsClient.addTagToDocument(documentId, BUSINESS_DELIVERED_TAG);
        prettyPrint(documentsClient.getDocumentsJson(queryParams));

        /*
         * STEP 5: We will remove the "BusinessDelivered" tag from the same document.
         * We'll also make another call to fetch all documents and print it to show
         * that tag removal took effect.
         */
        documentsClient.removeTagFromDocument(documentId, BUSINESS_DELIVERED_TAG);
        prettyPrint(documentsClient.getDocumentsJson(queryParams));

        /*
         * STEP 6: Close the client to free up resources.
         */
        documentsClient.close();
    }

    /**
     * This is simply for demo purposes to display a pretty-printed
     * version of the response JSON that gets returned.
     *
     * @param node - The JSON response to pretty print
     */
    private static void prettyPrint(JsonNode node) {
        try {
            String prettyPrintedJson = JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(node);
            System.out.println(prettyPrintedJson);
        } catch (JsonProcessingException e) {
            System.out.println("Pretty printing failed.");
        }
    }
}
