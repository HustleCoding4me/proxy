package hello.proxy.config.v2_dynamicProxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogTraceFilterHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    /**
     *
     * method 이름 패턴에 따라 작동시킬지 결정
     */

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //메서드 이름 필터
        String methodName = method.getName();
        /**
         * 특정 메서드명 패턴에 매치되지 않으면 Log를 찍지 않고( 별다른 공통 소스 없이)
         * 원본 메서드를 작동시킨다.
         */
        //save, request, reque*, *est
        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            return method.invoke(target, args);
        }
        TraceStatus status = logTrace.begin(method.getDeclaringClass() + "." + methodName + "()");
        Object result = method.invoke(target, args);
        logTrace.end(status);

        return result;
    }
}
