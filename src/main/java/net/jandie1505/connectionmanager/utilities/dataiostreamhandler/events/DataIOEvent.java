package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public abstract class DataIOEvent {
    private DataIOStreamHandler dataIOStreamHandler;
    private CMClient client;

    public DataIOEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client) {
        this.dataIOStreamHandler = dataIOStreamHandler;
        this.client = client;
    }
}
