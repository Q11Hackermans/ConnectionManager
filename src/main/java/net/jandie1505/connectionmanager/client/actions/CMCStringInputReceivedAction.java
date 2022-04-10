package net.jandie1505.connectionmanager.client.actions;

import net.jandie1505.connectionmanager.client.CMCClient;
import net.jandie1505.connectionmanager.client.events.CMCStringInputReceivedEvent;
import net.jandie1505.connectionmanager.server.events.CMSClientStringInputReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CMCStringInputReceivedAction implements CMCAction {
    @Override
    public void run(CMCClient client) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String input = reader.readLine();
            if(input != null && !input.trim().equals("")) {
                client.fireEvent(new CMCStringInputReceivedEvent(client, input));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
