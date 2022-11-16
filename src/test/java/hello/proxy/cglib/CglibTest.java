package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {

    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        /**
         * 상속 받아서 Proxy를 만들기 때문에 캐스팅이 가능하다.
         */
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService)enhancer.create();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        /**
         * INFO hello.proxy.cglib.CglibTest - targetClass=class hello.proxy.common.service
         * .ConcreteService
         * INFO hello.proxy.cglib.CglibTest - proxyClass=class hello.proxy.common.service
         * .ConcreteService$$EnhancerByCGLIB$$25d6b0e3
         */

    }
}
