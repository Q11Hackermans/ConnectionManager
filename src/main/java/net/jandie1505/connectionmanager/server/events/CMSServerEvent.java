package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSServer;

public abstract class CMSServerEvent {
    private CMSServer server;

    public CMSServerEvent(CMSServer server) {
        this.server = server;
    }

    /**
     * Returns the server the event is fired from
     * @return CMSServer
     */
    public CMSServer getServer() {
        return this.server;
    }
}
