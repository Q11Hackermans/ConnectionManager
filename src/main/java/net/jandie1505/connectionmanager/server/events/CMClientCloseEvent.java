package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMServerClient;

public class CMClientCloseEvent extends CMEvent {

    public CMClientCloseEvent(CMServerClient client) {
        super(client);
    }
}
