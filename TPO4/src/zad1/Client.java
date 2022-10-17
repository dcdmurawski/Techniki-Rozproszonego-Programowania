/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    private static Charset charset = Charset.forName("ISO-8859-2");
    private String host;
    private int port;
    private String id;
    private SocketChannel socketChannel;
    private ByteBuffer bbIn;

    public Client(String host, int port, String id) {
        this.host=host;
        this.port=port;
        this.id=id;
    }

    public void connect() {
        try{
            socketChannel=SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
            socketChannel.configureBlocking (false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String send(String str) {
        bbIn = ByteBuffer.allocateDirect(1024);
        str+="\n";
        StringBuffer res=new StringBuffer();
        try {
            CharBuffer cb= CharBuffer.wrap(str);
            ByteBuffer bb = charset.encode(cb);
            socketChannel.write(bb);
            while (true){
                bbIn.clear();
                int read = socketChannel.read(bbIn);
                if (read==0){
                    Thread.sleep(50);
                    continue;
                }else if(read==-1){
                    socketChannel.close();
                    break;
                }else {
                    bbIn.flip();
                    cb=charset.decode(bbIn);
                    res.append(cb);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res.toString();
    }

    public String getId() {
        return id;
    }
}