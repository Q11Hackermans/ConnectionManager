package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSClient;

/**
 * Basic event for CMEventListener
 */
public class CMSClientEvent {
    private CMSClient client;

    public CMSClientEvent(CMSClient client) {
        this.client = client;
    }

    /**
     * Returns the client the event is fired from
     * @return CMSClient
     */
    public CMSClient getClient() {
        return this.client;
    }
}
