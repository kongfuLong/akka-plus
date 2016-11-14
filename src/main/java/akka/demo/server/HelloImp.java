package akka.demo.server;

import akka.anntations.Actor;
import akka.anntations.AkkaRpc;
import akka.test.Hello;
import org.springframework.stereotype.Service;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 */
@Service
@AkkaRpc
public class HelloImp implements Hello {
    @Override
    public String sayHello() {
        return "hello xiao er bi!!";
    }
}
