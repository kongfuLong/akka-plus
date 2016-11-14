package akka.actors;

import akka.actor.UntypedActor;
import akka.msg.RpcEntity;
import akka.msg.RpcResult;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 */
public class RpcServerActor extends UntypedActor {

    private WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();

    @Override
    public void onReceive(Object o) throws Throwable {
        System.out.println("收到消息:" + o);
        if (o instanceof RpcEntity) {
            RpcEntity rpcEntity = (RpcEntity) o;
            Object[] params = rpcEntity.getParam();
            Object bean = wac.getBean(rpcEntity.getInterfaceName());
            Class<?>[] paramerTypes = new Class<?>[]{};
            List<Class> paramType = new ArrayList<>();
            if (bean != null) {
                Method method = null;
                if (params != null) {
                    for (Object param : params) {
                        paramType.add(param.getClass());
                    }
                    method = bean.getClass().getMethod(rpcEntity.getMethod(), paramType.toArray(paramerTypes));
                } else {
                    method = bean.getClass().getMethod(rpcEntity.getMethod());
                }
                Object result = method.invoke(bean, params);
                if (result != null) {
                    getSender().tell(new RpcResult(result), getSelf());
                }
            }
        } else {
            unhandled(o);
        }
    }
}
