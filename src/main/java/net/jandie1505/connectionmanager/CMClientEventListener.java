package net.jandie1505.connectionmanager;

import net.jandie1505.connectionmanager.events.CMClientEvent;

public interface CMClientEventListener {
    void onEvent(CMClientEvent event);
}
