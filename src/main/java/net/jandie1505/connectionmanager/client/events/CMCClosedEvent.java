package net.jandie1505.connectionmanager.client.events;

import net.jandie1505.connectionmanager.client.CMCClient;

public class CMCClosedEvent extends CMCEvent {
    public CMCClosedEvent(CMCClient client) {
        super(client);
    }
}
