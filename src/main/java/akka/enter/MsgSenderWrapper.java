package akka.enter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.params.BaseParam;

import java.util.concurrent.CountDownLatch;

/**
 * Created by ruancl@xkeshi.com on 16/10/12.
 */
public abstract class MsgSenderWrapper {


    protected ActorRef sender;

    protected ActorRef getter;

    protected ActorSystem system;

    protected CountDownLatch readyToSend;

    public MsgSenderWrapper(ActorRef sender, ActorRef getter, ActorSystem system,CountDownLatch readyToSend) {
        this.sender = sender;
        this.getter = getter;
        this.system = system;
        this.readyToSend = readyToSend;
    }



    public ActorRef getSender() {
        return sender;
    }

    public ActorRef getGetter() {
        return getter;
    }

    public ActorSystem getSystem() {
        return system;
    }

    public abstract Object sendMsg(BaseParam baseParam);
}
