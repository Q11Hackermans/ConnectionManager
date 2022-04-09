package net.jandie1505.connectionmanager.server.actions;

import net.jandie1505.connectionmanager.server.CMServerClient;
import net.jandie1505.connectionmanager.server.events.CMStringInputReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CMStringEventAction implements CMAction {

    @Override
    public void run(CMServerClient client) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String input = reader.readLine();
        if(input != null && !input.trim().equals("")) {
            client.fireEvent(new CMStringInputReceivedEvent(client, input));
        }
    }
}
