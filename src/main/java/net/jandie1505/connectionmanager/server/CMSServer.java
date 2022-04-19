package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.CMClientEventListener;
import net.jandie1505.connectionmanager.enums.ConnectionBehavior;
import net.jandie1505.connectionmanager.server.events.CMSServerConnectionAcceptedEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStartedEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * Server (CMS = ConnectionManager Server)
 */
public class CMSServer {
    // BASIC THINGS
    private final ServerSocket server;
    private final Map<UUID, CMSClient> clients;
    private final Thread thread;
    private final Map<UUID, CMSClient> pendingConnections;
    private ConnectionBehavior defaultConnectionBehavior;
    private long connectionReactionTime;

    // SERVER SPECIFIC THINGS
    private final List<CMSServerEventListener> listeners;

    // CLIENT SPECIFIC THINGS
    private final List<CMClientEventListener> globalClientListeners;

    // SETUP
    public CMSServer(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.clients = new HashMap<>();
        this.defaultConnectionBehavior = ConnectionBehavior.REFUSE;
        this.pendingConnections = new HashMap<>();
        this.connectionReactionTime = 10;

        this.listeners = new ArrayList<>();

        this.globalClientListeners = new ArrayList<>();

        // This will remove all clients which are disconnected
        Thread garbageCollection = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !server.isClosed()) {
                    for(UUID uuid : clients.keySet()) {
                        CMSClient client = clients.get(uuid);
                        if(client == null || client.isClosed()) {
                            clients.remove(uuid);
                        }
                    }
                }
            }
        });
        garbageCollection.setName(this + "-GarbageCollectionThread");
        garbageCollection.setDaemon(true);
        garbageCollection.start();

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.server.isClosed()) {
                try {
                    CMSClient client = new CMSClient(server.accept(), this, globalClientListeners);
                    clients.put(this.getRandomUniqueId(), client);
                    this.fireEvent(new CMSServerConnectionAcceptedEvent(this, client));
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });
        this.thread.setName(this + "-ConnectionThread");
        this.thread.start();

        this.fireEvent(new CMSServerStartedEvent(this));
    }

    // CONNECTION ACCEPTING

    /**
     * Returns the current default connection behavior
     * @return ConnectionBehavior
     */
    public ConnectionBehavior getDefaultConnectionBehavior() {
        return this.defaultConnectionBehavior;
    }

    /**
     * Set the default connection behavior
     * @param behavior ConnectionBehavior
     */
    public void setDefaultConnectionBehavior(ConnectionBehavior behavior) {
        this.defaultConnectionBehavior = behavior;
    }

    /**
     * Allow a connection (if the default connection behavior is REFUSE)
     * @param uuid UUID of the pending connection
     */
    public void allowConnection(UUID uuid) {

    }

    /**
     * Deny a connection (if the default connection behavior is ALLOW)
     * @param uuid UUID of the pending connection
     */
    public void denyConnection(UUID uuid) {

    }

    /**
     * Accept all pending connections
     */
    public void acceptAllConnections() {

    }

    /**
     * Deny all pending connections
     */
    public void denyAllConnections() {

    }

    /**
     * Get all pending connections
     * @return Map of UUIDs and pending connections
     */
    public Map<UUID, CMSClient> getPendingConnections() {
        return Map.copyOf(this.pendingConnections);
    }

    // CLIENT UUIDS
    private UUID getRandomUniqueId() {
        UUID uuid = UUID.randomUUID();
        boolean unique = true;
        for(UUID compare : this.clients.keySet()) {
            if(uuid.equals(compare)) {
                unique = false;
            }
        }
        if(unique) {
            return uuid;
        } else {
            return this.getRandomUniqueId();
        }
    }

    /**
     * Returns a map of the clients and their UUIDs
     * @return Map
     */
    public Map<UUID, CMSClient> getClients() {
        return this.clients;
    }

    /**
     * Get UUID of Client
     * @param client CMSClient
     * @return UUID
     */
    protected UUID getIdOfClient(CMSClient client) {
        for(UUID uuid : this.clients.keySet()) {
            CMSClient c = this.clients.get(uuid);
            if(client == c) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * Returns a list of all clients
     * @return List<CMSClient>
     */
    public List<CMSClient> getClientList() {
        List<CMSClient> returnList = new ArrayList<>();
        for(UUID uuid : this.clients.keySet()) {
            returnList.add(this.clients.get(uuid));
        }
        return returnList;
    }

    /**
     * Returns a client with a specific unique id
     * @param id UUID
     * @return CMSClient
     */
    public CMSClient getClientById(UUID id) {
        return this.clients.get(id);
    }

    // CLOSE
    /**
     * Close all clients. This will not close the server.
     */
    public void closeAll() {
        for(UUID uuid : this.clients.keySet()) {
            CMSClient client = this.clients.get(uuid);
            client.close();
        }
    }

    /**
     * Close the server and all its clients.
     */
    public void close() {
        try {
            this.server.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(this.thread != null) {
            this.thread.stop();
        }
        this.closeAll();
        this.clients.clear();
    }

    /**
     * Returns whether the server is closed or not.
     * @return boolean
     */
    public boolean isClosed() {
        return this.server.isClosed();
    }

    // LISTENERS
    /**
     * Add a server listener
     * @param listener listener
     */
    public void addListener(CMSServerEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove a server listener
     * @param listener listener
     */
    public void removeListener(CMSServerEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get a list of all server listeners
     * @return list of listeners
     */
    public List<CMSServerEventListener> getListeners() {
        return this.listeners;
    }

    // GLOBAL LISTENERS
    /**
     * Add a global client listener
     * @param listener listener
     */
    public void addGlobalListener(CMClientEventListener listener) {
        this.globalClientListeners.add(listener);
    }

    /**
     * Remove a global client listener
     * @param listener listener
     */
    public void removeGlobalListener(CMClientEventListener listener) {
        this.globalClientListeners.remove(listener);
    }

    /**
     * Get a list of global client listeners
     * @return list of listeners
     */
    public List<CMClientEventListener> getGlobalListeners() {
        return this.globalClientListeners;
    }

    // EVENTS
    private void fireEvent(CMSServerEvent event) {
        for(CMSServerEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
