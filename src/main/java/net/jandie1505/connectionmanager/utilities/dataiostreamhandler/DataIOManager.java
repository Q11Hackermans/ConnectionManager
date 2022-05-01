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
    private final boolean useMultiStreamHandler;
    private boolean opened;

    public DataIOManager(CMSServer server, DataIOType type, boolean useMultiStreamHandler) {
        this.server = server;
        this.handlers = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.type = type;
        this.useMultiStreamHandler = useMultiStreamHandler;
        this.opened = true;
        this.setup();
    }

    private void setup() {
        this.clientEventlistener = event -> {
            if(this.opened) {
                handlers.add(new DataIOStreamHandler(event.getClient(), type, useMultiStreamHandler));
            }
        };

        Thread garbageCollector = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && this.opened && server != null && !server.isClosed()) {
                try {
                    handlers.removeIf(DataIOStreamHandler::isClosed);
                    handlers.remove(null);
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
    }

    /**
     * This will return the DataIOHandler with a specifc UUID.
     * Returns null if the Client is not a CMSClient or if no handler with the specified UUID is found.
     * @param uuid
     * @return DataIOStreamHandler (when found) or null (when not found or CMClient is not a CMSClient)
     */
    public DataIOStreamHandler getHandlerByClientUUID(UUID uuid) {
        for(DataIOStreamHandler handler : this.handlers) {
            if(handler.getClient() instanceof CMSClient && ((CMSClient) handler.getClient()).getUniqueId().equals(uuid)) {
                return handler;
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
