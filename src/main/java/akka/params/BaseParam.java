package akka.params;

/**
 * Created by ruancl@xkeshi.com on 16/10/8.
 */
public abstract class BaseParam {

    /**
     * 工作组角色名
     */
    String roleName;

    /**
     * 任务名
     */
    String taskName;

    public String getRoleName() {
        return roleName;
    }

    public String getTaskName() {
        return taskName;
    }
}
