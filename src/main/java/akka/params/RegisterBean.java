package akka.params;

import akka.actors.AbstractActor;
import akka.routing.Pool;
import akka.routing.RoundRobinPool;

/**
 * Created by ruancl@xkeshi.com on 16/10/20.
 */
public class RegisterBean<T extends AbstractActor> {

    private final Class<T> tClass;

    private final String name;

    private Pool pool = new RoundRobinPool(1);


    public RegisterBean(Class<T> tClass, String name) {
        this.tClass = tClass;
        this.name = name;
    }

    public RegisterBean(Class<T> tClass, String name, Pool pool) {
        this.tClass = tClass;
        this.name = name;
        this.pool = pool;
    }

    public Class<T> gettClass() {
        return tClass;
    }

    public String getName() {
        return name;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}
