package io.quarkiverse.mdns.it;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jmdns.ServiceInfo;

public class ServiceRecord {
    public String name;
    public String type;
    public int port;
    public List<String> properties = new ArrayList<>();

    public ServiceRecord(ServiceInfo info) {
        this.name = info.getName();
        this.type = info.getTypeWithSubtype();
        this.port = info.getPort();
        for (Enumeration<String> names = info.getPropertyNames(); names.hasMoreElements();) {
            String prop = names.nextElement();
            properties.add(prop + "=" + info.getPropertyString(prop));
        }
    }
}
