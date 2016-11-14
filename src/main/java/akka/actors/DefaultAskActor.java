package akka.actors;

import akka.actor.*;
import akka.enter.ActorRefMap;
import akka.enter.AddressContex;
import akka.msg.Constant;
import akka.msg.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruancl@xkeshi.com on 16/9/30.
 */
public class DefaultAskActor extends UntypedActor {

    private List<ActorRefMap> actorRefs;


    public DefaultAskActor(AddressContex addressContex, String path) {
        this.actorRefs = addressContex.getActorRefs(path);
        if(actorRefs == null){
            actorRefs = new ArrayList<>();
            addressContex.addMap(path,actorRefs);
            List<Address> addresses = addressContex.getAddresses();
            if(addresses.size()==0){
                throw new NullPointerException("集群中没有可用地址,集群离线 or 未开启集群监听");
            }
            addresses.forEach(add->
                    getContext().actorSelection(add.toString()+path).tell(new Identify(add),getSelf())
            );
        }
    }

    public DefaultAskActor() {
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if(o instanceof Message){

        }else if(o instanceof ActorIdentity){
            ActorIdentity identity = (ActorIdentity) o;
            if (identity.correlationId() instanceof Address){
                Address address = (Address) identity.correlationId();
                ActorRef actorRef = identity.getRef();
                if(actorRef!=null){
                    actorRefs.add(new ActorRefMap(address,actorRef));
                }
            }
        }else {
            unhandled(o);
        }
    }
}
