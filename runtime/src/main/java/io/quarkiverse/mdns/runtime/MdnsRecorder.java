package io.quarkiverse.mdns.runtime;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class MdnsRecorder {

    private static final Logger LOG = Logger.getLogger(MdnsRecorder.class);

    public void initMdns(BeanContainer container, MdnsRuntimeConfig config, ShutdownContext shutdownContext) {
        try {
            JmDNSProducer producer = container.beanInstance(JmDNSProducer.class);
            InetAddress inetAddress = InetAddress.getLocalHost();
            Optional<String> appName = ConfigProvider.getConfig().getOptionalValue("quarkus.application.name", String.class);
            String defaultName = appName.orElse(inetAddress.getHostName());
            String name = toURLFriendly(config.host().orElse(defaultName));
            LOG.infof("Registering mDNS service '%s'", name);
            JmDNS jmdns = JmDNS.create(inetAddress, name);
            Optional<Integer> port = ConfigProvider.getConfig().getOptionalValue("quarkus.http.port", Integer.class);
            int quarkusPort = port.orElse(8080);
            final String url = "http://%s.local:%d/".formatted(name, quarkusPort);
            final Map<String, String> properties = new HashMap<>();
            properties.put("URL", url);
            properties.putAll(config.props());
            ServiceInfo serviceInfo = ServiceInfo.create(config.type(), name, quarkusPort, config.weight(),
                    config.priority(), properties);
            jmdns.registerService(serviceInfo);
            LOG.infof("The application is available from: %s", url);
            producer.initialize(jmdns, url);
            shutdownContext.addShutdownTask(producer::close);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toURLFriendly(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Trim the input string and replace multiple spaces with a single space
        String cleanedInput = input.trim().replaceAll("\\s+", " ");

        // Replace spaces with hyphens and convert to lowercase
        String urlFriendly = cleanedInput.replace(" ", "-").toLowerCase();

        // Optionally remove other characters that might not be URL friendly
        urlFriendly = urlFriendly.replaceAll("[^a-z0-9\\-]", "");

        return urlFriendly;
    }

}