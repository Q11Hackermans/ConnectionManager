package net.jandie1505.connectionmanager.client;

import net.jandie1505.connectionmanager.client.events.CMCByteReceivedEvent;
import net.jandie1505.connectionmanager.client.events.CMCClosedEvent;
import net.jandie1505.connectionmanager.client.events.CMCCreatedEvent;
import net.jandie1505.connectionmanager.client.events.CMCEvent;
import net.jandie1505.connectionmanager.enums.CloseEventReason;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Client-side client (CMC = ConnectionManager Client)
 */
public class CMCClient implements Closeable {
    private Socket socket;
    private List<CMCEventListener> listeners;
    private Thread managerThread;
    private CMCInputStream clientInputStream;
    private CMCOutputStream clientOutputStream;

    /**
     * Create a client.
     * @param host Host
     * @param port Port
     * @throws IOException IOException of the socket
     */
    public CMCClient(String host, int port) throws IOException {
        this.listeners = new ArrayList<>();
        setup(host, port);
    }
    /**
     * Create a client with a pre-defined list of listeners.
     * This will make it possible to receive the CMCCreatedEvent.
     * @param host Host
     * @param port Port
     * @param listeners Collection of event listeners
     * @throws IOException IOException of the socket
     */
    public CMCClient(String host, int port, Collection<CMCEventListener> listeners) throws IOException {
        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);
        setup(host, port);
    }

    private void setup(String host, int port) throws IOException {
        this.socket = new Socket(host, port);

        this.clientInputStream = new CMCInputStream(this);
        this.clientOutputStream = new CMCOutputStream(this);

        managerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    int input = socket.getInputStream().read();
                    if(input == -1) {
                        socket.close();
                        fireEvent(new CMCClosedEvent(this, CloseEventReason.DISCONNECTED_BY_REMOTE));
                    } else {
                        onByteReceived(input);
                        fireEvent(new CMCByteReceivedEvent(this, input));
                    }
                } catch (IOException e) {
                    try {
                        socket.close();
                        fireEvent(new CMCClosedEvent(this, CloseEventReason.CONNECTION_RESET));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        fireEvent(new CMCClosedEvent(this, CloseEventReason.ERROR));
                        managerThread.interrupt();
                    }
                }
            }
        });
        managerThread.start();

        this.fireEvent(new CMCCreatedEvent(this));
    }

    /**
     * Add an EventListener
     * @param listener CMCEventListener
     */
    public void addEventListener(CMCEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove an EventListener
     * @param listener CMCEventListener
     */
    public void removeEventListener(CMCEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Close the connections and Input/Output streams
     * @throws IOException IOException
     */
    public void close() throws IOException {
        this.fireEvent(new CMCClosedEvent(this, CloseEventReason.CONNECTION_CLOSED));
        this.socket.close();
    }

    /**
     * Send byte via OutputStream
     * @param data byte
     * @throws IOException IOException
     */
    public void sendByte(int data) throws IOException {
        this.socket.getOutputStream().write(data);
    }

    /**
     * Get the InputStream of the client.
     * THIS IS NOT THE INPUT STREAM OF THE SOCKET! IT IS A COPY OF IT!
     * USE THIS INPUT STREAM INSTEAD OF THE SOCKET INPUT STREAM TO AVOID ERRORS!
     * @return InputStream
     */
    public InputStream getInputStream() {
        return this.clientInputStream;
    }

    /**
     * Get the OutputStream of the client.
     * THIS IS NOT THE OUTPUT STREAM OF THE SOCKET! IT IS A COPY OF IT!
     * USE THIS OUTPUT STREAM INSTEAD OF THE SOCKET OUTPUT STREAM TO AVOID ERRORS!
     * @return OutputStream
     */
    public OutputStream getOutputStream() {
        return this.clientOutputStream;
    }

    /**
     * Get the closed and connection state
     * @return
     */
    public boolean isClosed() {
        return this.socket.isClosed();
    }

    /**
     * Get the IP Address
     * @return InetAddress
     */
    public InetAddress getIP() {
        return this.socket.getInetAddress();
    }

    /**
     * Get the port
     * @return Port
     */
    public int getPort() {
        return this.socket.getPort();
    }

    /**
     * Get socket
     * @return Socket
     * @deprecated use other methods if possible
     */
    @Deprecated
    public Socket getSocket() {
        return this.socket;
    }

    // PRIVATE

    /**
     * Similar to CMCByteReceivedEvent
     * @param data byte
     */
    private void onByteReceived(int data) {
        this.clientInputStream.send(data);
    }

    /**
     * Fire an event to all event listeners
     * @param event Event
     */
    private void fireEvent(CMCEvent event) {
        for(CMCEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
