import net.jandie1505.connectionmanager.CMClient;
import net.jandie1505.connectionmanager.CMClientEventListener;
import net.jandie1505.connectionmanager.client.CMCClient;
import net.jandie1505.connectionmanager.enums.ConnectionBehavior;
import net.jandie1505.connectionmanager.events.CMClientByteReceivedEvent;
import net.jandie1505.connectionmanager.events.CMClientCreatedEvent;
import net.jandie1505.connectionmanager.events.CMClientEvent;
import net.jandie1505.connectionmanager.server.CMSClient;
import net.jandie1505.connectionmanager.server.CMSServer;
import net.jandie1505.connectionmanager.server.CMSServerEventListener;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.*;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events.DataIOEvent;

import java.util.ArrayList;
import java.util.List;

public class Test implements CMClientEventListener, CMSServerEventListener, DataIOEventListener {
    public static void main(String[] args) throws Exception {
        CMSServer server = new CMSServer(25577);
        server.addGlobalListener(new Test());
        server.setDefaultConnectionBehavior(ConnectionBehavior.ACCEPT);
        DataIOManager dataIOManager = new DataIOManager(server, DataIOType.UTF, DataIOStreamType.DEFAULT_TIMED);
        dataIOManager.addEventListener(new Test());
        System.out.println("server created");

        Thread.sleep(3000);

        List<CMClientEventListener> listeners = new ArrayList<>();
        listeners.add(new Test());
        CMCClient client = new CMCClient("127.0.0.1", 25577, listeners);
        DataIOStreamHandler handler = new DataIOStreamHandler(client, DataIOType.UTF, DataIOStreamType.DEFAULT_TIMED);
        handler.addEventListener(new Test());
        System.out.println("client created");

        Thread.sleep(10000);

        handler.writeUTF("client says hello");
        System.out.println("client hello message");

        Thread.sleep(3000);

        for(CMSClient cmsClient : server.getClientList()) {
            dataIOManager.getHandlerByClient(cmsClient).writeUTF("Server says hello");
        }
        System.out.println("server hello message");

        Thread.sleep(3000);

        System.out.println("before server close");
        server.close();
        System.out.println("server closed");

        Thread.sleep(10000);

        client.close();
        System.out.println("client closed");
    }

    @Override
    public void onEvent(CMClientEvent event) {
        if(!(event instanceof CMClientByteReceivedEvent)) {
            System.out.println(event + " " + event.getClient());
        }
    }

    @Override
    public void onEvent(CMSServerEvent event) {
        System.out.println(event + " " + event.getServer());
    }

    @Override
    public void onEvent(DataIOEvent event) {
        System.out.println(event + " " + event.getClient() + " " + event.getHandler());
    }
}
