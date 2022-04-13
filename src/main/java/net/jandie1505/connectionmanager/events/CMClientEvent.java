package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;

public abstract class CMClientEvent {
    private CMClient client;

    public CMClientEvent(CMClient client) {
        this.client = client;
    }

    /**
     * Returns the client the event is fired from
     * @return CMClient
     */
    public CMClient getClient() {
        return this.client;
    }
}
