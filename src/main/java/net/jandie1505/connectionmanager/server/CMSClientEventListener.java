package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.events.CMSClientEvent;

public interface CMSClientEventListener {
    public void onEvent(CMSClientEvent event);
}
