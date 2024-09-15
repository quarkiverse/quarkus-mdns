package io.quarkiverse.mdns.runtime;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithUnnamedKey;

@ConfigMapping(prefix = "quarkus.mdns")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface MdnsRuntimeConfig {

    /**
     * Indicate if the extension should be enabled. Enabled in DEV mode by default if not explicitly disabled.
     */
    Optional<Boolean> enabled();

    /**
     * Host name to advertise in mDNS, will use the `quarkus.application.name` then machine name if left blank.
     */
    Optional<String> host();

    /**
     * Fully qualified service type name, such as <code>_http._tcp.local.</code>
     */
    @WithDefault("_http._tcp.local.")
    String type();

    /**
     * Weight of the service
     */
    @WithDefault("0")
    int weight();

    /**
     * Priority of the service
     */
    @WithDefault("0")
    int priority();

    /**
     * Extra properties to append into the service
     */
    @WithUnnamedKey("localhost")
    Map<String, String> props();
}