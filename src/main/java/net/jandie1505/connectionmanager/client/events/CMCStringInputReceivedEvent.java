package net.jandie1505.connectionmanager.client.events;

import net.jandie1505.connectionmanager.client.CMCClient;

public class CMCStringInputReceivedEvent extends CMCEvent {
    private String input;

    public CMCStringInputReceivedEvent(CMCClient client, String input) {
        super(client);
        this.input = input;
    }

    public String getInput() {
        return this.input;
    }
}
