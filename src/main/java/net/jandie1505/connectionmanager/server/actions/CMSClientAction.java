package net.jandie1505.connectionmanager.server.actions;

import net.jandie1505.connectionmanager.server.CMSClient;

/**
 * Basic interface for client event actions
 */
public interface CMSClientAction {
    public void run(CMSClient client);
}
