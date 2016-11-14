package akka.enter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.msg.Message;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ruancl@xkeshi.com on 16/10/12.
 */
public class TellSenderWrapper extends MsgSenderWrapper {


    public TellSenderWrapper(ActorRef sender, List<ActorRef> getters, ActorSystem system, CountDownLatch readyToSend) {
        super(sender, getters, system, readyToSend);
    }

    @Override
    public Object handleMsg(Message message) {
        getGetters().get().forEach(o->o.tell(message, null));
        return null;
    }
}
