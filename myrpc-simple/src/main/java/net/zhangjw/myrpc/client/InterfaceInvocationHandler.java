package net.zhangjw.myrpc.client;

import net.zhangjw.myrpc.common.MessageRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created on 2016/11/11.
 */
public class InterfaceInvocationHandler implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParameters(args);

        MessageSendHandler handler = Client.getInstance().getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);

        return callBack.start();
    }
}
