package net.zhangjw.myrpc.spring.support;

import net.zhangjw.myrpc.client.Client;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created on 2016/11/15.
 */
public class ClientStart implements InitializingBean{
    private String registryServerAddresses;

    public void afterPropertiesSet() throws Exception {
        Client.getInstance().start(registryServerAddresses);
    }

    public String getRegistryServerAddresses() {
        return registryServerAddresses;
    }

    public void setRegistryServerAddresses(String registryServerAddresses) {
        this.registryServerAddresses = registryServerAddresses;
    }
}
