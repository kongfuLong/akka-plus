package akka.enter;

import akka.actor.*;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.params.BaseParam;
import akka.params.DistributeAskParam;
import akka.params.Result;
import akka.params.TaskCut;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;
import akka.remote.RemoteScope;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Created by ruancl@xkeshi.com on 16/10/12.
 */
public class AskSenderWrapper<A> extends MsgSenderWrapper {


    public AskSenderWrapper(ActorRef sender, ActorRef getter, ActorSystem system, CountDownLatch readyToSend) {
        super(sender, getter, system, readyToSend);
    }

    @Override
    public Object sendMsg(BaseParam baseParam) {
        DistributeAskParam distributeAskParam = (DistributeAskParam) baseParam;
        Class excutorClass = distributeAskParam.getExcuterClazz();
        Result<A> result = distributeAskParam.getResult();
        TaskCut taskCut = distributeAskParam.getTaskCut();
        Long time = distributeAskParam.getTimeUp();
        String roleName = distributeAskParam.getRoleName();

        List<Address> addresses = AkkaContext.addressMap.get(roleName);
        FiniteDuration finiteDuration = Duration.create(time, TimeUnit.MILLISECONDS);
        final Timeout timeout = new Timeout(finiteDuration);
        final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
        int size = addresses.size();
        final ExecutionContext ec = getSystem().dispatcher();
        for (int i = 0; i < addresses.size(); i++) {
            Address address = addresses.get(i);
            ActorRef actor1 = getSystem().actorOf(Props.create(excutorClass).withDeploy(new Deploy(new RemoteScope(address))));
            //ActorRef actor1 = getContext().system().actorOf(Props.create(LookUpActor.class,address.toString()+"/user/cal/"),"remote");
            Object msg = "default msg";
            if (taskCut != null) {
                msg = taskCut.cut(size, address, i);
            }
            final Future future = Patterns.ask(actor1, msg, timeout);
            future.onFailure(new OnFailure() {
                @Override
                public void onFailure(Throwable throwable) throws Throwable {
                    System.out.println("failure------------------" + throwable);
                    if (throwable instanceof AskTimeoutException) {
                        System.out.println(address.toString() + ":链接超时 ");
                    }
                }
            }, ec);
            future.onSuccess(new OnSuccess() {
                @Override
                public void onSuccess(Object o) throws Throwable {
                    System.out.println(address.toString() + ":success:-----------object:" + o);
                }
            }, ec);
            futures.add(future);//任务切割 参数2为消息内容  需要与消息处理的地方类型统一
        }

        final Future<Iterable<Object>> aggregate = Futures.sequence(futures,
                ec);

        Future<A> back = aggregate.map(
                new Mapper<Iterable<Object>, A>() {
                    public A apply(Iterable<Object> coll) {
                        return result.getReturn(coll.iterator());
                    }
                }
                , ec);
        Patterns.pipe(back, ec).to(getGetter());
        return null;
    }
}
