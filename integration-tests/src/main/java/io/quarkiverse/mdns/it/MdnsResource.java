/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.quarkiverse.mdns.it;

import java.util.ArrayList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/mdns")
@ApplicationScoped
public class MdnsResource {

    @Inject
    JmDNS jmDNS;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String listService() {
        ServiceInfo[] infos = jmDNS.list("_http._tcp.local.");
        for (ServiceInfo info : infos) {
            if (info.getServer().equalsIgnoreCase("integration.local.")) {
                return info.getNiceTextString();
            }
        }
        return "";
    }

    @GET
    @Path("/http")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ServiceRecord> getHttpServices() {
        List<ServiceRecord> results = new ArrayList<>();
        ServiceInfo[] infos = jmDNS.list("_http._tcp.local.");
        for (ServiceInfo info : infos) {
            results.add(new ServiceRecord(info));
        }
        return results;
    }
}
