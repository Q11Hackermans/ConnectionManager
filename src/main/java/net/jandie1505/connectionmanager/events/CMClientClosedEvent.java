package net.jandie1505.connectionmanager.events;

import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.enums.ClientClosedReason;

public class CMClientClosedEvent extends CMClientEvent {
    private ClientClosedReason reason;

    public CMClientClosedEvent(CMClient client, ClientClosedReason reason) {
        super(client);
        this.reason = reason;
    }

    public ClientClosedReason getReason() {
        return this.reason;
    }
}
