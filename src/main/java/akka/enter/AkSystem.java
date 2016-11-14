package akka.enter;

import akka.actor.*;
import akka.actors.ClusterActor;
import akka.actors.DefaultAskActor;
import akka.actors.RpcServerActor;
import akka.cluster.Cluster;
import akka.cluster.metrics.AdaptiveLoadBalancingGroup;
import akka.cluster.metrics.HeapMetricsSelector;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.msg.Constant;
import akka.msg.Message;
import akka.params.DefaultAskHandle;
import akka.params.RegisterBean;
import akka.remote.RemoteScope;
import akka.rpc.ProxyIntecept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by ruancl@xkeshi.com on 16/10/9.
 */
public class AkSystem {

    private static final Logger logger = LoggerFactory.getLogger(AkSystem.class);
    private final ActorSystem system;
    private final int MAX_THREAD_COUNT = 100;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private AddressContex addressContex = new AddressContex();

    private ActorRef rpcActor;

    private ActorRef clusterActor;




    public AkSystem(ActorSystem system) {
        this.system = system;
    }

    /**
     *
     * @param system
     * @param withCluster 启动集群监听
     */
    public AkSystem(ActorSystem system,Boolean withCluster,Boolean rpcServerProvider) {
        this.system = system;
        if (withCluster){
            clusterActor = system.actorOf(Props.create(ClusterActor.class,addressContex));
        }
        if (withCluster){
            system.actorOf(Props.create(RpcServerActor.class));
        }
    }

    public Object getBean(Class clazzInterface){
        //对象可以交于spring托管
        if(rpcActor==null){
            rpcActor = getRouterActor(Arrays.asList(Constant.RPCPATH));
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new ProxyIntecept(clazzInterface,rpcActor));
        enhancer.setSuperclass(clazzInterface);
        return  enhancer.create();
    }


    /**
     * actor 注册
     * @param registerBean
     * @return
     */
    public AkSystem register(RegisterBean registerBean) {
        ActorRef ref = system.actorOf(registerBean.getPool().props(Props.create(registerBean.gettClass())), registerBean.getName());
        logger.info("register actor:{}", ref.path());
        return this;
    }


    /**
     * 集群加入成功后触发
     */
    public void executeOnMemberUp() {
        Cluster.get(system).registerOnMemberUp(() ->
            countDownLatch.countDown()
        );
    }

    private ActorRef getRouterActor(Iterable<String> routeesPaths){
        ClusterRouterGroup clusterRouterGroup = new ClusterRouterGroup(
                new AdaptiveLoadBalancingGroup(
                        HeapMetricsSelector.getInstance(),
                        routeesPaths),
                new ClusterRouterGroupSettings(MAX_THREAD_COUNT, routeesPaths,
                        false, Constant.ROLE_NAME));
        return getSystem().actorOf(clusterRouterGroup.props());
    }

    /**
     * 创建一个信息发射器 以便于相同信息发送的复用
     * 路由消息(tell模式)
     */
    public MsgSenderWrapper tellRouter(final String path) {
        return new TellSenderWrapper(null,
                Arrays.asList(getRouterActor(Arrays.asList(path))),
                system,
                countDownLatch);
    }


    /**
     * 广播消息(tell模式)
     * @param path
     * @return
     */
    public MsgSenderWrapper tellBroadCast(final String path) {
        return new TellSenderWrapper(system.actorOf(Props.create(DefaultAskActor.class,addressContex,path)),
                addressContex.getActorRefs(path).stream().map(ActorRefMap::getV).collect(Collectors.toList()),
                system,
                countDownLatch);
    }


    /**
     * 路由消息(ask模式)
     * @param path
     * @return
     */
    public MsgSenderWrapper AskRouter(final String path) {

        return new AskSenderWrapper<>(system.actorOf(Props.create(DefaultAskActor.class)),
                Arrays.asList(getRouterActor(Arrays.asList(path))),
                system,
                countDownLatch,
                new DefaultAskHandle());
    }


    /**
     * 使用该功能发消息必须要先启动集群监听@AkSystem
     *
     * 广播消息(ask模式)
     * @param path
     * @return
     */
    public MsgSenderWrapper askBroadCast(final String path){
        return new AskSenderWrapper<>(system.actorOf(Props.create(DefaultAskActor.class,addressContex,path)),
                addressContex.getActorRefs(path).stream().map(ActorRefMap::getV).collect(Collectors.toList()),
                system,
                countDownLatch,
                new DefaultAskHandle());
    }


    public ActorSystem getSystem() {
        return system;
    }

}
