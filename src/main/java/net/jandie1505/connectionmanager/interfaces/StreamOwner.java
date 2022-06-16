package net.jandie1505.connectionmanager.interfaces;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.events.CMClientEvent;

public interface StreamOwner {
    boolean isClosed();
    void fireEvent(CMClientEvent event);
    CMClient getEventClient();
}
