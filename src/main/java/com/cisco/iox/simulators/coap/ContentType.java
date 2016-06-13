package com.cisco.iox.simulators.coap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;

import java.io.IOException;

/**
 * @author Santhosh Kumar Tekuri
 */
public enum ContentType {
    raw {
        @Override
        public Response createResponse(String content) {
            Response response = new Response(CoAP.ResponseCode.CONTENT);
            response.setPayload(content);
            response.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
            return response;
        }

        @Override
        public CoapResponse put(CoapClient client, String content) {
            return client.put(content, MediaTypeRegistry.TEXT_PLAIN);
        }
    }, csv {
		@Override
		public Response createResponse(String content) {
			Response response = new Response(CoAP.ResponseCode.CONTENT);
            response.setPayload(content);
            response.getOptions().setContentFormat(MediaTypeRegistry.TEXT_CSV);
            return response;
		}

		@Override
		public CoapResponse put(CoapClient client, String content) {
			 return client.put(content, MediaTypeRegistry.TEXT_CSV);
		}
    	
    }, json {
        @Override
        public Response createResponse(String content) {
            Response response = new Response(CoAP.ResponseCode.CONTENT);
            response.setPayload(content);
            response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
            return response;
        }

        @Override
        public CoapResponse put(CoapClient client, String content) {
            return client.put(content, MediaTypeRegistry.APPLICATION_JSON);
        }
    }, cbor {
        private byte[] toCBOR(String content){
            try {
                final JsonNode jsonNode = new ObjectMapper().readTree(content);
                return new ObjectMapper(new CBORFactory()).writeValueAsBytes(jsonNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Response createResponse(String content) {
            Response response = new Response(CoAP.ResponseCode.CONTENT);
            response.setPayload(toCBOR(content));
            response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_OCTET_STREAM);
            return response;
        }

        @Override
        public CoapResponse put(CoapClient client, String content) {
            return client.put(toCBOR(content), MediaTypeRegistry.APPLICATION_OCTET_STREAM);
        }
    };

    public abstract Response createResponse(String content);
    public abstract CoapResponse put(CoapClient client, String content);
}
