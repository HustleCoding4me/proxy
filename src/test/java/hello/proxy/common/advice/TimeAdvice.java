package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class TimeAdvice implements MethodInterceptor {
    /**
     *
     * @param invocation the method invocation joinpoint
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long start = System.currentTimeMillis();

        /**
         더이상 target을 넣지 않고, invocation에 이미 담겨져있다.
         target을 찾아서 원본을 실행해준다,
         */
       // Object result = methodProxy.invoke(target, args);
        Object result = invocation.proceed();

        long end = System.currentTimeMillis();
        log.info("TimeProxy 종료");
        log.info("TimeProxy 실행 시간: {}ms", end - start);
        return result;
    }
}
