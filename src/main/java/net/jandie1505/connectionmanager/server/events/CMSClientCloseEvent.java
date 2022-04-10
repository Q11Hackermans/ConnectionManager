package net.jandie1505.connectionmanager.server.events;

import net.jandie1505.connectionmanager.enums.CloseEventReason;
import net.jandie1505.connectionmanager.server.CMSClient;

public class CMSClientCloseEvent extends CMSClientEvent {
    CloseEventReason reason;

    public CMSClientCloseEvent(CMSClient client, CloseEventReason reason) {
        super(client);
        this.reason = reason;
    }

    public CloseEventReason getReason() {
        return this.reason;
    }
}
