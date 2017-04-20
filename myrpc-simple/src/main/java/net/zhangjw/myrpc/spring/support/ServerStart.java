package net.zhangjw.myrpc.spring.support;

import net.zhangjw.myrpc.serever.Server;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created on 2016/11/15.
 */
public class ServerStart implements InitializingBean{
    private String address;

    public void afterPropertiesSet() throws Exception {
        Server.getInstance().start(address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
