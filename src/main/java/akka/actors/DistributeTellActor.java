package akka.actors;

import akka.msg.Status;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 */
public class DistributeTellActor extends AbstractActor {

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof Status) {
            System.out.println("```````````msg:" + getSender().path() + ((Status) o).getMsg());
        } else {
            unhandled(o);
        }
    }
}
