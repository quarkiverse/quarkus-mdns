package io.quarkiverse.mdns.runtime;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Provide runtime data for JSON-RPC Endpoint.
 */
public class MdnsJsonRPCService {

    @Inject
    @Named("mdnsExposedUrl")
    String url;

    public String getService() {
        return url;
    }

}