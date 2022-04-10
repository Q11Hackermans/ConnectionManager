package net.jandie1505.connectionmanager.client.events;

import net.jandie1505.connectionmanager.client.CMCClient;

public class CMCEvent {
    private CMCClient client;

    public CMCEvent(CMCClient client) {
        this.client = client;
    }

    public CMCClient getClient() {
        return this.client;
    }
}
