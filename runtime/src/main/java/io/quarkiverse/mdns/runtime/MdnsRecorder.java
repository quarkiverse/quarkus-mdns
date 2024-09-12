package io.quarkiverse.mdns.runtime;

import java.io.IOException;
import java.net.InetAddress;
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
            String hostName = config.host().orElse(inetAddress.getHostName());
            LOG.infof("Registering mDNS service '%s'", hostName);
            JmDNS jmdns = JmDNS.create(inetAddress, hostName);
            Optional<Integer> port = ConfigProvider.getConfig().getOptionalValue("quarkus.http.port", Integer.class);
            int quarkusPort = port.orElse(8080);
            final String url = "http://%s:%d/".formatted(hostName, quarkusPort);
            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", hostName, quarkusPort, 0, 0, url);
            jmdns.registerService(serviceInfo);
            LOG.infof("The application is available from: %s", url);
            producer.initialize(jmdns);
            shutdownContext.addShutdownTask(producer::close);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}