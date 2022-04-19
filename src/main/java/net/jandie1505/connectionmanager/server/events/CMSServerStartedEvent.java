package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSServer;

public class CMSServerStartedEvent extends CMSServerEvent {
    public CMSServerStartedEvent(CMSServer server) {
        super(server);
    }
}
