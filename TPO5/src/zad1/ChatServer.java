/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public class ChatServer {

    private final InetSocketAddress address;
    private Thread serverThread;
    private ServerSocketChannel channel;
    private final StringBuilder serverLog = new StringBuilder();
    private final Map<SocketChannel, String> clients = new HashMap<>();;

    public ChatServer(String host, int port) {
        address = new InetSocketAddress(host, port);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socket = socketChannel.accept();
        socket.configureBlocking(false);
        socket.register(key.selector(), OP_READ);
    }

    private StringBuilder requestHandler(SocketChannel clientSocket, String str) throws IOException {
        StringBuilder response = new StringBuilder();

        if (str.matches("log in .+")) {
            clients.put(clientSocket, str.substring(7));

            serverLog.append(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))).append(" ")
                    .append(str.substring(7)).append(" logged in").append("\n");

            response.append(str.substring(7)).append(" logged in").append("\n");
        } else if (str.matches("log out")) {
            serverLog.append(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))).append(" ")
                    .append(clients.get(clientSocket)).append(" logged out").append("\n");

            response.append(clients.get(clientSocket)).append(" logged out").append("\n");

            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(response.toString());
            clientSocket.write(byteBuffer);

            clients.remove(clientSocket);
        } else {
            serverLog.append(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss.SSS"))).append(" ")
                    .append(clients.get(clientSocket)).append(": ").append(str).append("\n");

            response.append(clients.get(clientSocket)).append(": ").append(str).append("\n");
        }
        return response;
    }

    public void startServer() {
        serverThread = new Thread(() -> {
            try {

                channel = ServerSocketChannel.open();
                channel.socket().bind(address);
                channel.configureBlocking(false);

                Selector selector = Selector.open();
                channel.register(selector, OP_ACCEPT);

                while (!serverThread.isInterrupted()) {

                    selector.select();

                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();

                    while(iterator.hasNext()) {

                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isValid()) {

                            if (key.isAcceptable()) {
                                accept(key);

                            }

                            if (key.isReadable()) {

                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(2048);
                                StringBuilder clientRequest = new StringBuilder();

                                int readBytes = 0;
                                do {
                                    readBytes = clientChannel.read(buffer);
                                    buffer.flip();
                                    clientRequest.append(StandardCharsets.UTF_8.decode(buffer).toString());
                                    buffer.clear();
                                    readBytes = clientChannel.read(buffer);
                                } while (readBytes != 0);

                                String[] req = clientRequest.toString().split("#");
                                for(String r : req) {
                                    String res = requestHandler(clientChannel, r).toString();
                                    for (Map.Entry<SocketChannel, String> entry : clients.entrySet()) {
                                        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(res);
                                        entry.getKey().write(byteBuffer);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
        System.out.println("Server started\n");
    }

    public void stopServer() {
        serverThread.interrupt();
        System.out.println("Server stopped");
    }

    public String getServerLog() {
        return serverLog.toString();
    }
}
