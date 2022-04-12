package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.enums.CloseEventReason;
import net.jandie1505.connectionmanager.server.events.CMSClientByteReceivedEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientCloseEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientCreatedEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientEvent;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Server-side client (CMS = ConnectionManager Server)
 */
public class CMSClient implements Closeable {
    UUID id;
    private Socket socket;
    private List<CMSClientEventListener> listeners;
    private Thread managerThread;
    private CMSInputStream clientInputStream;
    private CMSOutputStream clientOutputStream;

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

        clientInputStream = new CMSInputStream(this);
        clientOutputStream = new CMSOutputStream(this);

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
        this.clientInputStream.send(data);
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
