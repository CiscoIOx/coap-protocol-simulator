package com.cisco.iox.simulators.coap;


import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Santhosh Kumar Tekuri
 */
public class SimulatorResource extends CoapResource{

    private static Logger log = LoggerFactory.getLogger(SimulatorResource.class);

    public SimulatorResource(String name) {
        super(name);
    }

    private List<String[]> rows;
    private String template;
    ResourceConfig config;
    private int index = 0;

    public void setSimulatorData(List<String[]> rows, ResourceConfig config){
        this.rows = rows;
        this.config = config;

    }

    @Override
    public void handleGET(CoapExchange exchange) {
        log.debug("SimulatorResource.GET ");
        if(rows==null)
            super.handleGET(exchange);
        else{
            int row;
            synchronized (this){
                ++index;
                if(index==rows.size())
                    index = 1;
                row = index;
            }
            String payload = config.evaluate(rows, row);
            Response response = config.contentType.createResponse(payload);
            log.info("Reply {} ", payload);
            exchange.respond(response);
        }
    }

    public void handlePUT(CoapExchange exchange) {
        log.debug("SimulatorResource.PUT ");
        String requestText = exchange.getRequestText();
        if (rows == null) {
            //Not thread safe
            rows = new ArrayList<String[]>();
        }
        rows.add(index, requestText.split(","));
        Response response = ContentType.json.createResponse("{\"status\":\"success\"}");
        exchange.respond(response);
    }
}
