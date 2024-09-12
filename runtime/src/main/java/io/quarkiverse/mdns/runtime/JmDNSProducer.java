package io.quarkiverse.mdns.runtime;

import java.io.IOException;

import javax.jmdns.JmDNS;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@ApplicationScoped
public class JmDNSProducer {

    private volatile JmDNS jmDNS;

    void initialize(JmDNS jmDNS) {
        this.jmDNS = jmDNS;
    }

    void close() {
        if (jmDNS != null) {
            try {
                jmDNS.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Singleton
    @Produces
    public JmDNS jmdns() {
        return jmDNS;
    }
}