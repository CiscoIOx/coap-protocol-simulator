package com.cisco.iox.simulators.coap;

public class CoapSimulatorException extends RuntimeException {

    public CoapSimulatorException() {
    }

    public CoapSimulatorException(String message) {
        super(message);
    }

    public CoapSimulatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoapSimulatorException(Throwable cause) {
        super(cause);
    }
}
