package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOLongReceivedEvent extends DataIOEvent {
    private long data;

    public DataIOLongReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, long data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public long getData() {
        return this.data;
    }
}
