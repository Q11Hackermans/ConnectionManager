package net.jandie1505.connectionmanager.client.utilities;

import net.jandie1505.connectionmanager.client.CMCClient;

import java.io.IOException;
import java.io.PrintWriter;

public class CMCStringMessageSender {
    private CMCClient client;
    private PrintWriter out;

    public CMCStringMessageSender(CMCClient client) throws IOException {
        this.client = client;
        out = new PrintWriter(this.client.getSocket().getOutputStream(), true);
    }

    public CMCClient getClient() {
        return this.client;
    }

    public void sendStringMessage(String message) {
        out.println(message);
    }

    public static void sendMessage(CMCClient client, String message) throws IOException {
        CMCStringMessageSender messageSender = new CMCStringMessageSender(client);
        messageSender.sendStringMessage(message);
    }
}
