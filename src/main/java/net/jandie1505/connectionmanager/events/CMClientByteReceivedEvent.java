package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;

public class CMClientByteReceivedEvent extends CMClientEvent {
    private int data;

    public CMClientByteReceivedEvent(CMClient client, int data) {
        super(client);
        this.data = data;
    }

    /**
     * Returns the byte that was received (int 0-255)
     * @return byte
     */
    public int getData() {
        return this.data;
    }
}
