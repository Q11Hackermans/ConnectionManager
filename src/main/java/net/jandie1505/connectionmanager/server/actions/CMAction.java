package net.jandie1505.connectionmanager.server.actions;

import net.jandie1505.connectionmanager.server.CMServerClient;

public interface CMAction {
    public void run(CMServerClient client);
}
