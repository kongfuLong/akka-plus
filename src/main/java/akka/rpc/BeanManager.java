package akka.rpc;

import akka.anntations.AkkaRpcRef;
import akka.enter.AkkaInitService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;

/**
 * Created by ruancl@xkeshi.com on 16/11/10.
 */
@Service
public class BeanManager extends InstantiationAwareBeanPostProcessorAdapter {

    @Autowired
    private AkkaInitService akkaInitService;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Annotation[] annotations = bean.getClass().getAnnotations();
        if(annotations!=null && annotations.length>0){
            for(Annotation annotation : annotations){
                Class annotationClazz = annotation.getClass();
                if(annotationClazz == AkkaRpcRef.class){
                    return akkaInitService.getBean(bean.getClass().getInterfaces()[0]);
                }
            }
        }

        return bean;
    }
}
