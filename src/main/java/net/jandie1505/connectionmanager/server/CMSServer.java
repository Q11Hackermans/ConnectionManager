package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.actions.CMSClientAction;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStartListeningEvent;
import net.jandie1505.connectionmanager.server.events.CMSServerStopListeningEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CMSServer {
    // BASIC THINGS
    private ServerSocket server;
    private List<CMSClient> clients;
    Thread thread;

    // SERVER SPECIFIC THINGS
    private List<CMSServerEventListener> listeners;

    // CLIENT SPECIFIC THINGS
    private List<CMSClientEventListener> globalClientListeners;
    private List<CMSClientAction> globalClientActions;

    // CODE
    public CMSServer(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.clients = new ArrayList<>();

        this.listeners = new ArrayList<>();

        this.globalClientListeners = new ArrayList<>();
        this.globalClientActions = new ArrayList<>();
    }

    /**
     * Starts listening for connection attempts and accepting them
     */
    public void startListen() {
        if(this.thread != null && this.thread.isAlive()) {
            this.thread.stop();
        }

        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        CMSClient client = new CMSClient(server.accept());
                        clients.add(client);
                        for(CMSClientEventListener listener : globalClientListeners) {
                            client.addEventListener(listener);
                        }
                        for(CMSClientAction action : globalClientActions) {
                            client.addAction(action);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // This will remove all clients which are disconnected
        Thread garbageCollection = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    clients.remove(null);
                    clients.removeIf(CMSClient::isClosed);
                }
            }
        });
        garbageCollection.setDaemon(true);
        garbageCollection.start();

        fireEvent(new CMSServerStartListeningEvent(this));
    }

    /**
     * Returns a list of all clients
     * @return List<CMSClient>
     */
    public List<CMSClient> getClients() {
        return this.clients;
    }

    /**
     * Returns a client with a specific unique id
     * @param id UUID
     * @return CMSClient
     */
    public CMSClient getClientById(UUID id) {
        for(CMSClient client : clients) {
            if(client.getUniqueId().equals(id)) {
                return client;
            }
        }
        return null;
    }

    /**
     * Stops listening for connection attempts and accepting them
     */
    public void stopListen() {
        this.thread.stop();
        this.thread = null;
        this.fireEvent(new CMSServerStopListeningEvent(this));
    }

    /**
     * Close all clients
     * @throws IOException IOException
     */
    public void closeAll() throws IOException {
        for(CMSClient client : this.clients) {
            client.close();
        }
    }

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
    public List<CMSServerEventListener> listListeners() {
        return this.listeners;
    }

    /**
     * Add a global client listener
     * @param listener listener
     */
    public void addGlobalListener(CMSClientEventListener listener) {
        this.globalClientListeners.add(listener);
    }

    /**
     * Remove a global client listener
     * @param listener listener
     */
    public void removeGlobalListener(CMSClientEventListener listener) {
        this.globalClientListeners.remove(listener);
    }

    /**
     * Get a list of global client listeners
     * @return list of listeners
     */
    public List<CMSClientEventListener> listGlobalListeners() {
        return this.globalClientListeners;
    }

    /**
     * Add a global client action
     * @param listener action
     */
    public void addGlobalAction(CMSClientAction listener) {
        this.globalClientActions.add(listener);
    }

    /**
     * Remove a global client action
     * @param listener action
     */
    public void removeGlobalAction(CMSClientAction listener) {
        this.globalClientActions.remove(listener);
    }

    /**
     * Get a list of global client actions
     * @return list of actions
     */
    public List<CMSClientAction> listGlobalAction() {
        return this.globalClientActions;
    }

    private void fireEvent(CMSServerEvent event) {
        for(CMSServerEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
