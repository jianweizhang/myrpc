package net.zhangjw.myrpc.test.server;

import net.zhangjw.myrpc.test.interfance.UserService;
import net.zhangjw.myrpc.test.interfance.User;

/**
 * Created on 2016/11/11.
 */
public class UserServiceImpl implements UserService {

    public User test(String password) {
        return new User("zjw", password);
    }
}
