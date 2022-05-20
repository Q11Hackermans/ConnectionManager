package net.jandie1505.connectionmanager.utilities.dataiostreamhandler;

import net.jandie1505.connectionmanager.CMClientEventListener;
import net.jandie1505.connectionmanager.events.CMClientCreatedEvent;
import net.jandie1505.connectionmanager.events.CMClientEvent;
import net.jandie1505.connectionmanager.server.CMSClient;
import net.jandie1505.connectionmanager.server.CMSServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataIOManager {
    private CMSServer server;
    private final List<DataIOStreamHandler> handlers;
    private final List<DataIOEventListener> listeners;
    private CMClientEventListener clientEventlistener;
    private final DataIOType type;
    private final DataIOStreamType useMultiStreamHandler;
    private boolean opened;

    public DataIOManager(CMSServer server, DataIOType type, DataIOStreamType useMultiStreamHandler) {
        this.server = server;
        this.handlers = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.type = type;
        this.useMultiStreamHandler = useMultiStreamHandler;
        this.opened = true;
        this.setup();
    }

    private void setup() {
        this.clientEventlistener = new CMClientEventListener() {
            @Override
            public void onEvent(CMClientEvent event) {
                if(opened && event instanceof CMClientCreatedEvent) {
                    DataIOStreamHandler handler = new DataIOStreamHandler(event.getClient(), type, useMultiStreamHandler);
                    for(DataIOEventListener listener : listeners) {
                        handler.addEventListener(listener);
                    }
                    synchronized(handlers) {
                        handlers.add(handler);
                    }
                }
            }
        };

        Thread garbageCollector = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && this.opened && server != null && !server.isClosed()) {
                try {
                    synchronized(this.handlers) {
                        handlers.removeIf(DataIOStreamHandler::isClosed);
                        handlers.remove(null);
                    }
                } catch(Exception e) {
                    Thread.currentThread().interrupt();
                    close();
                    e.printStackTrace();
                }
            }
        });
        garbageCollector.setName("DATAIO-MANAGER-GARBAGECOLLECTOR " +  this);
        garbageCollector.setDaemon(true);
        garbageCollector.start();

        this.server.addGlobalListener(this.clientEventlistener);
    }

    /**
     * This will return the DataIOStreamHandler with a specifc UUID.
     * Returns null if the Client is not a CMSClient or if no handler with the specified UUID is found.
     * @param uuid UUID
     * @return DataIOStreamHandler (when found) or null (when not found or CMClient is not a CMSClient)
     */
    public DataIOStreamHandler getHandlerByClientUUID(UUID uuid) {
        synchronized(this.handlers) {
            for(DataIOStreamHandler handler : this.handlers) {
                if(handler.getClient() instanceof CMSClient && ((CMSClient) handler.getClient()).getUniqueId().equals(uuid)) {
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
        if(this.server != null) {
            this.server.getGlobalListeners().remove(clientEventlistener);
        }
        for(DataIOStreamHandler handler : this.handlers) {
            handler.close();
        }
        this.opened = false;
        this.server = null;
    }

    public boolean isClosed() {
        return !this.opened;
    }
}
