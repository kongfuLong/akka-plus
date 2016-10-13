package akka.enter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.params.BaseParam;
import akka.params.DistributeTellParam;

import java.util.concurrent.CountDownLatch;

/**
 * Created by ruancl@xkeshi.com on 16/10/12.
 */
public class TellSenderWrapper extends MsgSenderWrapper {


    public TellSenderWrapper(ActorRef sender, ActorRef getter, ActorSystem system, CountDownLatch readyToSend) {
        super(sender, getter, system, readyToSend);
    }

    @Override
    public Object sendMsg(BaseParam baseParam) {
        if(readyToSend.getCount()==1){
            try {
                readyToSend.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DistributeTellParam distributeTellParam = (DistributeTellParam) baseParam;
                getGetter().tell(distributeTellParam.getParam(), null);
        return null;
    }
}
