package io.quarkiverse.mdns.runtime;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import io.smallrye.mutiny.Uni;

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

    public Uni<String> launchBrowser() {
        new MdnsBrowser();
        return Uni.createFrom().item("Browser Launched");
    }

}
