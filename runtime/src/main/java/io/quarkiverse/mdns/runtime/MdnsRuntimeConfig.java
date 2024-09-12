package io.quarkiverse.mdns.runtime;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.mdns")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface MdnsRuntimeConfig {

    /**
     * Indicate if the extension should be enabled. Enabled in DEV mode by default if not explicitly disabled.
     */
    Optional<Boolean> enabled();

    /**
     * Host name to advertise in mDNS, will use the machine name if left blank.
     */
    Optional<String> host();
}