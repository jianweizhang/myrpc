package net.zhangjw.myrpc.spring.support;

import net.zhangjw.myrpc.serever.Server;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created on 2016/11/16.
 */
public class ServiceRegistery implements InitializingBean {
    private String interfance;
    private Object providerImpl;

    public void afterPropertiesSet() throws Exception {
        Server.getInstance().getHanderMap().put(interfance, providerImpl);
    }

    public String getInterfance() {
        return interfance;
    }

    public void setInterfance(String interfance) {
        this.interfance = interfance;
    }

    public Object getProviderImpl() {
        return providerImpl;
    }

    public void setProviderImpl(Object providerImpl) {
        this.providerImpl = providerImpl;
    }
}
