package akka.demo.server;


import akka.test.Hello;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by ruancl@xkeshi.com on 16/9/29.
 *
 */

public class TestMain {

    public static void main(String[] args) {
        //随spring启动
       /* AkkaContext.createSystem(2551, "EsbSystem", "calculateModule",false,false)
        .register(DoCalcu.class,"greeting");*/
       // AkkaContext.addBean(Hello.class, new HelloImp());
       // ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

    /*    Map<String,List<String>> map = new HashMap<>();
        map.put("a", Arrays.asList("b"));
        List<String> list = map.get("a");
        list = Arrays.asList("c");
        map.put("a",list);
        System.out.println(map.get("a").get(0));*/
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        Hello hello = context.getBean(Hello.class);
        System.out.println(hello.sayHello());
     }

}
