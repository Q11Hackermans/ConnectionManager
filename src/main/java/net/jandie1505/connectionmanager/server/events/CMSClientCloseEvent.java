package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.server.CMSClient;

public class CMSClientCloseEvent extends CMSClientEvent {

    public CMSClientCloseEvent(CMSClient client) {
        super(client);
    }
}
