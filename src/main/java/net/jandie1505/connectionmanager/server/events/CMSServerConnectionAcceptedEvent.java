package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSClient;
import net.jandie1505.connectionmanager.server.CMSServer;

public class CMSServerConnectionAcceptedEvent extends CMSServerEvent {
    private CMSClient client;

    public CMSServerConnectionAcceptedEvent(CMSServer server, CMSClient client) {
        super(server);
        this.client = client;
    }

    public CMSClient getClient() {
        return this.client;
    }
}
