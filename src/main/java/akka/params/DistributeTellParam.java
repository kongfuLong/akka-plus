package akka.params;

import akka.routing.Pool;

/**
 * Created by ruancl@xkeshi.com on 16/10/11.
 */
public class DistributeTellParam extends BaseParam {




    private Object param;

    /**
     * 服务端执行的actor路径
     */
    private Iterable<String> routeesPaths;



    /**
     * 路由自定义
     */
    private Pool local;

    public DistributeTellParam(Object param, String roleName, String taskName, Iterable<String> routeesPaths) {
        this.roleName = roleName;
        this.taskName = taskName;
        this.param = param;
        this.routeesPaths = routeesPaths;
    }

    public DistributeTellParam(Object param, String roleName, String taskName,Pool local,Iterable<String> routeesPaths) {
        this.roleName = roleName;
        this.taskName = taskName;
        this.param = param;
        this.local = local;
        this.routeesPaths = routeesPaths;
    }

    public Pool getLocal() {
        return local;
    }

    public void setLocal(Pool local) {
        this.local = local;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }


    public Iterable<String> getRouteesPaths() {
        return routeesPaths;
    }
}
