package akka.enter;

import akka.actor.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ruancl@xkeshi.com on 16/11/9.
 */
public class AddressContex {


    private List<Address> addresses = new ArrayList<>();

    private Map<String,List<ActorRefMap>> map = new HashMap<>();


    public void addAddress(Address address){
        synchronized (addresses) {
            addresses.add(address);
        }
    }

    public void deleteAddress(Address address){
        synchronized (addresses) {
            addresses.remove(address);
            deleteActorRef(address);
        }
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    private void deleteActorRef(Address address){
        synchronized (map){
            for(String key : map.keySet()){
                List<ActorRefMap> actorRefMaps = map.get(key);
                map.put(key,actorRefMaps.stream().filter(o-> !o.removeAdd(address)).collect(Collectors.toList()));
            }
        }
    }

    public List<ActorRefMap> getActorRefs(String key){
        return map.get(key);
    }


    public synchronized void addMap(String key,List<ActorRefMap> actorRefMaps){
        if(map.containsKey(key)){
            throw new IllegalArgumentException("请勿重复生成消息任务");
        }
        synchronized (map) {
            map.put(key, actorRefMaps);
        }
    }
}
