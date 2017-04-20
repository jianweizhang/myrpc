package net.zhangjw.myrpc.client;


import net.zhangjw.myrpc.common.MessageRequest;
import net.zhangjw.myrpc.common.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageCallBack_old {

    private MessageRequest request;
    private MessageResponse response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    private Logger logger = LoggerFactory.getLogger(MessageCallBack_old.class);

    public MessageCallBack_old(MessageRequest request) {
        this.request = request;
    }

    public Object start() throws InterruptedException {
        try {
            lock.lock();
            finish.await(10 * 1000, TimeUnit.MILLISECONDS);
            logger.info("after await: " + Thread.currentThread().getName());
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } finally {
//            Client.getInstance().getMessageSendHandler().removeFromCallBackMap(request.getMessageId());
            lock.unlock();
        }
    }

    public void over(MessageResponse reponse) {
        try {
            lock.lock();
            finish.signal();
            this.response = reponse;
            logger.info("after response: " + Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }
}
