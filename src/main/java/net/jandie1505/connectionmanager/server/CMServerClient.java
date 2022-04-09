package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.actions.CMAction;
import net.jandie1505.connectionmanager.server.events.CMClientCloseEvent;
import net.jandie1505.connectionmanager.server.events.CMClientCreatedEvent;
import net.jandie1505.connectionmanager.server.events.CMEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CMServerClient {
    UUID id;
    private Socket socket;
    private List<CMEventListener> listeners;
    private List<CMAction> actions;

    public CMServerClient(Socket socket) {
        this.id = UUID.randomUUID();
        listeners = new ArrayList<>();
        actions = new ArrayList<>();
        this.socket = socket;

        this.fireEvent(new CMClientCreatedEvent(this));

        new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                try {
                    if(socket.getInputStream().read() == -1) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    for(CMAction action : actions) {
                        action.run(this);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
     * @param listener CMEventListener
     */
    public void addEventListener(CMEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove an EventListener
     * @param listener CMEventListener
     */
    public void removeEventListener(CMEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Close the connections and Input/Output streams
     * @throws IOException IOException
     */
    public void close() throws IOException {
        this.fireEvent(new CMClientCloseEvent(this));
        this.socket.close();
    }

    /**
     * Get the InputStream
     * @return InputStream
     * @throws IOException IOException
     */
    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    /**
     * Get the OutputStream
     * @return OutputStream
     * @throws IOException IOException
     */
    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    /**
     * Get the closed and connection state
     * @return
     */
    public boolean isClosed() {
        return this.socket.isClosed();
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
     * Fire an event to all event listeners
     * @param event Event
     */
    public void fireEvent(CMEvent event) {
        for(CMEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
