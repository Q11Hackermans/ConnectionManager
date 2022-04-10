package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSClient;

public class CMSClientCreatedEvent extends CMSClientEvent {
    public CMSClientCreatedEvent(CMSClient client) {
        super(client);
    }
}
