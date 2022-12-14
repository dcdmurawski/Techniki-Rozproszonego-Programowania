/**
 *
 *  @author Murawski Dinhchidung S22825
 *
 */

package zad1;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<ChatClient>{
    private ChatClientTask(Callable<ChatClient> callable) {
        super(callable);
    }

    public static ChatClientTask create(ChatClient c, List<String> msgs, int wait) {
        return new ChatClientTask(() -> {

            c.login();
            if (wait != 0)
                Thread.sleep(wait);
            for (String msg : msgs) {
                c.send(msg);
                if (wait != 0)
                    Thread.sleep(wait);
            }
            c.logout();
            if (wait != 0)
                Thread.sleep(wait);
            return c;
        });
    }

    public ChatClient getClient() {
        try {
            return this.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
