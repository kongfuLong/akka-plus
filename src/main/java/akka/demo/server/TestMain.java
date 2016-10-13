package akka.demo.server;

import akka.enter.AkkaContext;

/**
 * Created by ruancl@xkeshi.com on 16/9/29.
 *
 */
public class TestMain {

    public static void main(String[] args) {
        AkkaContext.createSystem(2551, "EsbSystem", "calculateModule",false)
        .register(DoCalcu.class,"greeting");
       // AkkaContext.addBean(Hello.class, new HelloImp());

        System.out.println("cluster2551:");
    }
}
