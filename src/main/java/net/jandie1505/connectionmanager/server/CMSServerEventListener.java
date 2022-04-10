package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.events.CMSServerEvent;

public interface CMSServerEventListener {
    public void onEvent(CMSServerEvent event);
}
