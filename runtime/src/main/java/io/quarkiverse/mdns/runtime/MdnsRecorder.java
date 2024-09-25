package io.quarkiverse.mdns.runtime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
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
            LOG.infof("Localhost %s IP Address: %s", inetAddress.getHostName(), inetAddress.getHostAddress());
            Optional<String> appName = ConfigProvider.getConfig().getOptionalValue("quarkus.application.name", String.class);
            Optional<String> httpHost = ConfigProvider.getConfig().getOptionalValue("quarkus.http.host", String.class);
            if (!httpHost.orElse("localhost").equals("0.0.0.0")) {
                LOG.warnf(
                        "For mDNS to work properly, 'quarkus.http.host' must be set to '0.0.0.0' for the local domain URL to be accessible.");
            }
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

    public static InetAddress getIpAddress() throws UnknownHostException, SocketException {
        // Get all network interfaces
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        InetAddress localhost = InetAddress.getLocalHost();
        // Print out information about each IP address
        LOG.infof("Localhost %s IP Address: %s", localhost.getHostName(), localhost.getHostAddress());

        // Iterate through all interfaces
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            // Get all IP addresses for each network interface
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                    // Print out information about each IP address
                    LOG.infof("Network Interface: %s", networkInterface.getDisplayName());
                    LOG.infof("%s IP Address: %s", inetAddress.getHostName(), inetAddress.getHostAddress());
                    LOG.debugf("    Loopback: %s", inetAddress.isLoopbackAddress());
                    LOG.debugf("    Site Local: %s", inetAddress.isSiteLocalAddress());
                    LOG.debugf("    Multicast: %s", inetAddress.isMulticastAddress());
                    LOG.debugf("    Any Local: %s", inetAddress.isAnyLocalAddress());
                    LOG.debugf("    Link Local: %s", inetAddress.isLinkLocalAddress());
                    localhost = inetAddress;
                }
            }
        }
        return localhost;
    }

}