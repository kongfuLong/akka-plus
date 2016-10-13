package akka.rpc;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.actors.RpcServerActor;
import akka.enter.AkkaContext;
import akka.msg.RpcEntity;
import akka.msg.RpcResult;
import akka.pattern.Patterns;
import akka.remote.routing.RemoteRouterConfig;
import akka.routing.RandomPool;
import akka.util.Timeout;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 * 调用方 调代理类
 */
public class ProxyIntecept implements MethodInterceptor {


    private final Long timeOut = 3000l;

    private final String RPCROLENAME = "calculateModule";

    private Class clazz;

    private ActorSystem system;

    public ProxyIntecept(Class clazz, ActorSystem system) {
        this.clazz = clazz;
        this.system = system;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        RpcEntity rpcEntity = new RpcEntity(clazz, method.getName(), objects);
        List<Address> addresses = AkkaContext.addressMap.get(RPCROLENAME);
        Address[] arr = addresses.toArray(new Address[addresses.size()]);

        final ActorRef ref = system.actorOf(new RemoteRouterConfig(new RandomPool(arr.length), arr).props(Props.create(RpcServerActor.class)));
        FiniteDuration finiteDuration = Duration.create(timeOut, TimeUnit.MILLISECONDS);
        final Timeout timeout = new Timeout(finiteDuration);
        final Future future = Patterns.ask(ref, rpcEntity, timeout);

        RpcResult res = (RpcResult) Await.result(future, finiteDuration);

        return res.getO();
    }
}
