package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.events.CMClientCloseEvent;
import net.jandie1505.connectionmanager.server.events.CMEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CMServerClient {
    UUID id;
    private Socket client;
    private Thread thread;
    private List<CMEventListener> listeners;

    public CMServerClient(Socket socket) {
        this.id = UUID.randomUUID();
        listeners = new ArrayList<>();
        this.client = socket;
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
     * @throws IOException
     */
    public void close() throws IOException {
        this.fireEvent(new CMClientCloseEvent(this));
        this.client.close();
    }

    /**
     * Get socket
     * @return Socket
     */
    public Socket getClient() {
        return this.client;
    }

    /**
     * Fire an event to all event listeners
     * @param event Event
     */
    private void fireEvent(CMEvent event) {
        for(CMEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
