package net.jandie1505.connectionmanager.utilities.dataiostreamhandler;

import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events.DataIOEvent;

public interface DataIOEventListener {
    void onEvent(DataIOEvent event);
}
