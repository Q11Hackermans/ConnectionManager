package net.jandie1505.connectionmanager.server;

import net.jandie1505.connectionmanager.server.events.CMEvent;

public interface CMEventListener {
    public void onEvent(CMEvent event);
}
