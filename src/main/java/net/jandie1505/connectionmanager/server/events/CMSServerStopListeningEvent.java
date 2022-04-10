package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSServer;

public class CMSServerStopListeningEvent extends CMSServerEvent {
    public CMSServerStopListeningEvent(CMSServer server) {
        super(server);
    }
}
