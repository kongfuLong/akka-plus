package akka.params;

import akka.actor.Address;


/**
 * Created by ruancl@xkeshi.com on 16/9/30.
 * <p>
 * 任务分割策略  需自己实现  返回对象为消息对象  汇总使用   需要序列化
 */
public interface TaskCut<T> {

    T cut(final int serverSize, final Address address, final int index);
}
