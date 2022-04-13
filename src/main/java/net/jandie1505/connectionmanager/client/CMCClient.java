package net.jandie1505.connectionmanager.client;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.CMClientEventListener;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

/**
 * Client-side client (CMC = ConnectionManager Client)
 */
public class CMCClient extends CMClient {

    /**
     * Create a client.
     * @param host Host
     * @param port Port
     * @throws IOException IOException of the socket
     */
    public CMCClient(String host, int port) throws IOException {
        super(new Socket(host, port));
    }
    /**
     * Create a client with a pre-defined list of listeners.
     * This will make it possible to receive the CMCCreatedEvent.
     * @param host Host
     * @param port Port
     * @param listeners Collection of event listeners
     * @throws IOException IOException of the socket
     */
    public CMCClient(String host, int port, Collection<CMClientEventListener> listeners) throws IOException {
        super(new Socket(host, port), listeners);
    }
}
