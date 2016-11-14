package akka.actors;

import akka.msg.Message;
import akka.msg.Status;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 */
public class DistributeTellActor extends AbstractActor {


    @Override
    public void handleMsg(Message message) {
        System.out.println("```````````msg:" + getSender().path() + message);
    }


}
