package akka.actors;

/**
 * Created by ruancl@xkeshi.com on 16/9/30.
 */
public class DistributeAskActor extends AbstractActor {

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof Double) {
            System.out.println("最终统计获得:" + o);
        } else {
            unhandled(o);
        }
    }
}
