package com.tradeshift;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.tradeshift.client.DocumentsClient;

import java.util.Map;
import java.util.UUID;


/**
 * This is an example of how to connect to Tradeshift
 * APIs to retrieve a list of documents using the
 * sample client.
 */
public class SampleClientDemo {
	/*
	 * REPLACE THE VALUES OF "TOKEN", "TOKEN_SECRET", AND "TRADESHIFT_TENANT_ID"!
	 *
	 * You obtain these values by activating the "API Access to Own Account" app:
	 * https://sandbox.tradeshift.com/apps/view/Tradeshift.APIAccessToOwnAccount
	 */
	private static final String TOKEN = "<YOUR_VALUE_HERE>";
	private static final String TOKEN_SECRET = "<YOUR_VALUE_HERE>";
	private static final UUID TRADESHIFT_TENANT_ID = UUID.fromString("<YOUR_VALUE_HERE>");

	/*
	 * REPLACE THE VALUE OF USER_AGENT!
	 *
     * This value must uniquely identify you as an API user and is of
     * your choosing.
     */
    private static final String USER_AGENT = "<YOUR_VALUE_HERE>";

	public static void main(final String[] args) throws Exception {
		final DocumentsClient documentsClient = DocumentsClient.createSandboxClient(
		        TOKEN,
                TOKEN_SECRET,
                TRADESHIFT_TENANT_ID,
                USER_AGENT);

		/*
		 * Refer to https://api.tradeshift.com/tradeshift/rest/external/doc,
		 * specifically in the "Documents" section, for the list of all
		 * possible query params you can pass to the API.
		 */
		Map<String, String> queryParams = Maps.newHashMap();
		queryParams.put("type", "invoice");
		queryParams.put("processState", "OVERDUE");
        JsonNode documentListNode = documentsClient.getDocumentsJson(queryParams);
        prettyPrint(documentListNode);
	}

    /**
     * This is simply for demo purposes to display a pretty-printed
     * version of the response JSON that gets returned.
     *
     * @param node - The JSON response to pretty print
     * @throws JsonProcessingException
     */
	private static void prettyPrint(JsonNode node) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String prettyPrintedJson = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(node);
            System.out.println(prettyPrintedJson);
        } catch (JsonProcessingException e) {
            System.out.println("Pretty printing failed.");
        }
    }
}
