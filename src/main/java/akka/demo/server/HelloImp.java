package akka.demo.server;

import akka.test.Hello;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 */
public class HelloImp implements Hello {
    @Override
    public String sayHello() {
        return "hello xiao er bi!!";
    }
}
