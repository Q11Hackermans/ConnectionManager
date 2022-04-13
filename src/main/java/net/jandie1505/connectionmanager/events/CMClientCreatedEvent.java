package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;

public class CMClientCreatedEvent extends CMClientEvent {
    public CMClientCreatedEvent(CMClient client) {
        super(client);
    }
}
