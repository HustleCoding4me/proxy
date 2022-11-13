package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {

    /**
     * 핵심은 target용 Proxy 객체를 각각 생성해준 것이 아닌,
     * TimeInvocationHandler에 공통 기능을 담고, 공통기능을 A구현체, B구현체에 적용시킨 것이다.
     */
    @Test
    void dynamicA() {
        AInterface target = new AImpl();

        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class},
                handler);

        proxy.call();
        log.info("tagetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        BInterface targetB = new BImpl();
        TimeInvocationHandler handlerB = new TimeInvocationHandler(targetB);

        BInterface proxyB = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(),
                new Class[]{BInterface.class},
                handler);

        proxyB.call();
        log.info("tagetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

    }

}
