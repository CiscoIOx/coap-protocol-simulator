package com.cisco.iox.simulators.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoAPEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfigDefaults;

import java.net.InetSocketAddress;

/**
 * @author Santhosh Kumar Tekuri
 */
public class CoapSimulator {

    public static void main(String[] args) throws Exception{
        if(args.length!=4){
            System.err.println("arguments: <server/client> <config-file> <host> <port>");
            System.exit(1);
        }
        String simulator = args[0];
        String configFile = args[1];
        String host = args[2];
        int port = Integer.parseInt(args[3]);

        NetworkConfig networkConfig = getNetworkConfig();

        if("server".equals(simulator)){
            CoAPEndpoint endpoint = new CoAPEndpoint(new InetSocketAddress(host, port), networkConfig);
            CoapServer server = new CoapServer();
            server.addEndpoint(endpoint);
            new CoapServerConfigurer(configFile).configure(server);
            server.start();
        }else if("client".equals(simulator)){
            CoAPEndpoint endpoint = new CoAPEndpoint(networkConfig);
            endpoint.start();
            CoapClient client = new CoapClient();
            client.setTimeout(5000);
            client.setEndpoint(endpoint);
            new CoapClientConfigurer(configFile, host, port).configure(client);
        }else
            throw new IllegalArgumentException("first argument should be client or server");
    }

    private static NetworkConfig getNetworkConfig() {
        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setInt(NetworkConfigDefaults.DEFAULT_BLOCK_SIZE, 1024);
        networkConfig.setInt(NetworkConfigDefaults.MAX_MESSAGE_SIZE, 4096);
        networkConfig.setInt(NetworkConfigDefaults.ACK_TIMEOUT, 5000);
        return networkConfig;
    }
}
