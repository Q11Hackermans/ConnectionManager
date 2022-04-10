package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSServer;

public class CMSServerStartListeningEvent extends CMSServerEvent {
    public CMSServerStartListeningEvent(CMSServer server) {
        super(server);
    }
}
