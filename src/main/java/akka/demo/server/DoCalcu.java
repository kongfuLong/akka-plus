package akka.demo.server;

import akka.actors.AbstractActor;
import akka.anntations.Actor;
import akka.anntations.AkkaRpcRef;
import akka.msg.Message;
import akka.test.Hello;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ruancl@xkeshi.com on 16/9/30.
 */
@Actor(name = "greeting")
public class DoCalcu extends AbstractActor {

    @Override
    public void handleMsg(Message message) {
        System.out.println(new Date() + "进来了string" + message + " thread:" + Thread.currentThread().getId() + "--------------");

    }
}
