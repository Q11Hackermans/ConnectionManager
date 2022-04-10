package net.jandie1505.connectionmanager.client.events;

import net.jandie1505.connectionmanager.client.CMCClient;
import net.jandie1505.connectionmanager.enums.CloseEventReason;

public class CMCClosedEvent extends CMCEvent {
    CloseEventReason reason;

    public CMCClosedEvent(CMCClient client, CloseEventReason reason) {
        super(client);
        this.reason = reason;
    }

    public CloseEventReason getReason() {
        return this.reason;
    }
}
