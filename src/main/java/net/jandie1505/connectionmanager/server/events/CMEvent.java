package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMServerClient;

/**
 * Basic event for CMEventListener
 */
public class CMEvent {
    CMServerClient client;

    public CMEvent(CMServerClient client) {
        this.client = client;
    }
}
