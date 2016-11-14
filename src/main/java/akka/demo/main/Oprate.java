package akka.demo.main;

/**
 * Created by ruancl@xkeshi.com on 16/10/26.
 */
public enum Oprate implements Cal {

    ADD{
        @Override
        public Number calculate(String a) {
            return 1;
        }
    },
    MULTIPLY{
        @Override
        public Number calculate(String a) {
            return 2;
        }
    };
}
