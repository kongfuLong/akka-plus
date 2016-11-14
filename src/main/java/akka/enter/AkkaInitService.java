package akka.enter;

import akka.actor.ActorSystem;
import akka.actors.AbstractActor;
import akka.anntations.Actor;
import akka.msg.Constant;
import akka.params.RegisterBean;
import akka.rpc.ProxyIntecept;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by ruancl@xkeshi.com on 16/10/19.
 */
@Component
public class AkkaInitService {

    private static final Logger logger = LoggerFactory.getLogger(AkkaInitService.class);



    private AkSystem akSystem;


    @PostConstruct
    public void init() {
        this.akSystem = createSystem(Constant.SYSTEM_NAME);
        scanPackage().ifPresent(list->
            list.forEach(bean->{
                this.akSystem.register(bean);
                logger.info("注册actor:{}成功", bean.getName());
            }));
    }

    private Optional<List<RegisterBean>> scanPackage() {
        List<RegisterBean> classes = new ArrayList();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourcePatternResolver.getResources(Constant.SCAN_PATH_PATTEN);

            MetadataReaderFactory metadataReaderFactory =
                    new CachingMetadataReaderFactory(new PathMatchingResourcePatternResolver());
            AnnotationTypeFilter filter = new AnnotationTypeFilter(Actor.class);
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    continue;
                }
                //将标记actor注解的类收集 注册到akkasystem
                String className = metadataReader.getClassMetadata().getClassName();
                Class clazz = Class.forName(className);
                Actor actor = (Actor) clazz.getAnnotation(Actor.class);
                if (clazz.getSuperclass() != AbstractActor.class) {
                    throw new IllegalArgumentException("无效的actor继承类型", new IllegalArgumentException());
                }
                classes.add(new RegisterBean(clazz, actor.name(), actor.pool().getPool(actor.number())));
            }
        } catch (IOException e) {
            logger.error("actor类资源读取io异常");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.error("扫描获取的类路径异常,找不对对应class");
            e.printStackTrace();
        }
        return Optional.ofNullable(classes);
    }


    public AkSystem createSystem(String systemName) {
        Config config = ConfigFactory.load();
        ActorSystem system = ActorSystem.create(systemName, config);
        AkSystem akSystem = new AkSystem(system,Constant.WITH_CLUSTER,Constant.WITH_RPC_PROVIDER);
        akSystem.executeOnMemberUp();//在节点监听还未成功建立前阻塞消息
        logger.info("actor system创建完毕");
        return akSystem;
    }

    public AkSystem getAkSystem() {
        return akSystem;
    }

    public Object getBean(Class clazzInterface){
        return akSystem.getBean(clazzInterface);
    }
}
