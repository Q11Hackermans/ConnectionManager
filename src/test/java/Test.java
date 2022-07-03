import net.jandie1505.connectionmanager.CMClientEventListener;
import net.jandie1505.connectionmanager.client.CMCClient;
import net.jandie1505.connectionmanager.enums.ConnectionBehavior;
import net.jandie1505.connectionmanager.events.CMClientByteReceivedEvent;
import net.jandie1505.connectionmanager.events.CMClientEvent;
import net.jandie1505.connectionmanager.server.CMSClient;
import net.jandie1505.connectionmanager.server.CMSServer;
import net.jandie1505.connectionmanager.server.CMSServerEventListener;
import net.jandie1505.connectionmanager.server.events.CMSServerEvent;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.*;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events.DataIOEvent;
import net.jandie1505.connectionmanager.utilities.dataiostreamhandler.events.DataIOUTFReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Test implements CMClientEventListener, CMSServerEventListener, DataIOEventListener {
    private static long timeStart;
    private static long timeEnd;

    public static void main(String[] args) throws Exception {
        restartTest();
    }

    private static void performanceTest() throws Exception {
        CMSServer server = new CMSServer(25577);
        server.addListener(new Test());
        server.addGlobalListener(new Test());
        server.setDefaultConnectionBehavior(ConnectionBehavior.ACCEPT);
        System.out.println("server created");

        Thread.sleep(3000);

        List<CMClientEventListener> listeners = new ArrayList<>();
        listeners.add(new Test());
        CMCClient client1 = new CMCClient("127.0.0.1", 25577, listeners);
        System.out.println("created client 1");

        Thread.sleep(2000);

        CMCClient client2 = new CMCClient("127.0.0.1", 25577, listeners);
        System.out.println("created client 2");

        Thread.sleep(2000);

        CMCClient client3 = new CMCClient("127.0.0.1", 25577, listeners);
        System.out.println("created client 3");

        Thread.sleep(5000);

        server.close();
        client1.close();
        client2.close();
        client3.close();
    }

    private static void timeOfInputStreams() throws Exception {
        timeStart = 0;
        timeEnd = 0;

        CMSServer server = new CMSServer(25577);
        server.addListener(new Test());
        server.addGlobalListener(new Test());
        server.setDefaultConnectionBehavior(ConnectionBehavior.ACCEPT);
        DataIOManager dataIOManager = new DataIOManager(server, DataIOType.UTF, DataIOStreamType.MULTI_STREAM_HANDLER_CONSUMING);
        dataIOManager.addEventListener(new Test());
        System.out.println("server created");

        Thread.sleep(3000);

        List<CMClientEventListener> listeners = new ArrayList<>();
        listeners.add(new Test());
        CMCClient client = new CMCClient("127.0.0.1", 25577, listeners);
        DataIOStreamHandler dataIOStreamHandler = new DataIOStreamHandler(client, DataIOType.UTF, DataIOStreamType.MULTI_STREAM_HANDLER_CONSUMING);
        dataIOStreamHandler.addEventListener(new Test());
        System.out.println("client created");

        Thread.sleep(3000);

        for(CMSClient cmsClient : server.getClientList()) {
            DataIOStreamHandler handler = dataIOManager.getHandlerByClient(cmsClient);
            timeStart = System.currentTimeMillis();
            handler.writeUTF("{\"fields\":[[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201],[0,200,200,201]]}");
        }
        System.out.println("sent hello world message");

        Thread.sleep(10000);

        server.close();
        client.close();
        System.out.println("server and client closed");

        long time = timeEnd - timeStart;

        System.out.println("-------------------- RESULT --------------------\n" +
                "Start time: " + timeStart + "ms\n" +
                "End time: " + timeEnd + "ms\n" +
                "REQUIRED TIME: " + time + "ms\n" +
                "------------------------------------------------\n");
    }

    public static void restartTest() throws Exception {
        System.out.println("started");

        CMSServer server = new CMSServer(26677);

        Thread.sleep(10000);

        int count = 100;
        while (count >= 0 && (!server.isClosed() && server.isOperational())) {
            server.close();

            Thread.sleep(1000);

            server = new CMSServer(26677);
            server.addListener(new Test());
            server.addGlobalListener(new Test());

            Thread.sleep(10000);

            System.out.println("next");

            count--;
        }

        System.out.println("finished");
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
        timeEnd = System.currentTimeMillis();
        System.out.println(event + " " + event.getClient() + " " + event.getHandler());
        if(event instanceof DataIOUTFReceivedEvent) {
            System.out.println(((DataIOUTFReceivedEvent) event).getData());
        }
    }
}
