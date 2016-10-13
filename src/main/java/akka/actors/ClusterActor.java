package akka.actors;

import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.enter.AkkaContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ruancl@xkeshi.com on 16/9/29.
 */
public class ClusterActor extends AbstractActor {

    Cluster cluster = Cluster.get(getContext().system());


    @Override
    public void preStart() throws Exception {
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object o) throws Throwable {

        if (o instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp memberUp = (ClusterEvent.MemberUp) o;
            Member member = memberUp.member();
            System.out.println("member up :" + member);
            Set<String> roles = member.getRoles();
            for (String role : roles) {
                List<Address> addresses = AkkaContext.addressMap.get(role);
                Address address = member.address();
                if (addresses == null) {
                    addresses = new ArrayList<>();
                    addresses.add(address);
                    AkkaContext.addressMap.put(role, addresses);
                } else {
                    if (!addresses.contains(address)) {
                        addresses.add(address);
                    }
                }
            }
            System.out.println("roles:" + memberUp.member().getRoles() + " || " + member.roles());
        } else if (o instanceof ClusterEvent.MemberRemoved) {
            System.out.println("member removed :" + ((ClusterEvent.MemberRemoved) o).member());
        } else if (o instanceof ClusterEvent.UnreachableMember) {
            ClusterEvent.UnreachableMember unreachableMember = (ClusterEvent.UnreachableMember) o;
            Address address = unreachableMember.member().address();
            AkkaContext.deleteAddress(address);
            System.out.println("member unreachable :" + ((ClusterEvent.UnreachableMember) o).member());
        } else if (o instanceof ClusterEvent.MemberEvent) {
            // ignore

        } else {
            unhandled(o);
        }
    }
}
