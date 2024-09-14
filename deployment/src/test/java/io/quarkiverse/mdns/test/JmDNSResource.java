package io.quarkiverse.mdns.test;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/jmdns")
public class JmDNSResource {
    @Inject
    JmDNS jmDNS;

    @POST
    @Path("/list")
    @Produces(MediaType.TEXT_PLAIN)
    public String listService() {
        ServiceInfo[] infos = jmDNS.list("_http._tcp.local.");
        for (ServiceInfo info : infos) {
            if (info.getServer().equalsIgnoreCase("testing.local.")) {
                return info.getPropertyString("URL");
            }
        }
        return "";
    }

}