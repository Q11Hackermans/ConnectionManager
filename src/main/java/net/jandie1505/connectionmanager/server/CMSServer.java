package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.actions.CMSClientAction;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
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
        this.clients = new ArrayList<>();
        this.server = new ServerSocket(port);
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

    private void fireEvent(CMSServerEvent event) {
        for(CMSServerEventListener listener : this.listeners) {
            listener.onEvent(event);
        }
    }
}
