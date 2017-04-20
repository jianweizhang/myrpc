package net.zhangjw.myrpc.spring.support;

import com.google.common.reflect.Reflection;
import net.zhangjw.myrpc.client.InterfaceInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created on 2016/11/15.
 */
public class ProxyFactoryBean<T> implements FactoryBean<T>, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(ProxyFactoryBean.class);

    private Class<T> interfaceClass;

    public void afterPropertiesSet() throws Exception {
        logger.info("ProxyFactoryBean afterPropertiesSet");
    }

    public T getObject() throws Exception {
        return Reflection.newProxy(interfaceClass, new InterfaceInvocationHandler());
    }

    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public boolean isSingleton() {
        return true;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}
