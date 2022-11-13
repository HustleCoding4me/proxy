package hello.proxy.config.v2_dynamicProxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogTraceHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;


    public LogTraceHandler(Object target, LogTrace logTrace) {
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //target 호출
        String methodMetaStr = method.getDeclaringClass().getSimpleName() + "." + method.getName()
                + "()";

        TraceStatus status = logTrace.begin(methodMetaStr);

        Object result = method.invoke(target, args);


        logTrace.end(status);
        return result;
    }
}
