package akka.params;

/**
 * Created by ruancl@xkeshi.com on 16/10/8.
 * 全双工
 */
public class DistributeAskParam<A, B> extends BaseParam {


    /**
     * 任务响应超时时间  毫秒
     */
    private final Long timeUp;

    /**
     * 结果汇总处理功能实现
     */
    private final Result<A> result;

    /**
     * 任务分割实现
     */
    private final TaskCut<B> taskCut;


    /**
     * 执行任务类class
     */
    private final Class excuterClazz;


    public DistributeAskParam(Builder builder) {
        this.roleName = builder.roleName;
        this.result = builder.result;
        this.taskCut = builder.taskCut;
        this.taskName = builder.taskName;
        this.excuterClazz = builder.excuterClazz;
        this.timeUp = builder.timeUp;
    }

    public Long getTimeUp() {
        return timeUp;
    }

    public Result<A> getResult() {
        return result;
    }

    public TaskCut<B> getTaskCut() {
        return taskCut;
    }

    public Class getExcuterClazz() {
        return excuterClazz;
    }

    public static class Builder<A, B> {
        private final String roleName;

        private final Result<A> result;

        private final String taskName;

        private final Class excuterClazz;

        private Long timeUp = 5000l;

        private TaskCut<B> taskCut;

        public Builder(String roleName, Result<A> result, String taskName, Class excuterClazz) {
            this.roleName = roleName;
            this.result = result;
            this.taskName = taskName;
            this.excuterClazz = excuterClazz;
        }

        public Builder setCut(TaskCut<B> taskCut) {
            this.taskCut = taskCut;
            return this;
        }

        public Builder setTimeLimit(Long millionSecond) {
            this.timeUp = millionSecond;
            return this;
        }

        public DistributeAskParam build() {
            return new DistributeAskParam(this);
        }
    }
}
