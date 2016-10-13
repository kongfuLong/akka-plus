package akka.demo.server;

import akka.actors.AbstractActor;

import java.util.Date;

/**
 * Created by ruancl@xkeshi.com on 16/9/30.
 */
public class DoCalcu extends AbstractActor {
    @Override
    public void onReceive(Object o) throws Throwable {
        System.out.println(new Date() + "进来了string" + o + " thread:" + Thread.currentThread().getId() + "--------------");
        if (o instanceof String) {
            //Thread.sleep(5000);
            // getSender().tell(Double.parseDouble((String)o)*2+"",getSelf());
            //getSender().tell(new Status("处理完毕"), getSelf());
        }
    }
}
