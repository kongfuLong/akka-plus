package akka.demo.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ruancl@xkeshi.com on 16/10/25.
 */
@EnableAutoConfiguration
@ComponentScan
@RestController
public class Main {

    @RequestMapping("/hehe")
    public String hello(@RequestParam("a")String a){
        System.out.println(a+"--------------");
        return a;
    }

    public static void main(String[] args) {

        SpringApplication.run(Main.class,args);
    }
}
