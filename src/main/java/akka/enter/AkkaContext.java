package akka.enter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.actors.ClusterActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ruancl@xkeshi.com on 16/10/8.
 */
public class AkkaContext {

    private static final String CLUSTER_NAME = "clusterListener";
    /**
     * 维护一个集群的地址列表
     */
    public static ConcurrentHashMap<String, List<Address>> addressMap = new ConcurrentHashMap<>();
    private static HashMap<Integer, AkSystem> systems = new HashMap<>();
    private static HashMap<Class, Object> rpcBeans = new HashMap<>();

    public static void addBean(Class clazz, Object bean) {
        rpcBeans.put(clazz, bean);
    }

    public static Object getBean(Class clazz) {
        return rpcBeans.get(clazz);
    }

    public static void deleteAddress(Address address) {
        for (String key : addressMap.keySet()) {
            List<Address> list = addressMap.get(key);
            if (list.contains(address)) {
                list.remove(address);
            }
        }
    }

    /**
     * @param port
     * @param systemName 与配置文件地址里面的名字一致
     * @param roleName   角色组名
     * @return
     */
    public static AkSystem createSystem(int port, String systemName, String roleName,boolean withClusterListener) {
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port)
                .withFallback(
                        ConfigFactory
                                .parseString("akka.cluster.roles = [" + roleName + "]"))
                .withFallback(
                        ConfigFactory.load());
        ActorSystem system = ActorSystem.create(systemName, config);
        //每创建一个system则为其建立一个集群状态监听
        ActorRef cluster = null;
        if(withClusterListener){
            cluster = system.actorOf(Props.create(ClusterActor.class), CLUSTER_NAME);
        }
        AkSystem akSystem = new AkSystem(system,cluster);
        akSystem.executeOnMemberUp();//在节点监听还未成功建立前阻塞消息
        systems.put(port, akSystem);
        return akSystem;
    }


}
