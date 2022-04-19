package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSPendingClient;
import net.jandie1505.connectionmanager.server.CMSServer;

import java.util.UUID;

public class CMSServerConnectionAttemptEvent extends CMSServerEvent {
    private UUID uuid;
    private CMSPendingClient client;

    public CMSServerConnectionAttemptEvent(CMSServer server, UUID uuid, CMSPendingClient client) {
        super(server);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public CMSPendingClient getClient() {
        return this.client;
    }
}
