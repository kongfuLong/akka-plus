package akka.enter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actors.AbstractActor;
import akka.actors.DistributeAskActor;
import akka.actors.DistributeTellActor;
import akka.cluster.Cluster;
import akka.cluster.metrics.AdaptiveLoadBalancingGroup;
import akka.cluster.metrics.HeapMetricsSelector;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.params.BaseParam;
import akka.params.DistributeAskParam;
import akka.params.DistributeTellParam;
import akka.routing.Pool;
import akka.routing.RoundRobinPool;
import akka.rpc.ProxyIntecept;
import org.springframework.cglib.proxy.Enhancer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ruancl@xkeshi.com on 16/10/9.
 */
public class AkSystem {

    private final ActorSystem system;

    private ActorRef cluster;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private final int MAX_THREAD_COUNT = 100;


    private ConcurrentHashMap<Class, Object> beans = new ConcurrentHashMap<>();

    public AkSystem(ActorSystem system, ActorRef cluster) {
        this.system = system;
        this.cluster = cluster;
    }

    public <T> T proxy(Class<T> clazz) {
        if (countDownLatch.getCount()==1) {
            try {
                countDownLatch.await();//等待直到加入集群完毕信号
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Object bean = beans.get(clazz);
        if (bean != null) {
            return (T) bean;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new ProxyIntecept(clazz, system));
        bean = enhancer.create();
        beans.put(clazz, bean);
        return (T) bean;
    }

    public void deleteErrorBean() {

    }

    /**
     * 本地的actor注册
     *
     * @param clazz
     * @param objects 构造函数参数
     * @return
     */
    /*public AkSystem register(Class<? extends AbstractActor> clazz, Object... objects) {
        ActorRef ref = system.actorOf(new RoundRobinPool(1).props(Props.create(clazz, objects)), clazz.getName());
        System.out.println("register actor:"+ref.path());
        return this;
    }

    public AkSystem register(Class<? extends AbstractActor> clazz,Pool pool, Object... objects) {
        ActorRef ref = system.actorOf(pool.props(Props.create(clazz, objects)), clazz.getName());
        System.out.println("register actor:"+ref.path());
        return this;
    }*/

    public AkSystem register(Class<? extends AbstractActor> clazz, Pool pool, String name, Object... objects){
        ActorRef ref = system.actorOf(pool.props(Props.create(clazz, objects)), name);
        System.out.println("register actor:"+ref.path());
        return this;
    }

    public AkSystem register(Class<? extends AbstractActor> clazz, String name, Object... objects){
        ActorRef ref = system.actorOf(new RoundRobinPool(3).props(Props.create(clazz, objects)), name);
        System.out.println("register actor:"+ref.path());
        return this;
    }

    public void executeOnMemberUp() {
        Cluster.get(system).registerOnMemberUp(() -> {
            countDownLatch.countDown();
        });
        // new RequestStrategy(system, cluster, baseParam));
    }


    /**
     * 创建一个信息发射器 以便于相同信息发送的复用(基于taskname区分)
     *
     * @param baseParam 使用baseParam子类 不同子类对应不同的信息类型
     */
    public MsgSenderWrapper senderCreate(BaseParam baseParam) {
        if (baseParam instanceof DistributeAskParam) {
            return new AskSenderWrapper(null,system.actorOf(Props.create(DistributeAskActor.class), baseParam.getTaskName()), system,countDownLatch);
        } /*else if (baseParam instanceof DistributeTellParam) {
            DistributeTellParam distributeTellParam = (DistributeTellParam) baseParam;
            Class excutorClass = distributeTellParam.getExecuter();
            String roleName = distributeTellParam.getRoleName();
            Pool pool = distributeTellParam.getLocal();
            if (pool == null) {
                pool = new RoundRobinPool(5);
            }
            List<Address> addresses = AkkaContext.addressMap.get(roleName);
            Address[] arr = addresses.toArray(new Address[addresses.size()]);
            final ActorRef ref = getSystem().actorOf(new RemoteRouterConfig(pool, arr).props(Props.create(excutorClass)),baseParam.getTaskName());
            return new TellSenderWrapper(system.actorOf(Props.create(DistributeTellActor.class)),ref, system);
        }*/
        else if (baseParam instanceof DistributeTellParam) {
            DistributeTellParam distributeTellParam = (DistributeTellParam) baseParam;
            String roleName = distributeTellParam.getRoleName();
            Iterable<String> routeesPaths = distributeTellParam.getRouteesPaths();
            ClusterRouterGroup clusterRouterGroup = new ClusterRouterGroup(
                    new AdaptiveLoadBalancingGroup(
                            HeapMetricsSelector.getInstance(),
                            routeesPaths),
                    new ClusterRouterGroupSettings(MAX_THREAD_COUNT,routeesPaths,
                            false,roleName ));
            final ActorRef ref = getSystem().actorOf(clusterRouterGroup.props());
            return new TellSenderWrapper(system.actorOf(Props.create(DistributeTellActor.class)),ref, system,countDownLatch);
        }
        return null;
    }

    /**
     * 自定义集群发现器实现
     *
     * @param clazz
     * @param objects
     * @return
     */
    public AkSystem overrideCluster(Class clazz, Object... objects) {
        // this.system.stop(this.cluster);
        this.cluster = system.actorOf(Props.create(clazz, objects), clazz.getSimpleName());
        return this;
    }

    public ActorSystem getSystem() {
        return system;
    }

    public ActorRef getCluster() {
        return cluster;
    }
}
