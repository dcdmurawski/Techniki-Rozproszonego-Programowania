/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;

import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.time.LocalTime;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;



public class Server {
    ExecutorService eServer = Executors.newCachedThreadPool();
    private ServerSocketChannel ssc = null;
    private Selector selector = null;
    boolean serverIsRunning;
    private StringBuffer reqString = new StringBuffer();
    private static final int BSIZE = 1024;
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);
    public  StringBuilder serverLog =new StringBuilder();
    public StringBuilder clientLog;
    private static Charset charset  = Charset.forName("ISO-8859-2");
    private static Pattern reqPatt = Pattern.compile(" +", 3);
    private StringBuffer remsg;
    private static String msg[] = { "Request:", "Result: \n",""};
    private String id;
    private int coPrzychodzi=0;
    private boolean rowbolegle=false;
    private List<String> klucze = new ArrayList<>();
    List<String> loginy =new ArrayList<>();
    public HashMap<SocketChannel,String> loginSocket = new HashMap<>();
    int i =0;

    public Server(String host, int port) {
        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(host, port));
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch(Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
    }

    public void startServer() {
        eServer.execute(()->{
            serverService();
        });
    }

    public void stopServer() {
        serverIsRunning=false;
        eServer.shutdownNow();
    }

    public String getServerLog() {
        return serverLog.toString();
    }
    private void writeResp(SocketChannel sc, String addMsg) throws IOException {
        remsg= new StringBuffer();
        remsg.append(addMsg);

        ByteBuffer buf = charset.encode(CharBuffer.wrap(remsg));
        sc.write(buf);
    }
    public void serverService(){
        serverIsRunning = true;
        Map<String,StringBuilder> mapaLogow = new TreeMap<>();
        while(serverIsRunning == true) {
            try {
                selector.select();

                Set keys = selector.selectedKeys();

                Iterator iter = keys.iterator();
                while (iter.hasNext()) {

                    SelectionKey key = (SelectionKey) iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel cc = ssc.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ);
                        continue;
                    }

                    if (key.isReadable()) {
                        SocketChannel cc = (SocketChannel) key.channel();
                        if (!cc.isOpen())
                            return;
                        reqString.setLength(0);
                        bbuf.clear();
                        try {
                            readLoop:
                            while (true) {
                                int n = cc.read(bbuf);
                                if (n > 0) {
                                    bbuf.flip();
                                    CharBuffer cbuf = charset.decode(bbuf);
                                    while (cbuf.hasRemaining()) {
                                        char c = cbuf.get();
                                        if (c == '\r' || c == '\n') break readLoop;
                                        reqString.append(c);
                                    }
                                }
                            }
                            String[] req = reqPatt.split(reqString, 3);
                            String which = req[0];
                            if(!(which.equals("bye")||which.equals("login"))){

                                try{
                                    String odp="";
                                    odp=(Time.passed(req[0],req[1]));
                                    coPrzychodzi++;
                                    serverLog.append(loginSocket.get(cc) + " request at " + LocalTime.now() + ": " + '"' + req[0] + " " + req[1] + '"' + "\n");
                                    mapaLogow.get(loginSocket.get(cc)).append("Request: " + req[0] + " " + req[1] + "\n" + "Result:" + "\n" + odp + "\n");
                                    writeResp(cc, odp);

                                }catch (Exception e){

                                }
                            }
                            else if (which.equals("bye")) {
                                coPrzychodzi++;

                                serverLog.append(loginSocket.get(cc) + " logged out at " + LocalTime.now()+ "\n");
                                mapaLogow.get(loginSocket.get(cc)).append("logged out" + "\n" + "=== " + loginSocket.get(cc) + " log end ===" + "\n");
                                writeResp(cc, mapaLogow.get(loginSocket.get(cc)).toString());          // - zamknięcie kanału
                                cc.close();                      // i gniazda
                                cc.socket().close();

                            } else if (which.equals("login")) {
                                coPrzychodzi++;
                                if(coPrzychodzi==2){
                                    rowbolegle=true;
                                }
                                loginy.add(req[1]);
                                clientLog = new StringBuilder();
                                mapaLogow.put(req[1],clientLog);
                                loginSocket.put(cc,req[1]);
                                id=req[1];
                                mapaLogow.get(loginSocket.get(cc)).append("=== "+loginSocket.get(cc)+" log start ==="+"\n"+"logged in"+"\n");
                                serverLog.append(loginSocket.get(cc)+" logged in at "+ LocalTime.now()+"\n");
                                i=loginy.size();

                                writeResp(cc,"logged in");
                            } else writeResp(cc,  null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                cc.close();
                                cc.socket().close();
                            } catch (Exception ignore) { }
                        }
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}