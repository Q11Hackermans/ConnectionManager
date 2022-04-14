package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.CMClientEventListener;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStartListeningEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStopListeningEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * Server (CMS = ConnectionManager Server)
 */
public class CMSServer {
    // BASIC THINGS
    private ServerSocket server;
    private Map<UUID, CMSClient> clients;
    Thread thread;

    // SERVER SPECIFIC THINGS
    private List<CMSServerEventListener> listeners;

    // CLIENT SPECIFIC THINGS
    private List<CMClientEventListener> globalClientListeners;

    // SETUP
    public CMSServer(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.clients = new HashMap<>();

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
        garbageCollection.setDaemon(true);
        garbageCollection.start();
    }

    // LISTENER THREAD
    /**
     * Starts listening for connection attempts and accepting them
     */
    public void startListen() {
        if(this.thread != null && this.thread.isAlive()) {
            this.thread.stop();
        }

        this.thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !this.server.isClosed()) {
                try {
                    clients.put(this.getRandomUniqueId(), new CMSClient(server.accept(), this, globalClientListeners));
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                    this.fireEvent(new CMSServerStopListeningEvent(this));
                    e.printStackTrace();
                }
            }
        });
        this.thread.start();

        fireEvent(new CMSServerStartListeningEvent(this));
    }

    /**
     * Stops listening for connection attempts and accepting them
     */
    public void stopListen() {
        if(this.thread != null) {
            this.thread.stop();
        }
        this.thread = null;
        this.fireEvent(new CMSServerStopListeningEvent(this));
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
