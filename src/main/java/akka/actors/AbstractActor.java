package akka.actors;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.msg.Constant;
import akka.msg.Message;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 * Created by ruancl@xkeshi.com on 16/10/12.
 * 自定义actor都来继承该抽象类  此类上面可以做一些请求的信息包装  拆包  监控等扩展
 */
public abstract class AbstractActor extends UntypedActor {


    @Override
    public void onReceive(Object o) throws Throwable {
        if(o instanceof Message){
            handleMsg((Message) o);
        }else {
            unhandled(o);
        }
    }

    public abstract void handleMsg(Message message);
}
