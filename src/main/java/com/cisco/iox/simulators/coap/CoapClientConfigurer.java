package com.cisco.iox.simulators.coap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CoapClientConfigurer extends CoapConfigurer {

    private String host;
    private int port;

    public CoapClientConfigurer() {
    }

    public CoapClientConfigurer(String confFile, String host, int port) {
        super(confFile);
        this.host = host;
        this.port = port;
    }

    public void configure(CoapClient client) {
        try {
            File configFile = getConfigFile();
            Configuration config = new ObjectMapper().readValue(configFile, Configuration.class);
            List<String[]> rows = readDataset(config, configFile);
            final QueryParameter queryParameter = config.queryParameter;
            if(queryParameter ==null)
                throw new IllegalArgumentException("queryParameter is not specified");
            if(queryParameter.name==null)
                throw new IllegalArgumentException("queryParameter name is not specified");
            queryParameter.name = queryParameter.name.trim();
            if(queryParameter.name.isEmpty())
                throw new IllegalArgumentException("queryParameter name is empty");
            if(queryParameter.value==null)
                throw new IllegalArgumentException("queryParameter value is not specified");
            queryParameter.value = queryParameter.value.trim();
            if(queryParameter.value.isEmpty())
                throw new IllegalArgumentException("queryParameter value is empty");
            String url = "coap://"+host+":"+port+"/"+config.urn+"/";
            String query = "?"+queryParameter.name+"="+queryParameter.value;
            int index = 0;
            while (true) {
                for (ResourceConfig resourceConfig : config.resources) {
                    client.setURI(url + resourceConfig.uri+query);
                    ++index;
                    if(index==rows.size())
                        index = 1;
                    String payload = resourceConfig.evaluate(rows, index);
                    CoapResponse response = resourceConfig.contentType.put(client, payload);
                    if(response==null)
                        log.error("put timedout for "+client.getURI());
                    else if(!response.isSuccess())
                        log.error("put failed for "+client.getURI()+". reason: "+response.getResponseText());
                    else
                        log.debug(client.getURI() +  "PUT -> " + response.getResponseText());
                }
                Thread.sleep(config.intervalInMilliSeconds);
            }
        } catch (Exception ex) {
            log.error("Error while configuring coap server", ex);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
