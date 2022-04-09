package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMServerClient;

public class CMClientCreatedEvent extends CMEvent {
    public CMClientCreatedEvent(CMServerClient client) {
        super(client);
    }
}
