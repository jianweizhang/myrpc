package net.zhangjw.myrpc.client;


import net.zhangjw.myrpc.common.MessageRequest;
import net.zhangjw.myrpc.common.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MessageCallBack {

    private MessageRequest request;
    private MessageResponse response;

    private Exchanger<MessageResponse> exchanger = new Exchanger<>();

    private Logger logger = LoggerFactory.getLogger(MessageCallBack.class);

    public MessageCallBack(MessageRequest request) {
        this.request = request;
    }

    public Object start() throws InterruptedException {
        try {
            response = exchanger.exchange(null, 10*1000, TimeUnit.MILLISECONDS);
            if (this.response != null) {
                return response.getResult();
            } else {
                return null;
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        } finally {
            Client.getInstance().getMessageSendHandler().removeFromCallBackMap(request.getMessageId());
        }
    }

    public void over(MessageResponse reponse) {
        try {
            exchanger.exchange(reponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
