package com.cisco.iox.simulators.coap;

import java.util.List;

/**
 * @author Santhosh Kumar Tekuri
 */
public class Configuration {
    public String urn;
    public QueryParameter queryParameter;
    public String dataset;
    public long intervalInMilliSeconds;
    public List<ResourceConfig> resources;
}
