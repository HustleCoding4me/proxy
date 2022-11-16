package hello.proxy.cglib.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor
{
    private final Object target;

    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long start = System.currentTimeMillis();

        /**
            CGLIB는  method Proxy를 사용하면 더 빠르다.
         */
        Object result = methodProxy.invoke(target, args);

        long end = System.currentTimeMillis();
        log.info("TimeProxy 종료");
        log.info("TimeProxy 실행 시간: {}ms", end - start);
        return result;
    }
}
