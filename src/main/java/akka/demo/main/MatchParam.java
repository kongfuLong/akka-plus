package akka.demo.main;

import akka.enums.PoolType;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruancl@xkeshi.com on 16/10/26.
 */
public class MatchParam<T> {


    public void setName(T param){
        System.out.println(param);
    }

    public static void main(String[] args) {
        Date date = new Date(1477473100350l);
        A a = new A(date);

        System.out.println(a.getNow().getTime());
        a.update();
        System.out.println(a.getNow().getTime());
    }
    static class A{
        final Date now;


        public A(Date now) {
            this.now = now;
        }

        public void update(){
            now.setTime(1477473100888l);
        }

        public Date getNow() {
            return now;
        }
    }
}
