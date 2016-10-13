package akka.demo.client;

import akka.enter.AkSystem;
import akka.enter.AkkaContext;
import akka.enter.MsgSenderWrapper;
import akka.params.DistributeTellParam;

import java.util.Arrays;

/**
 * Created by ruancl@xkeshi.com on 16/9/29.
 */
public class TestMain {

    public static void main(String[] args) {

        final AkSystem aksystem = AkkaContext.createSystem(2553, "EsbSystem", "customRoleName",false);
        /**
         * 单向消息发送
         */
        Iterable<String> routeesPaths = Arrays.asList("/user/greeting");
        DistributeTellParam param = new DistributeTellParam("hello i m jackie chan", "calculateModule", "customTaskName",routeesPaths);
        //以任务名为单位创建一个发射器
        MsgSenderWrapper senderWrapper = aksystem.senderCreate(param);
        for(int i=1;i<10;i++){
            param.setParam("hello i m jackie chan"+i);
            senderWrapper.sendMsg(param);
        }


        /**
         * rpc远程调用
         */
          /*  Hello hello = system.proxy(Hello.class);
            for (int i = 0; i < 50; i++) {
                Long now = System.currentTimeMillis();
                System.out.println(hello.sayHello()+": "+(System.currentTimeMillis()-now)+"ms");
            }*/

        /**
         * 全双工  集群任务+结果返回统一处理
         */
            /*DistributeAskParam askParam = new DistributeAskParam.Builder("calculateModule",
                    (it) -> {
                        Double total = new Double(0);
                        while (it.hasNext()) {
                            final String x = (String) it.next();
                            total += Double.parseDouble(x);
                        }
                        System.out.println("返回值得总和:" + total);
                        return total;
                    }
                    ,
                    "calculate",
                    DoCalcu.class)
                    .setCut((serverSize, address, index) -> index * 2 + 100 + "")
                    .setTimeLimit(10000l).build();
            MsgSenderWrapper senderWrapper2 = system.senderCreate(param);
            senderWrapper2.sendMsg(askParam);
*/
    }




}
