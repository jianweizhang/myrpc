package net.zhangjw.myrpc.test.client;

import net.zhangjw.myrpc.test.interfance.User;
import net.zhangjw.myrpc.test.interfance.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created on 2016/11/16.
 */
public class Test implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(Test.class);


    private UserService userService;
    private TaskExecutor taskExecutor;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("start");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    taskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            long l = System.currentTimeMillis();
                            for (int i = 0; i < 1000; i++) {
                                User test = userService.test(i + "");
                                if(!test.getPassword().equals(String.valueOf(i))){
                                    logger.info("dif");
                                }
                            }
                            long l1 = System.currentTimeMillis();
                            logger.info((l1 - l) + "");
                        }
                    });
                }
            }
        }, 5000, 30000);
    }
}
