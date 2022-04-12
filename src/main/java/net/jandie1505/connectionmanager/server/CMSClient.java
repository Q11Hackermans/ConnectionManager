package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.enums.CloseEventReason;
import net.jandie1505.connectionmanager.server.events.CMSClientCloseEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientCreatedEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientByteReceivedEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Server-side client (CMS = ConnectionManager Server)
 */
public class CMSClient {
    UUID id;
    private Socket socket;
    private List<CMSClientEventListener> listeners;
    private Thread managerThread;

    public CMSClient(Socket socket) {
        this.listeners = new ArrayList<>();
        setup(socket);
    }

    public CMSClient(Socket socket, List<CMSClientEventListener> listeners) {
        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);
        setup(socket);
    }

    private void setup(Socket socket) {
        this.id = UUID.randomUUID();
        this.socket = socket;

        this.managerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    if(!socket.isConnected()) {
                        socket.close();
                        fireEvent(new CMSClientCloseEvent(this, CloseEventReason.CONNECTION_FAILED));
                    }

                    int input = socket.getInputStream().read();
                    if(input == -1) {
                        socket.close();
                        fireEvent(new CMSClientCloseEvent(this, CloseEventReason.DISCONNECTED_BY_REMOTE));
                    } else {
                        onByteReceived(input);
                        fireEvent(new CMSClientByteReceivedEvent(this, input));
                    }
                } catch (IOException e) {
                    try {
                        socket.close();
                        fireEvent(new CMSClientCloseEvent(this, CloseEventReason.CONNECTION_RESET));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        fireEvent(new CMSClientCloseEvent(this, CloseEventReason.ERROR));
                        managerThread.interrupt();
                    }
                }
            }
        });
        managerThread.start();

        this.fireEvent(new CMSClientCreatedEvent(this));
    }

    /**
     * Get the unique ID of the client
     * @return Unique ID (UUID)
     */
    public UUID getUniqueId() {
        return this.id;
    }

    /**
     * Add an EventListener
     * @param listener CMSClientEventListener
     */
    public void addEventListener(CMSClientEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove an EventListener
     * @param listener CMSClientEventListener
     */
    public void removeEventListener(CMSClientEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get the event listeners
     * @return Listener list
     */
    public List<CMSClientEventListener> getListeners() {
        return this.listeners;
    }

    /**
     * Close the connections and Input/Output streams
     * @throws IOException IOException
     */
    public void close() throws IOException {
        this.socket.close();
        this.fireEvent(new CMSClientCloseEvent(this, CloseEventReason.CONNECTION_CLOSED));
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

    /**
     * Returns true if the manager thread is running
     * @return Manager thread alive
     */
    public boolean managerThreadAlive() {
        return managerThread.isAlive();
    }

    // PRIVATE

    /**
     * This method is similar to the CMSClientByteReceivedEvent
     * @param data byte
     */
    private void onByteReceived(int data) {

    }

    /**
     * Fire an event to all event listeners
     * @param event Event
     */
    private void fireEvent(CMSClientEvent event) {
        for(CMSClientEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
