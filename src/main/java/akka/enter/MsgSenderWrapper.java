package akka.enter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.msg.Message;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ruancl@xkeshi.com on 16/10/12.
 */
public abstract class MsgSenderWrapper {


    protected ActorRef sender;

    protected List<ActorRef> getters;

    protected ActorSystem system;

    protected CountDownLatch readyToSend;

    public MsgSenderWrapper(ActorRef sender, List<ActorRef> getters, ActorSystem system, CountDownLatch readyToSend) {
        this.sender = sender;
        this.getters = getters;
        this.system = system;
        this.readyToSend = readyToSend;
    }

    public ActorRef getSender() {
        return sender;
    }

    public Optional<List<ActorRef>> getGetters() {
        return Optional.of(getters);
    }

    public ActorSystem getSystem() {
        return system;
    }

    public Object sendMsg(Message message) {
        //防止集群加入动作还未完成就立马发信息  造成信息丢失
        if (readyToSend.getCount() == 1) {
            try {
                readyToSend.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return handleMsg(message);
    }

    public abstract Object handleMsg(Message message);
}
