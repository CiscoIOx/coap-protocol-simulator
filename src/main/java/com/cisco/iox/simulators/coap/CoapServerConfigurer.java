package com.cisco.iox.simulators.coap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;

import java.io.File;
import java.util.List;

public class CoapServerConfigurer extends CoapConfigurer  {

    public CoapServerConfigurer() {
    }

    public CoapServerConfigurer(String confFile) {
        super(confFile);
    }

    public void configure(CoapServer server) {
        try {
            File configFile = getConfigFile();
            Configuration config = new ObjectMapper().readValue(configFile, Configuration.class);
            List<String[]> rows = readDataset(config, configFile);
            Resource resource = server.getRoot();
            for (String part : config.urn.split("/")) {
                Resource child = resource.getChild(part);
                if(child==null)
                    resource.add(child=new SimulatorResource(part));
                resource = child;
            }
            SimulatorResource urnResource = (SimulatorResource) resource;
            for (ResourceConfig resourceConfig : config.resources) {
                resource = urnResource;
                for (String part : resourceConfig.uri.split("/")) {
                    Resource child = resource.getChild(part);
                    if(child==null) {
                        resource.add(child = new SimulatorResource(part));
                        log.info("Adding coap resource: {}", child.getPath());
                    }
                    resource = child;
                }
                ((SimulatorResource)resource).setSimulatorData(rows, resourceConfig);
            }
        } catch (Exception ex) {
            log.error("Error while configuring coap server", ex);
        }
    }
}
