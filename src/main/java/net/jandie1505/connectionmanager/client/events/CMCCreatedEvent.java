package net.jandie1505.connectionmanager.client.events;

import net.jandie1505.connectionmanager.client.CMCClient;

public class CMCCreatedEvent extends CMCEvent {
    public CMCCreatedEvent(CMCClient client) {
        super(client);
    }
}
