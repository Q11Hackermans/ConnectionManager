package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOShortReceivedEvent extends DataIOEvent {
    private short data;

    public DataIOShortReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, short data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public short getData() {
        return this.data;
    }
}
