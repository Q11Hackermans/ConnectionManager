package net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.DataIOStreamHandler;

public class DataIOByteReceivedEvent extends DataIOEvent {
    private int data;

    public DataIOByteReceivedEvent(DataIOStreamHandler dataIOStreamHandler, CMClient client, int data) {
        super(dataIOStreamHandler, client);
        this.data = data;
    }

    public int getByte() {
        return data;
    }
}
