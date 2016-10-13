package akka.params;


import java.util.Iterator;

/**
 * Created by ruancl@xkeshi.com on 16/9/30.
 * <p>
 * 任务结果统一返回处理  需实现该接口自定义返回内容
 */
public interface Result<T> {
    T getReturn(final Iterator<Object> it);
}
