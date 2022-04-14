package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOUTFReceivedEvent extends DataIOEvent {
    private String data;

    public DataIOUTFReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, String data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public String getData() {
        return this.data;
    }
}
