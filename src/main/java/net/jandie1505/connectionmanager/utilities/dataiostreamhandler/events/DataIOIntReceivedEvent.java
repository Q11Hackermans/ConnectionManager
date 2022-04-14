package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOIntReceivedEvent extends DataIOEvent {
    private int data;

    public DataIOIntReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, int data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public int getData() {
        return this.data;
    }
}
