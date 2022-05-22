package net.jandie1505.connectionmanager.utilities.dataiostreamhandler;

import net.jandie1505.connectionmanager.CMClientEventListener;
import net.jandie1505.connectionmanager.events.CMClientCreatedEvent;
import net.jandie1505.connectionmanager.events.CMClientEvent;
import net.jandie1505.connectionmanager.server.CMSClient;
import net.jandie1505.connectionmanager.server.CMSServer;

import java.util.*;

public class DataIOManager {
    private CMSServer server;
    private final List<DataIOStreamHandler> handlers;
    private final List<DataIOEventListener> listeners;
    private final DataIOType type;
    private final DataIOStreamType inputStreamType;
    private boolean opened;

    public DataIOManager(CMSServer server, DataIOType type, DataIOStreamType inputStreamType) {
        this.server = server;
        this.handlers = Collections.synchronizedList(new ArrayList<>());
        this.listeners = new ArrayList<>();
        this.type = type;
        this.inputStreamType = inputStreamType;
        this.opened = true;
        this.setup();
    }

    private void setup() {
        Thread dataIOManagerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && this.opened && server != null && !server.isClosed()) {
                try {
                    synchronized(this.handlers) {
                        handlers.removeIf(DataIOStreamHandler::isClosed);
                        handlers.remove(null);

                        for(CMSClient client : server.getClientList()) {
                            if(getHandlerByClient(client) == null) {
                                DataIOStreamHandler handler = new DataIOStreamHandler(client, type, inputStreamType);
                                for(DataIOEventListener listener : listeners) {
                                    handler.addEventListener(listener);
                                }
                                handlers.add(handler);
                            }
                        }
                    }
                } catch(Exception e) {
                    Thread.currentThread().interrupt();
                    close();
                    e.printStackTrace();
                }
            }
        });
        dataIOManagerThread.setName("DATAIO-MANAGER- " +  this);
        dataIOManagerThread.start();
    }

    /**
     * This will return the DataIOStreamHandler with a specifc UUID.
     * Returns null if the Client is not a CMSClient or if no handler with the specified UUID is found.
     * @param uuid UUID
     * @return DataIOStreamHandler (when found) or null (when not found or CMClient is not a CMSClient)
     */
    public DataIOStreamHandler getHandlerByClientUUID(UUID uuid) {
        return this.getHandlerByClient(server.getClientById(uuid));
    }

    public DataIOStreamHandler getHandlerByClient(CMSClient client) {
        synchronized(this.handlers) {
            for(DataIOStreamHandler handler : this.handlers) {
                if(handler.getClient() == client) {
                    return handler;
                }
            }
        }
        return null;
    }

    /**
     * Return all DataIOStreamHandlers
     * @return List of DataIOStreamHandlers
     */
    public List<DataIOStreamHandler> getHandlers() {
        return List.copyOf(this.handlers);
    }

    public void addEventListener(DataIOEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeEventListener(int index) {
        this.listeners.remove(index);
    }

    public List<DataIOEventListener> getEventListeners() {
        return List.copyOf(this.listeners);
    }

    public void close() {
        for(DataIOStreamHandler handler : this.handlers) {
            try {
                handler.close();
            } catch(Exception e) {
                System.err.println("[CM] Error while closing handler " + Arrays.toString(e.getStackTrace()));
            }

        }
        this.opened = false;
        this.server = null;
    }

    public boolean isClosed() {
        return !this.opened;
    }
}
