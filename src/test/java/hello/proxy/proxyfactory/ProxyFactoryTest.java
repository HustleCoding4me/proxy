package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        /**
         * 실제 target으로 ProxyFactory 생성,
         * Advice를 추가하여 Proxy 기능을 담는다.
         */
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //proxy 작동
        proxy.save();

        /**
         * `AopUtils`
         * ProxyFactory로 Proxy를 생성했을때 사용 가능하다.
         */

        //타겟이 Proxy객체인지 확인
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        //타겟이 Proxy객체이면서 인터페이스를 구현한 Proxy인지 확인
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        //타겟이 Proxy객체이면서 클래스를 상속받은 Proxy인지 확인
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isFalse();

    }

    @Test
    @DisplayName("CLASS만 있으면 CGLIB 사용")
    void concreteProxy() {
        /**
         * 실제 target으로 ProxyFactory 생성,
         * Advice를 추가하여 Proxy 기능을 담는다.
         */
        ConcreteService target = new ConcreteService();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());

        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();

        //proxy 작동
        proxy.call();

        /**
         * `AopUtils`
         * ProxyFactory로 Proxy를 생성했을때 사용 가능하다.
         */

        //타겟이 Proxy객체인지 확인
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        //타겟이 Proxy객체이면서 인터페이스를 구현한 Proxy인지 확인
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        //타겟이 Proxy객체이면서 클래스를 상속받은 Proxy인지 확인
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue();

    }


    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void interfaceButConcreteProxy() {
        /**
         * 실제 target으로 ProxyFactory 생성,
         * Advice를 추가하여 Proxy 기능을 담는다.
         */
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        //CGLIB를 기반으로 사용하란 뜻
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        //proxy 작동
        proxy.save();

        /**
         * `AopUtils`
         * ProxyFactory로 Proxy를 생성했을때 사용 가능하다.
         */

        //타겟이 Proxy객체인지 확인
        Assertions.assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        //타겟이 Proxy객체이면서 인터페이스를 구현한 Proxy인지 확인
        Assertions.assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        //타겟이 Proxy객체이면서 클래스를 상속받은 Proxy인지 확인
        Assertions.assertThat(AopUtils.isCglibProxy(proxy)).isTrue();

    }
}
