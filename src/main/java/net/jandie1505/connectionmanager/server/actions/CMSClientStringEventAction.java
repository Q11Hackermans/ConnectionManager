package net.jandie1505.connectionmanager.server.actions;

import net.jandie1505.connectionmanager.server.CMSClient;
import net.jandie1505.connectionmanager.server.events.CMSClientStringInputReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This action will fire a CMSClientStringInputReceivedEvent if a text message was received
 */
public class CMSClientStringEventAction implements CMSClientAction {

    @Override
    public void run(CMSClient client) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String input = reader.readLine();
            if(input != null && !input.trim().equals("")) {
                client.fireEvent(new CMSClientStringInputReceivedEvent(client, input));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
