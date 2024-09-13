package io.quarkiverse.mdns.runtime;

import java.io.IOException;

import javax.jmdns.JmDNS;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Producer class for JmDNS instances and related resources.
 */
@ApplicationScoped
public class JmDNSProducer {

    private volatile JmDNS jmDNS;
    private volatile String url;

    /**
     * Initializes the JmDNSProducer with a JmDNS instance and URL.
     *
     * @param jmDNS The JmDNS instance to be used.
     * @param url The URL to be associated with this producer.
     */
    void initialize(JmDNS jmDNS, String url) {
        this.jmDNS = jmDNS;
        this.url = url;
    }

    /**
     * Closes the JmDNS instance, unregistering all services and releasing resources.
     *
     * @throws RuntimeException if an IOException occurs during closing.
     */
    void close() {
        if (jmDNS != null) {
            try {
                jmDNS.unregisterAllServices();
                jmDNS.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Produces a singleton JmDNS instance.
     *
     * @return The JmDNS instance.
     */
    @Singleton
    @Produces
    public JmDNS jmdns() {
        return jmDNS;
    }

    /**
     * Produces the URL exposed by mDNS.
     *
     * @return The exposed URL as a String.
     */
    @Produces
    @Named("mdnsExposedUrl")
    public String getMdnsExposedUrl() {
        return url;
    }
}