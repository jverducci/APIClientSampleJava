package com.tradeshift.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;


/**
 * A client filter that will append the user agent and Tradeshift
 * tenant headers to every outbound request.
 */
@Provider
public class HeadersFilter implements ClientRequestFilter {

    private static final String TENANT_HEADER = "X-Tradeshift-TenantId";
    private static final String USER_AGENT_HEADER = "User-Agent";

    private final UUID tenantId;
    private final String userAgent;

    public HeadersFilter(final UUID tenantId, final String userAgent) {
        checkNotNull(tenantId);
        checkArgument(StringUtils.isNotBlank(userAgent));
        this.tenantId = tenantId;
        this.userAgent = userAgent;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(TENANT_HEADER, tenantId.toString());
        requestContext.getHeaders().add(USER_AGENT_HEADER, userAgent);
    }
}
