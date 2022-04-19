package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSPendingClient;
import net.jandie1505.connectionmanager.server.CMSServer;

import java.util.UUID;

public class CMSServerConnectionRefusedEvent extends CMSServerEvent {
    private UUID uuid;
    private CMSPendingClient client;

    public CMSServerConnectionRefusedEvent(CMSServer server, UUID uuid, CMSPendingClient client) {
        super(server);
        this.uuid = uuid;
        this.client = client;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public CMSPendingClient getClient() {
        return this.client;
    }
}
