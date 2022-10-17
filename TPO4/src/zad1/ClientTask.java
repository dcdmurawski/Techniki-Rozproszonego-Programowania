/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.*;

public class ClientTask extends FutureTask<String> {

    private static String log = "";

    private ClientTask(Callable<String> callable) {
        super(callable);
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        Callable<String> callable = ()->{
            c.connect();
            c.send("login "+c.getId());
            for(String r : reqList){
                String res = c.send(r);
                if(showRes)
                    System.out.println(res);
            }
            String l = c.send("bye and log transfer");
            log+=l;
            return log;
        };
        return new ClientTask(callable);
    }
}