package net.jandie1505.connectionmanager.client;

import net.jandie1505.connectionmanager.client.events.CMCEvent;

public interface CMCEventListener {
    public void onEvent(CMCEvent event);
}
